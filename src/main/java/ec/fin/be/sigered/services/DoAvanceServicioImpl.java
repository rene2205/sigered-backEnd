package ec.fin.be.sigered.services;

import java.io.File;
import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.*;

import ec.fin.be.comunes.excepciones.ServiceException;
import ec.fin.be.sigered.UtilPropertiesArquetipo;
import ec.fin.be.sigered.dto.AvanceDto;
import ec.fin.be.sigered.dto.DocumentoDto;
import ec.fin.be.sigered.primary.entity.DoAvance;
import ec.fin.be.sigered.primary.entity.DoDelegado;
import ec.fin.be.sigered.primary.entity.DoDocumento;
import ec.fin.be.sigered.primary.entity.DoGerencia;
import ec.fin.be.sigered.primary.entity.DoInstitucion;
import ec.fin.be.sigered.primary.entity.DoSolicitud;
import ec.fin.be.sigered.logger.LoggerSigered;
import ec.fin.be.sigered.primary.repository.DoAvanceRepositorio;
import ec.fin.be.sigered.primary.repository.DoDelegadoRepositorio;
import ec.fin.be.sigered.primary.repository.DoSolicitudRepositorio;
import ec.fin.be.sigered.utils.Constantes;

@Service
public class DoAvanceServicioImpl implements DoAvanceServicio {
    private static final Logger LOG = LoggerSigered.getLogger(DoAvanceServicioImpl.class);
    
    @Autowired
    private DoAvanceRepositorio repositorioAvance;
    @Autowired
    private DoDelegadoRepositorio repositorioDelegado;
    @Autowired
    private DoSolicitudRepositorio repositorioSolicitud;
    @Autowired
    private DoDocumentoServicio servicioDocumento;
    @Autowired
    private DoSolicitudServicio servicioSolicitud;

