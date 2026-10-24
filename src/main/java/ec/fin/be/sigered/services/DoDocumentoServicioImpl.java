package ec.fin.be.sigered.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ec.fin.be.comunes.excepciones.ServiceException;
import ec.fin.be.sigered.dto.DocumentoDto;
import ec.fin.be.sigered.dto.ResponseDto;
import ec.fin.be.sigered.primary.entity.DoAvance;
import ec.fin.be.sigered.primary.entity.DoDocumento;
import ec.fin.be.sigered.primary.entity.DoSolicitud;
import ec.fin.be.sigered.logger.LoggerSigered;
import ec.fin.be.sigered.primary.repository.DoAvanceRepositorio;
import ec.fin.be.sigered.primary.repository.DoDocumentoRepositorio;
import ec.fin.be.sigered.primary.repository.DoSolicitudRepositorio;
import ec.fin.be.sigered.utils.Constantes;
import ec.fin.be.sigered.utils.Utils;

@Service
public class DoDocumentoServicioImpl implements DoDocumentoServicio {
    private static final Logger LOG = LoggerSigered.getLogger(DoDocumentoServicioImpl.class);
    private static final String MNJ_CONCAT_USUARIO = ", usuario: ";
    
    @Autowired
    private DoDocumentoRepositorio repositorioDocumento;
    @Autowired
    private DoSolicitudRepositorio repositorioSolicitud;
    @Autowired
    private DoAvanceRepositorio repositorioAvance;

    @Override
    public List<DocumentoDto> obtenerDocumentosPorSolicitud(int idSolicitud, String tipo, String estado) throws ServiceException {
        LOG.info("Inicio obtenerDocumentosPorSolicitud - idSolicitud: " + idSolicitud);

        try {
            List<DoDocumento> lista;

            if (idSolicitud > 0 && tipo != null) {
                lista = repositorioDocumento.findBySolicitudIdAndTipoAndEstado(idSolicitud, tipo, estado);
            } else if (idSolicitud > 0) {
                lista = repositorioDocumento.findBySolicitudIdAndEstado(idSolicitud, estado);
            } else if(tipo != null) {
                lista = repositorioDocumento.findByTipoAndEstado(tipo, estado);
            } else {
                lista = repositorioDocumento.findAll();
            }

            LOG.info("Total documentos obtenidas: " + lista.size());

            return lista.stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());

        } catch (Exception ex) {
            LOG.error("Error en obtenerDocumentosPorSolicitud", ex);
            throw new ServiceException(ex);
        }
    }

    @Override
    public List<DocumentoDto> obtenerDocumentosPorAvance(int idAvance, String estado) throws ServiceException {
        LOG.info("Inicio obtenerDocumentosPorAvance - idAvance: " + idAvance);

        try {
            List<DoDocumento> lista;

            if (idAvance > 0) {
                lista = repositorioDocumento.findByAvanceIdAndEstado(idAvance, estado);
            } else {
                lista = repositorioDocumento.findAll();
            }

            LOG.info("Total documentos obtenidas: " + lista.size());

            return lista.stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());

        } catch (Exception ex) {
            LOG.error("Error en obtenerDocumentosPorAvance", ex);
            throw new ServiceException(ex);
        }
    }

    @Override
    public DocumentoDto obtenerDocumento(int id) throws ServiceException {
        LOG.info("Inicio obtenerDocumento - id: " + id);

        try {
            DoDocumento entity = repositorioDocumento.findById(id);
            if (entity == null) {
                LOG.warn("No se encontró documento con id: " + id);
                return null;
            }

            return toDto(entity);
        } catch (Exception ex) {
            LOG.error("Error en obtenerDocumento - id: " + id, ex);
            throw new ServiceException(ex);
        }
    }

    @Transactional
    @Override
    public int guardarDocumento(DocumentoDto dto, String usuario) throws ServiceException {
        LOG.info("Inicio guardarDocumento - usuario: " + usuario + ", id: " + dto.getId());

        try {
            DoDocumento entity;

            if (dto.getId() != null) {
                LOG.debug("Actualizando documento - id: " + dto.getId());

                entity = validarYObtenerDocumento(dto.getId());
                entity.setFechaModificacion(LocalDateTime.now());
                entity.setUsuarioModificacion(usuario);
            } else {
                LOG.debug("Creando nuevo documento para la Solicitud: " + dto.getIdSolicitud());

                entity = new DoDocumento();
                entity.setFechaCreacion(LocalDateTime.now());
                entity.setUsuarioCreacion(usuario);
            }

            validarTipoDocumento(dto.getTipo());

            // mapping DTO → Entity
            entity.setExtension(dto.getExtension());
            entity.setNombreFisico(dto.getNombreFisico());
            entity.setNombreOriginal(dto.getNombreOriginal());
            entity.setRuta(dto.getRuta());
            entity.setTamanoBytes(dto.getTamanoBytes());
            entity.setEstado(Constantes.ESTADO_ACTIVO);
            entity.setTipo(dto.getTipo());
            entity.setSolicitud(validarYObtenerSolicitud(dto));

            // mapping avance solo si tipoDocumento es avance
            if(entity.getTipo().equals(Constantes.TIPO_DOCUMENTO_AVANCE)) {
                entity.setAvance(validarYObtenerAvance(dto));
            }
            
            entity = repositorioDocumento.save(entity);

            LOG.info("documento guardado correctamente - id: " + entity.getId());
            return entity.getId();

        } catch (Exception ex) {
            LOG.error("Error en guardarDocumento - usuario: " + usuario, ex);
            throw new ServiceException(ex);
        }
    }

    @Override
    public ResponseDto descargarDocumentoBase64(int id) throws ServiceException {
        LOG.info("Inicio descargarDocumento - id: " + id);

        try {
            DoDocumento doc = repositorioDocumento.findById(id);
            if (doc == null) {
                String mensaje = "No se encontró documento con id: " + id + ".";                
                LOG.error(mensaje);
                throw new InvalidParameterException(mensaje);
            }

            Path path = Paths.get(doc.getRuta() + doc.getNombreFisico());

            byte[] bytes = Files.readAllBytes(path);
            String base64 = Base64.getEncoder().encodeToString(bytes);

            ResponseDto respuesta = Utils.devolverRespuesta(Constantes.CODIGOS_RESPUESTA.OK_00);
            Map<String, Object> data = new HashMap<>();
            data.put("nombreArchivo", doc.getNombreOriginal());
            data.put("extension", doc.getExtension());
            data.put("base64", base64);
            data.put("tamanoBytes", doc.getTamanoBytes());

            respuesta.setData(data);
            return respuesta;
            
        } catch (Exception ex) {
            LOG.error("Error en descargarDocumento - id: " + id, ex);
            throw new ServiceException(ex);
        }
    }

    @Transactional
    @Override
    public boolean eliminarDocumentos(int idSolicitud, String tipo, String usuario) throws ServiceException {
        LOG.info("Inicio eliminarDocumentosSolicitud - idSolicitud: " + idSolicitud + ", tipo: " + tipo + MNJ_CONCAT_USUARIO + usuario);

        try {
            List<DoDocumento> documentos = repositorioDocumento.findBySolicitudIdAndTipo(idSolicitud, tipo);

            if (documentos == null || documentos.isEmpty()) {
                String mensaje = "No existen documentos de tipo " + tipo + " para la solicitud con id: " + idSolicitud + ".";
                LOG.error(mensaje);
                throw new InvalidParameterException(mensaje);
            }

            // eliminación fisica de archivos
            for (DoDocumento doc : documentos) {
                eliminarArchivoFisico(doc);
            }

            // Eliminación de registros
            repositorioDocumento.deleteAll(documentos);

            LOG.info("Documentos de tipo " + tipo + " eliminados - idSolicitud: " + idSolicitud);
            return true;

        } catch (Exception ex) {
            LOG.error("Error en eliminarDocumentosSolicitud - idSolicitud: " + idSolicitud, ex);
            throw new ServiceException(ex);
        }
        
    }

    private void eliminarArchivoFisico(DoDocumento doc) throws IOException {
        Path path = Paths.get(doc.getRuta(), doc.getNombreFisico());

        try {

            if (!Files.exists(path)) {
                LOG.info("Documento no existe fisicamente - path: " + path);
                return;
            }

            Files.delete(path);
            LOG.info("Documento eliminado fisicamente - path: " + path);
            
        } catch (IOException ex) {
            LOG.error("No se pudo eliminar el documento fisicamente - path: " + path, ex);
            throw ex;
        }
        
    }

    //#region validaciones
    private DoDocumento validarYObtenerDocumento(Integer id) {
        // validar existencia
        Optional<DoDocumento> optional = repositorioDocumento.findById(id);
        if (!optional.isPresent()) {
            String mensaje = "Intento de actualización de documento inexistente - id: " + id;
            LOG.error(mensaje);
            throw new InvalidParameterException(mensaje);
        }

        DoDocumento entity = optional.get();                
        
        // validar estado eliminado
        if (entity.getEstado().equals(Constantes.ESTADO_ELIMINADO)) {
            String mensaje = "Intento de actualización de documento eliminado - id: " + id;
            LOG.error(mensaje);
            throw new InvalidParameterException(mensaje);
        }

        return entity;
    }

    private void validarTipoDocumento(String tipo) {
        // validar tipo de documento
        if (tipo == null || tipo.trim().isEmpty()) {
            String mensaje = "Debe seleccionar un tipo de documento.";
            LOG.error(mensaje);
            throw new InvalidParameterException(mensaje);
        }

        // tipo de documento debe ser avance o solicitud
        if (!tipo.equals(Constantes.TIPO_DOCUMENTO_AVANCE) && !tipo.equals(Constantes.TIPO_DOCUMENTO_SOLICITUD)) {
            String mensaje = "El tipo de documento debe ser avance o solicitud.";
            LOG.error(mensaje);
            throw new InvalidParameterException(mensaje);
        }
    }

    private DoSolicitud validarYObtenerSolicitud(DocumentoDto dto) {
        if(dto.getIdSolicitud() == null || dto.getIdSolicitud() == 0){
            String mensaje = "Debe seleccionar una solicitud.";
            LOG.error(mensaje);
            throw new InvalidParameterException(mensaje);
        }

        Optional<DoSolicitud> opSolicitud = repositorioSolicitud.findById(dto.getIdSolicitud());
        if (!opSolicitud.isPresent()) {
            String mensaje = "No existe la solicitud con id: " + dto.getIdSolicitud() + ".";
            LOG.error(mensaje);
            throw new InvalidParameterException(mensaje);
        }

        return opSolicitud.get();
    }

    private DoAvance validarYObtenerAvance(DocumentoDto dto) {
        if(dto.getIdAvance() == null || dto.getIdAvance() == 0){
            String mensaje = "Si el tipo de documento es avance, debe seleccionar un avance.";
            LOG.error(mensaje);
            throw new InvalidParameterException(mensaje);
        }

        Optional<DoAvance> opAvance = repositorioAvance.findById(dto.getIdAvance());
        if (!opAvance.isPresent()) {
            String mensaje = "No existe el avance con id: " + dto.getIdAvance() + ".";
            LOG.error(mensaje);
            throw new InvalidParameterException(mensaje);
        }

        return opAvance.get();
    }

    //#endregion

    //#region Mappers
    private DocumentoDto toDto(DoDocumento entity) {
        if (entity == null) {
            return null;
        }

        DocumentoDto dto = new DocumentoDto();
        dto.setId(entity.getId());
        dto.setExtension(entity.getExtension());
        dto.setNombreFisico(entity.getNombreFisico());
        dto.setNombreOriginal(entity.getNombreOriginal());
        dto.setRuta(entity.getRuta());
        dto.setTamanoBytes(entity.getTamanoBytes());
        dto.setTipo(entity.getTipo());
        dto.setEstado(entity.getEstado());

        if (entity.getSolicitud() != null) {
            dto.setIdSolicitud(entity.getSolicitud().getId());
        }

        if (entity.getAvance() != null) {
            dto.setIdAvance(entity.getAvance().getId());
        }

        return dto;
    }

    //#endregion

}