    @Override
    public List<AvanceDto> obtenerAvances(int idSolicitud) throws ServiceException {
        LOG.info("Inicio obtenerAvances - idSolicitud: " + idSolicitud);

        try {
            List<DoAvance> lista;

            if(idSolicitud > 0) {
                lista = repositorioAvance.findBySolicitudId(idSolicitud);
            } else {
                lista = repositorioAvance.findAll();
            }

            LOG.info("Total avances obtenidas: " + lista.size());

            return lista.stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());

        } catch (Exception ex) {
            LOG.error("Error en obtenerAvances", ex);
            throw new ServiceException(ex);
        }
    }

    @Override
    public AvanceDto obtenerAvance(int id) throws ServiceException {
        LOG.info("Inicio obtenerAvance - id: " + id);

        try {
            DoAvance entity = repositorioAvance.findById(id);
            if (entity == null) {
                LOG.warn("No se encontró avance con id: " + id);
                return null;
            }

            return toDto(entity);
        } catch (Exception ex) {
            LOG.error("Error en obtenerAvance - id: " + id, ex);
            throw new ServiceException(ex);
        }
    }

    @Override
    public int guardarAvance(AvanceDto avanceDto, String usuario) throws ServiceException {
        LOG.info("Inicio guardarAvance - usuario: " + usuario + ", id: " + avanceDto.getId());

        try {
            
            DoAvance entityAvance;

            if (avanceDto.getId() != null && avanceDto.getId() > 0) {
                LOG.debug("Actualizando avance - id: " + avanceDto.getId());

                entityAvance = validarYObtenerAvance(avanceDto.getId());
                entityAvance.setFechaModificacion(LocalDateTime.now());
                entityAvance.setUsuarioModificacion(usuario);
            } else {
                LOG.debug("Creando nuevo avance para la Solicitud: " + avanceDto.getIdSolicitud() + " - delegado: " + avanceDto.getIdDelegado());

                entityAvance = new DoAvance();
                entityAvance.setFechaCreacion(LocalDateTime.now());
                entityAvance.setUsuarioCreacion(usuario);
            }

            // mapping DTO → Entity
            entityAvance.setTipo(avanceDto.getTipo());
            entityAvance.setDiasProrroga(avanceDto.getDiasProrroga());
            entityAvance.setComentario(avanceDto.getComentario());
            entityAvance.setDelegado(validarYObtenerDelegado(avanceDto.getIdDelegado()));
            entityAvance.setSolicitud(validarYObtenerSolicitud(avanceDto.getIdSolicitud()));
            entityAvance.setEstado(Constantes.ESTADO_ACTIVO);            
            entityAvance = repositorioAvance.save(entityAvance);

            LOG.info("avance guardada correctamente - id: " + entityAvance.getId());
            return entityAvance.getId();

        } catch (Exception ex) {
            LOG.error("Error en guardarAvance - usuario: " + usuario, ex);
            throw new ServiceException(ex);
        }
    }

    
    @Transactional
    @Override
    public int guardarAvanceConDocumento(AvanceDto avance, MultipartFile multipartFile, String usuario) throws ServiceException {
        LOG.info("Inicio guardarAvanceConDocumento - usuario: " + usuario);
        
        try {
            // validar archivo fue seleccionado
            if (multipartFile == null || multipartFile.isEmpty()) {
                String mensaje = "Debe seleccionar un archivo.";
                LOG.error(mensaje);
                throw new InvalidParameterException(mensaje);
            }

            // obtener parametros del archivo properties
            UtilPropertiesArquetipo propFile = UtilPropertiesArquetipo.getIntances();
            String serverPath = propFile.obtenerParametro(Constantes.RUTA_DOCUMENTOS);
            String fileMaxSizeKbStr = propFile.obtenerParametro(Constantes.DOCUMENTOS_MAX_SIZE_KB);
            int fileMaxSizeKb = Integer.parseInt(fileMaxSizeKbStr);

            // validar tamaño del archivo
            if (multipartFile.getSize() > fileMaxSizeKb * 1024) {
                String mensaje = "El tamaño del archivo supera el tamaño permitido.";
                LOG.error(mensaje);
                throw new InvalidParameterException(mensaje);
            }

            // Guardar avance
            avance.setId(guardarAvance(avance, usuario));

            DoSolicitud solicitud = validarYObtenerSolicitud(avance.getIdSolicitud());
            DoGerencia gerencia = solicitud.getGerenciaResponsable();
            DoInstitucion institucion = solicitud.getInstitucion();

            // Guardar registro del documento
            String ruta = serverPath 
                + "/" + Constantes.DOCUMENTS_ROOT_FOLDER 
                + "/" + gerencia.getCodigo() 
                + "/" + institucion.getCodigo() 
                + "/" + solicitud.getDocumentoInstitucion()
                + "/" + "Avances"
                + "/" + avance.getId()
                + "/";

            String originalFileName = multipartFile.getOriginalFilename();

            if (originalFileName == null || originalFileName.isEmpty()) {
                originalFileName = "documentoSinNombre.pdf";
            }

            DocumentoDto documentoDto = new DocumentoDto();
            documentoDto.setIdSolicitud(avance.getIdSolicitud());
            documentoDto.setIdAvance(avance.getId());
            documentoDto.setExtension(originalFileName.substring(originalFileName.lastIndexOf('.')));
            documentoDto.setNombreFisico(originalFileName);
            documentoDto.setNombreOriginal(originalFileName);
            documentoDto.setRuta(ruta);
            documentoDto.setTamanoBytes(Integer.valueOf(String.valueOf(multipartFile.getSize())));
            documentoDto.setTipo(Constantes.TIPO_DOCUMENTO_AVANCE);
            documentoDto.setEstado(Constantes.ESTADO_ACTIVO);

            int idDocumento = servicioDocumento.guardarDocumento(documentoDto, usuario);

            // Si tipo de avance es R-Resolución: marcar solicitud como cerrada
            if (avance.getTipo().equals(Constantes.TIPO_AVANCE_RESOLUCION)) {
                servicioSolicitud.cerrarSolicitud(avance.getIdSolicitud(), usuario);
            } else {
                // caso contrario (es Prorroga): actualizar fecha máxima de atención de la solicitud
                servicioSolicitud.prorrogarSolicitud(avance.getIdSolicitud(), avance.getDiasProrroga(), usuario);
            }            
                        
            // guardar documento fisico
            File directorio = new File(ruta);
            if (!directorio.exists()) {
                if (directorio.mkdirs()) {
                    LOG.info("Directorio " + ruta + " creado");
                } else {
                    LOG.info("Error al crear directorio" + ruta);
                }
            }

            File file = new File(ruta + originalFileName);
            multipartFile.transferTo(file);

            LOG.info("Avance guardado con archivo correctamente - idAvance: " + avance.getId() + ", idDocumento: " + idDocumento);
            return avance.getId();

        } catch (Exception ex) {
            LOG.error("Error en guardarAvanceConDocumento - usuario: " + usuario, ex);
            throw new ServiceException(ex);
        }
    }

    @Override
    public boolean eliminarAvance(int id, String usuario) throws ServiceException {
        LOG.info("Inicio eliminarAvance - id: " + id + ", usuario: " + usuario);

        try {
            DoAvance entity = repositorioAvance.findById(id);

            if (entity == null) {
                String mensaje = "No existe avance con id: " + id + ".";
                LOG.error(mensaje);
                throw new InvalidParameterException(mensaje);
            }

            entity.setEstado(Constantes.ESTADO_ELIMINADO);
            entity.setFechaEliminacion(LocalDateTime.now());
            entity.setUsuarioEliminacion(usuario);
            repositorioAvance.save(entity);

            LOG.info("Avance eliminado lógicamente - id: " + id);
            return true;

        } catch (Exception ex) {
            LOG.error("Error en eliminarAvance - id: " + id + ", usuario: " + usuario, ex);
            throw new ServiceException(ex);
        }
    }

    //#region validaciones
    private DoAvance validarYObtenerAvance(Integer id) {
        // validar existencia
        Optional<DoAvance> optional = repositorioAvance.findById(id);
        if (!optional.isPresent()) {
            String mensaje = "Intento de actualización de avance inexistente - id: " + id;
            LOG.error(mensaje);
            throw new InvalidParameterException(mensaje);
        }
        
        DoAvance entityAvance = optional.get();

        // validar estado eliminado
        if (entityAvance.getEstado().equals(Constantes.ESTADO_ELIMINADO)) {
            String mensaje = "Intento de actualización de avance eliminado - id: " + id;
            LOG.error(mensaje);
            throw new InvalidParameterException(mensaje);
        }

        return entityAvance;
    }

    private DoSolicitud validarYObtenerSolicitud(Integer id) {
        if(id == null || id == 0){
            String mensaje = "Debe seleccionar una solicitud.";
            LOG.error(mensaje);
            throw new InvalidParameterException(mensaje);
        }

        Optional<DoSolicitud> opSolicitud = repositorioSolicitud.findById(id);
        if (!opSolicitud.isPresent()) {
            String mensaje = "No existe la solicitud con id: " + id + ".";
            LOG.error(mensaje);
            throw new InvalidParameterException(mensaje);
        }
        DoSolicitud solicitud = opSolicitud.get();

        // validar que solicitud no esté cerrada
        if(solicitud.getEstado().equals(Constantes.ESTADO_SOL_CERRADA)){
            String mensaje = "No se puede registrar un avance para una solicitud que se encuentra cerrada - id: " + id + ".";
            LOG.error(mensaje);
            throw new InvalidParameterException(mensaje);
        }

        // validar que solicitud no esté deshabilitada
        if(solicitud.getEstado().equals(Constantes.ESTADO_ELIMINADO)){
            String mensaje = "No se puede registrar un avance para una solicitud que se encuentra deshabilitada - id: " + id + ".";
            LOG.error(mensaje);
            throw new InvalidParameterException(mensaje);
        }

        return solicitud;
    }

    private DoDelegado validarYObtenerDelegado(Integer id) {
        if(id == null || id == 0){
            String mensaje = "Debe seleccionar un delegado.";
            LOG.error(mensaje);
            throw new InvalidParameterException(mensaje);
        }

        Optional<DoDelegado> opDelegado = repositorioDelegado.findById(id);
        if (!opDelegado.isPresent()) {
            String mensaje = "No existe el delegado con id: " + id + ".";
            LOG.error(mensaje);
            throw new InvalidParameterException(mensaje);
        }

        return opDelegado.get();
    }

    //#endregion
    
    // MAPPERS
    private AvanceDto toDto(DoAvance avanceEntity) {
        if (avanceEntity == null) {
            return null;
        }

        AvanceDto avanceDto = new AvanceDto();
        avanceDto.setId(avanceEntity.getId());
        avanceDto.setTipo(avanceEntity.getTipo());
        avanceDto.setDiasProrroga(avanceEntity.getDiasProrroga());
        avanceDto.setComentario(avanceEntity.getComentario());
        avanceDto.setFechaCreacion(avanceEntity.getFechaCreacion());
        avanceDto.setEstado(avanceEntity.getEstado());
        
        if (avanceEntity.getDelegado() != null) {
            avanceDto.setIdDelegado(avanceEntity.getDelegado().getId());
            avanceDto.setNombreDelegado(avanceEntity.getDelegado().getNombreFuncionario());
        }

        if (avanceEntity.getSolicitud() != null) {
            avanceDto.setIdSolicitud(avanceEntity.getSolicitud().getId());
        }

        if (avanceEntity.getDocumentos() != null && !avanceEntity.getDocumentos().isEmpty()) {
            DocumentoDto documentoDto = new DocumentoDto();
            DoDocumento documentoEntity = avanceEntity.getDocumentos().get(0);

            documentoDto.setId(documentoEntity.getId());
            documentoDto.setExtension(documentoEntity.getExtension());
            documentoDto.setNombreFisico(documentoEntity.getNombreFisico());
            documentoDto.setNombreOriginal(documentoEntity.getNombreOriginal());
            documentoDto.setRuta(documentoEntity.getRuta());
            documentoDto.setTamanoBytes(documentoEntity.getTamanoBytes());
            documentoDto.setTipo(documentoEntity.getTipo());
            documentoDto.setEstado(documentoEntity.getEstado());
            documentoDto.setIdAvance(avanceDto.getId());
            documentoDto.setIdSolicitud(avanceDto.getIdSolicitud());

            avanceDto.setDocumento(documentoDto);
        }

        return avanceDto;
    }

}