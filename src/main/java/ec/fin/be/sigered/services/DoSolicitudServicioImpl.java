package ec.fin.be.sigered.services;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ec.fin.be.autenticacion.ldap.utils.UtilProperties;
import ec.fin.be.autenticacion.ldap.ws.RolMDto;
import ec.fin.be.comunes.excepciones.ServiceException;
import ec.fin.be.sigered.BeanConfig;
import ec.fin.be.sigered.UtilPropertiesArquetipo;
import ec.fin.be.sigered.dto.DocumentoDto;
import ec.fin.be.sigered.dto.ResponseDto;
import ec.fin.be.sigered.dto.SolicitudDto;
import ec.fin.be.sigered.dto.SolicitudFiltroDto;
import ec.fin.be.sigered.primary.entity.DoDelegado;
import ec.fin.be.sigered.primary.entity.DoDocumento;
import ec.fin.be.sigered.primary.entity.DoGerencia;
import ec.fin.be.sigered.primary.entity.DoInstitucion;
import ec.fin.be.sigered.primary.entity.DoSolicitud;
import ec.fin.be.sigered.logger.LoggerSigered;
import ec.fin.be.sigered.primary.repository.DoAvanceRepositorio;
import ec.fin.be.sigered.primary.repository.DoDelegadoRepositorio;
import ec.fin.be.sigered.primary.repository.DoDocumentoRepositorio;
import ec.fin.be.sigered.primary.repository.DoGerenciaRepositorio;
import ec.fin.be.sigered.primary.repository.DoInstitucionRepositorio;
import ec.fin.be.sigered.primary.repository.DoSolicitudRepositorio;
import ec.fin.be.sigered.specification.DoSolicitudSpecification;
import ec.fin.be.sigered.utils.Constantes;

@Service
public class DoSolicitudServicioImpl implements DoSolicitudServicio {
    private static final Logger LOG = LoggerSigered.getLogger(DoSolicitudServicioImpl.class);
    private static final String MNJ_NO_EXISTE_REGISTRO = "No existe la solicitud con id: ";
    private static final String MNJ_TOTAL_SOLICITUDES = "Total solicitudes obtenidas: ";
    private static final String MNJ_CONCAT_USUARIO = ", usuario: ";
    private static final UtilPropertiesArquetipo propFile = UtilPropertiesArquetipo.getIntances();

    @Autowired
    DoSolicitudRepositorio repositorioSolicitud;
    @Autowired
    DoInstitucionRepositorio repositorioInstitucion;
    @Autowired
    DoGerenciaRepositorio repositorioGerencia;
    @Autowired
    DoDocumentoServicio servicioDocumento;
    @Autowired
    DoDocumentoRepositorio repositorioDocumento;
    @Autowired
    DoDelegadoRepositorio repositorioDelegado;
    @Autowired
    NotificacionCorreoServicio notificacionCorreoServicio;
    @Autowired
    DoAvanceRepositorio repositorioAvance;
    @Autowired
    BeanConfig beanConfig;

    @Override
    public List<SolicitudDto> obtenerSolicitudes() throws ServiceException {
        LOG.info("Inicio obtenerSolicitudes");
        try {
            List<DoSolicitud> lista = repositorioSolicitud.findAll();

            LOG.info(MNJ_TOTAL_SOLICITUDES + lista.size());

            return lista.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        } catch (Exception ex) {
            LOG.error("Error en obtenerSolicitudes", ex);
            throw new ServiceException(ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudDto> obtenerSolicitudesAEliminar() throws ServiceException {
        LOG.info("Inicio obtenerSolicitudesAEliminar");
        try {
            // 1. Parámetro de conservación
            int maxAnios = Integer.parseInt(propFile.obtenerParametro(Constantes.MAX_ANIOS_CONSERVACION));
            LocalDate fechaCorte = LocalDate.now().minusYears(maxAnios);

            List<DoSolicitud> lista = repositorioSolicitud.findByFechaIngresoBefore(fechaCorte);

            LOG.info(MNJ_TOTAL_SOLICITUDES + lista.size());

            return lista.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        } catch (Exception ex) {
            LOG.error("Error en obtenerSolicitudesAEliminar", ex);
            throw new ServiceException(ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public int obtenerCantidadSolicitudesAEliminar(String cedulaDelegado) throws ServiceException {
        LOG.info("Inicio obtenerQtySolicitudesAEliminar");

        try {
            // 1. Parámetro de conservación
            int maxAnios = Integer.parseInt(propFile.obtenerParametro(Constantes.MAX_ANIOS_CONSERVACION));
            LocalDate fechaCorte = LocalDate.now().minusYears(maxAnios);

            DoDelegado delegado = repositorioDelegado.findByCedula(cedulaDelegado);
            
            if (delegado == null) {
                throw new ServiceException("No se encontró el delegado con cédula: " + cedulaDelegado);
            }

            if (delegado.getGerencia() == null) {
                throw new ServiceException("No se encontró la gerencia del delegado con cédula: " + cedulaDelegado);
            }

            int qty = repositorioSolicitud.countByFechaIngresoBeforeAndGerenciaResponsable(fechaCorte, delegado.getGerencia());

            LOG.info(MNJ_TOTAL_SOLICITUDES + qty);

            return qty;

        } catch (Exception ex) {
            LOG.error("Error en obtenerQtySolicitudesAEliminar", ex);
            throw new ServiceException(ex);
        }
    }

    @Override
    public SolicitudDto obtenerSolicitud(int id) throws ServiceException {
        LOG.info("Inicio obtenerSolicitud - id: " + id);

        try {
            DoSolicitud entity = repositorioSolicitud.findById(id);
            if (entity == null) {
                LOG.warn("No se encontró solicitud con id: " + id);
                return null;
            }

            return toDto(entity);
        } catch (Exception ex) {
            LOG.error("Error en obtenerSolicitud - id: " + id, ex);
            throw new ServiceException(ex);
        }
    }

    @Override
    public List<SolicitudDto> buscarSolicitudes(SolicitudFiltroDto filtro, int pageSize, String cedulaUsuario) throws ServiceException {
        LOG.info("Inicio buscarSolicitudes - usuario: " + cedulaUsuario);

        try {
            
            if (!puedeVerTodo(cedulaUsuario)) {
                DoDelegado delegado = repositorioDelegado.findByCedula(cedulaUsuario);

                if (delegado == null) {
                    throw new ServiceException("No se encontró información del usuario delegado.");
                }
                
                if (delegado.getGerencia() == null) {
                    throw new ServiceException("El usuario no tiene una gerencia asignada.");
                }

                int idGerencia = delegado.getGerencia().getId();

                if (filtro.getIdGerenciaResponsable() != null && !filtro.getIdGerenciaResponsable().equals(idGerencia)) {
                    throw new ServiceException("No tiene permisos para consultar solicitudes de una gerencia diferente a la que tiene asignada.");
                }
                
                // el delegado Operador solo puede consultar solicitudes de su gerencia
                filtro.setIdGerenciaResponsable(idGerencia);
            }
            
            List<DoSolicitud> lista;
            Specification<DoSolicitud> specification = DoSolicitudSpecification.buscar(filtro);

            if(pageSize > 0) {
                Pageable pageable = PageRequest.of(
                    0,
                    pageSize,
                    Sort.by(Sort.Direction.DESC, "fechaCreacion")
                );

                lista = repositorioSolicitud
                    .findAll(specification, pageable)
                    .getContent();
            } else {
                lista = repositorioSolicitud.findAll(
                    specification,
                    Sort.by(Sort.Direction.DESC, "fechaCreacion")
                );
            }

            LOG.info(MNJ_TOTAL_SOLICITUDES + lista.size());

            return lista.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            LOG.error("Error en buscarSolicitudes", ex);
            throw new ServiceException(ex);
        }
    }
    
    @Override
    public int guardarSolicitud(SolicitudDto dto, String usuario) throws ServiceException {
        LOG.info("Inicio guardarSolicitud - usuario: " + usuario + ", id: " + dto.getId());

        try {
            DoSolicitud entity;
            boolean esActualizacion = dto.getId() != null && dto.getId() > 0;

            if (esActualizacion) {
                LOG.debug("Actualizando solicitud - id: " + dto.getId());

                entity = validarYObtenerSolicitud(dto.getId());
                entity.setFechaModificacion(LocalDateTime.now());
                entity.setUsuarioModificacion(usuario);
            } else {
                LOG.debug("Creando nueva solicitud");

                entity = new DoSolicitud();
                entity.setFechaCreacion(LocalDateTime.now());
                entity.setUsuarioCreacion(usuario);
                entity.setEstado(Constantes.ESTADO_SOL_EN_PROCESO);
            }

            // validar que nro de documento interno no exista
            DoSolicitud solicitudExistente = repositorioSolicitud.findByDocumentoInterno(dto.getDocumentoInterno());

            if (solicitudExistente != null && !solicitudExistente.getId().equals(dto.getId())) {
                String mensaje = "El número de documento interno " + dto.getDocumentoInterno() + " ya existe.";
                LOG.error(mensaje);
                throw new InvalidParameterException(mensaje);
            }

            // mapping DTO → Entity
            entity.setTipoPlazo(dto.getTipoPlazo());
            entity.setDiasPlazo(dto.getDiasPlazo());
            entity.setDocumentoInstitucion(dto.getDocumentoInstitucion());
            entity.setDocumentoInterno(dto.getDocumentoInterno());
            entity.setFechaIngreso(dto.getFechaIngreso());
            entity.setFechaMaximaAtencion(dto.getFechaMaximaAtencion());
            entity.setTituloDocumento(dto.getTituloDocumento());
            entity.setInstitucion(validarYObtenerInstitucion(dto.getIdInstitucion()));
            entity.setFuncionarioReceptor(dto.getFuncionarioReceptor());

            DoGerencia gerenciaResponsable = validarYObtenerGerencia(dto.getIdGerenciaResponsable());
            entity.setGerenciaResponsable(gerenciaResponsable);            

            // guardar
            entity = repositorioSolicitud.save(entity);
            LOG.info("solicitud guardada correctamente - id: " + entity.getId() + ", gerencia responsable: " + gerenciaResponsable.getId());

            // si fue nueva solicitud: enviar correo en background al delegado de la gerencia responsable
            if (!esActualizacion) {
                enviarCorreoEnBackground(entity, gerenciaResponsable);
            }

            return entity.getId();

        } catch (Exception ex) {
            LOG.error("Error en guardarSolicitud - usuario: " + usuario, ex);
            throw new ServiceException(ex);
        }
    }

    private void enviarCorreoEnBackground(DoSolicitud entity, DoGerencia gerenciaResponsable) {
        int idGerencia = gerenciaResponsable.getId();
        DoDelegado delegado = repositorioDelegado.findByGerenciaId(idGerencia);

        final String correo = delegado.getCorreo();
        final String documentoInstitucion = entity.getDocumentoInstitucion();
        final String documentoInterno = entity.getDocumentoInterno();
        final String tituloDocumento = entity.getTituloDocumento();
        final String fechaMaximaAtencion = entity.getFechaMaximaAtencion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        LOG.debug("Enviando correo a delegado de la gerencia responsable. Gerencia: " + idGerencia + ", delegado: " + delegado.getId() + ", email: " + delegado.getCorreo());
        
        // enviar correo en background
        CompletableFuture.runAsync(() -> {
            try {
                ResponseDto response = notificacionCorreoServicio.enviarCorreoNuevaSolicitud(
                    correo,
                    documentoInstitucion,
                    documentoInterno,
                    tituloDocumento,
                    fechaMaximaAtencion);

                    LOG.info("Resultado de envío de correo: " + response.toString());
                    
            } catch (Exception e) {
                LOG.error("Error enviando correo", e);
            }
        });
    }

    @Transactional
    @Override
    public int guardarSolicitudConDocumentos(SolicitudDto solicitud, MultipartFile[] multipartFiles, String usuario) throws ServiceException {
        LOG.info("Inicio guardarSolicitudConDocumentos - usuario: " + usuario + ", id: " + solicitud.getId());

        try {
            // obtener parametros del archivo properties
            String serverPath = propFile.obtenerParametro(Constantes.RUTA_DOCUMENTOS);
            String fileMaxSizeKbStr = propFile.obtenerParametro(Constantes.DOCUMENTOS_MAX_SIZE_KB);
            String loteMaxSizeKbStr = propFile.obtenerParametro(Constantes.DOCUMENTOS_LOTE_MAX_SIZE_KB);
            String documentosMaxQtyStr = propFile.obtenerParametro(Constantes.DOCUMENTOS_MAX_QTY);
            int fileMaxSizeKb = Integer.parseInt(fileMaxSizeKbStr);
            int loteMaxSizeKb = Integer.parseInt(loteMaxSizeKbStr);
            int documentosMaxQty = Integer.parseInt(documentosMaxQtyStr);

            validarArchivoSeleccionados(multipartFiles, fileMaxSizeKb, loteMaxSizeKb, documentosMaxQty);
            
            // guardar solicitud
            boolean esActualizacion = solicitud.getId() != null && solicitud.getId() > 0;
            solicitud.setId(guardarSolicitud(solicitud, usuario));

            // si fue actualizacion, eliminar físicamente documentos anteriores de tipo S de la solicitud si existen
            if (esActualizacion) {
                boolean existenDocumentosAnteriores = repositorioDocumento.existsBySolicitudIdAndTipo(solicitud.getId(), Constantes.TIPO_DOCUMENTO_SOLICITUD);

                if (existenDocumentosAnteriores) {
                    servicioDocumento.eliminarDocumentos(solicitud.getId(), Constantes.TIPO_DOCUMENTO_SOLICITUD, usuario);
                }
            }

            DoGerencia gerencia = validarYObtenerGerencia(solicitud.getIdGerenciaResponsable());
            DoInstitucion institucion = validarYObtenerInstitucion(solicitud.getIdInstitucion());

            // Guardar registro del documento
            String ruta = serverPath 
                + "/" + Constantes.DOCUMENTS_ROOT_FOLDER 
                + "/" + gerencia.getCodigo() 
                + "/" + institucion.getCodigo() 
                + "/" + solicitud.getDocumentoInstitucion()
                + "/";

            // guardar cada archivo
            for (MultipartFile multipartFile : multipartFiles) {
                if (multipartFile == null || multipartFile.isEmpty()) {
                    continue;
                }

                String originalFileName = multipartFile.getOriginalFilename();

                if (originalFileName == null || originalFileName.isEmpty()) {
                    originalFileName = "documentoSinNombre.pdf";
                }

                DocumentoDto documentoDto = new DocumentoDto();
                documentoDto.setIdSolicitud(solicitud.getId());
                documentoDto.setExtension(originalFileName.substring(originalFileName.lastIndexOf('.')));
                documentoDto.setNombreFisico(originalFileName);
                documentoDto.setNombreOriginal(originalFileName);
                documentoDto.setRuta(ruta);
                documentoDto.setTamanoBytes(Integer.valueOf(String.valueOf(multipartFile.getSize())));
                documentoDto.setTipo(Constantes.TIPO_DOCUMENTO_SOLICITUD);
                documentoDto.setEstado(Constantes.ESTADO_ACTIVO);

                int idDocumento = servicioDocumento.guardarDocumento(documentoDto, usuario);
                
                guardarArchivoFisico(multipartFile, originalFileName, ruta);

                LOG.info("Archivo " + originalFileName + " guardado correctamente - solicitud: " + solicitud.getId() + ", idDocumento: " + idDocumento);
            }

            LOG.info("Solicitud guardada correctamente - id: " + solicitud.getId() + ", cantidad de documentos: " + multipartFiles.length);

            return solicitud.getId();

        } catch (Exception ex) {
            LOG.error("Error en guardarSolicitudConDocumento - usuario: " + usuario, ex);
            throw new ServiceException(ex);
        }
    }

    @Override
    public boolean cerrarSolicitud(int id, String usuario) throws ServiceException {
        LOG.info("Inicio cerrarSolicitud - id: " + id + MNJ_CONCAT_USUARIO + usuario);

        try {
            DoSolicitud entity = repositorioSolicitud.findById(id);

            if (entity == null) {
                String mensaje = MNJ_NO_EXISTE_REGISTRO + id + ".";
                LOG.error(mensaje);
                throw new InvalidParameterException(mensaje);
            }

            entity.setEstado(Constantes.ESTADO_SOL_CERRADA);
            entity.setFechaResolucion(LocalDateTime.now());
            entity.setUsuarioResolucion(usuario);

            repositorioSolicitud.save(entity);

            LOG.info("Solicitud cerrada - id: " + id);
            return true;

        } catch (Exception ex) {
            LOG.error("Error en cerrarSolicitud - id: " + id + MNJ_CONCAT_USUARIO + usuario, ex);
            throw new ServiceException(ex);
        }
    }

    @Override
    public boolean prorrogarSolicitud(int id, int diasProrroga, String usuario) throws ServiceException {
        LOG.info("Inicio prorrogarSolicitud - id: " + id + ", diasProrroga: " + diasProrroga + MNJ_CONCAT_USUARIO + usuario);

        try {
            DoSolicitud entity = repositorioSolicitud.findById(id);

            if (entity == null) {
                String mensaje = MNJ_NO_EXISTE_REGISTRO + id + ".";
                LOG.error(mensaje);
                throw new InvalidParameterException(mensaje);
            }

            // solo se puede prorrogar solicitudes en proceso
            if (!entity.getEstado().equals(Constantes.ESTADO_SOL_EN_PROCESO)) {
                String mensaje = "No se puede prorrogar una solicitud con estado " + entity.getEstado() + " - id: " + id;
                LOG.error(mensaje);
                throw new InvalidParameterException(mensaje);
            }

            entity.setFechaMaximaAtencion(entity.getFechaMaximaAtencion().plusDays(diasProrroga));

            repositorioSolicitud.save(entity);

            LOG.info("Solicitud prorrogada - id: " + id);
            return true;

        } catch (Exception ex) {
            LOG.error("Error en prorrogarSolicitud - id: " + id + ", diasProrroga: " + diasProrroga + MNJ_CONCAT_USUARIO + usuario, ex);
            throw new ServiceException(ex);
        }
    }

    @Override
    public boolean reabrirSolicitud(int id, String usuario) throws ServiceException {
        LOG.info("Inicio reabrirSolicitud - id: " + id + MNJ_CONCAT_USUARIO + usuario);

        try {
            DoSolicitud entity = repositorioSolicitud.findById(id);

            if (entity == null) {
                String mensaje = MNJ_NO_EXISTE_REGISTRO + id + ".";
                LOG.error(mensaje);
                throw new InvalidParameterException(mensaje);
            }

            entity.setEstado(Constantes.ESTADO_SOL_EN_PROCESO);
            repositorioSolicitud.save(entity);

            LOG.info("Solicitud reabierta - id: " + id + MNJ_CONCAT_USUARIO + usuario);
            return true;

        } catch (Exception ex) {
            LOG.error("Error en reabrirSolicitud - id: " + id + MNJ_CONCAT_USUARIO + usuario, ex);
            throw new ServiceException(ex);
        }
    }

    @Override
    public boolean deshabilitarSolicitud(int id, String razonAnulacion, String usuario) throws ServiceException {
        LOG.info("Inicio deshabilitarSolicitud - id: " + id + MNJ_CONCAT_USUARIO + usuario);

        try {
            DoSolicitud entity = repositorioSolicitud.findById(id);

            if (entity == null) {
                String mensaje = MNJ_NO_EXISTE_REGISTRO + id + ".";
                LOG.error(mensaje);
                throw new InvalidParameterException(mensaje);
            }

            // valida que no se encuentre deshabilitada
            if (entity.getEstado().equals(Constantes.ESTADO_DESHABILITADO)) {
                String mensaje = "La solicitud ya se encuentra deshabilitada - id: " + id;
                LOG.error(mensaje);
                throw new InvalidParameterException(mensaje);
            }

            if (razonAnulacion == null || razonAnulacion.isEmpty()) {
                String mensaje = "Debe ingresar una razón de anulación.";
                LOG.error(mensaje);
                throw new InvalidParameterException(mensaje);
            }

            // deshabilitar (eliminación lógica)
            entity.setEstado(Constantes.ESTADO_DESHABILITADO);
            entity.setFechaEliminacion(LocalDateTime.now());
            entity.setUsuarioEliminacion(usuario);
            entity.setRazonAnulacion(razonAnulacion);

            repositorioSolicitud.save(entity);

            LOG.info("Solicitud deshabilitada exitosamente - id: " + id);
            return true;

        } catch (Exception ex) {
            LOG.error("Error en deshabilitarSolicitud - id: " + id + MNJ_CONCAT_USUARIO + usuario, ex);
            throw new ServiceException(ex);
        }
    }
    
    @Override
    @Transactional
    public boolean eliminarDefinitivamenteSolicitudYAvances(int id, String usuario, String cedula) throws ServiceException {
        LOG.info("Inicio eliminarDefinitivamenteSolicitudYAvances - id: " + id + MNJ_CONCAT_USUARIO + usuario);

        try {
            DoSolicitud entity = repositorioSolicitud.findById(id);

            if (entity == null) {
                String mensaje = MNJ_NO_EXISTE_REGISTRO + id + ".";
                LOG.error(mensaje);
                throw new InvalidParameterException(mensaje);
            }

            // validar que usuario puede eliminar
            if (!puedeEliminar(cedula)) {
                String mensaje = "El usuario no tiene permisos para eliminar solicitudes.";
                LOG.error(mensaje);
                throw new InvalidParameterException(mensaje);
            }

            // validar antiguedad
            int maxAnios = Integer.parseInt(propFile.obtenerParametro(Constantes.MAX_ANIOS_CONSERVACION));
            LocalDate fechaCorte = LocalDate.now().minusYears(maxAnios);

            if (entity.getFechaIngreso().isAfter(fechaCorte)) {
                String mensaje = "Solo se pueden eliminar solicitudes con al menos " + maxAnios + " años de antigüedad.";
                LOG.error(mensaje);
                throw new InvalidParameterException(mensaje);
            }

            // obtener ruta principal usando el primer documento de la solicitud de tipo S
            String rutaPrincipal = entity.getDocumentos().stream()
                .filter(d -> d.getTipo().equals(Constantes.TIPO_DOCUMENTO_SOLICITUD))
                .findFirst()
                .map(DoDocumento::getRuta)
                .orElse(null);

            // Eliminar los archivos fisicos de tipo solicitud
            boolean existenDocumentosSolicitud = rutaPrincipal != null && !rutaPrincipal.isEmpty();

            if (existenDocumentosSolicitud) {
                servicioDocumento.eliminarDocumentos(id, Constantes.TIPO_DOCUMENTO_SOLICITUD, usuario);
            }

            // eliminar los archivos fisicos de tipo avance
            boolean existenDocumentosAvance = entity.getDocumentos().stream().anyMatch(d -> d.getTipo().equals(Constantes.TIPO_DOCUMENTO_AVANCE));

            if (existenDocumentosAvance) {
                servicioDocumento.eliminarDocumentos(id, Constantes.TIPO_DOCUMENTO_AVANCE, usuario);
            }

            // eliminar directorio recursivamente
            eliminarDirectorioRecursivo(rutaPrincipal);

            // eliminar registros de avances
            repositorioAvance.deleteAll(entity.getAvances());

            // eliminar registro de solicitud
            repositorioSolicitud.deleteById(id);
            LOG.info("Solicitud y avances eliminados definitivamente - id: " + id);

            return true;
        } catch (Exception ex) {
            LOG.error("Error en eliminarDefinitivamenteSolicitudYAvances - id: " + id + MNJ_CONCAT_USUARIO + usuario, ex);
            throw new ServiceException(ex);
        }
    }

    private void eliminarDirectorioRecursivo(String rutaPrincipal) throws IOException {
        Path ruta = Paths.get(rutaPrincipal);

        if (!Files.exists(ruta)) {
            return;
        }

        try (Stream<Path> paths = Files.walk(ruta)) {
            paths.sorted(Comparator.reverseOrder())
            .forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });

        }
    }

    private void guardarArchivoFisico(MultipartFile multipartFile, String originalFileName, String ruta) throws IOException {
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
    }

    //#region Validaciones
    private void validarArchivoSeleccionados(MultipartFile[] multipartFiles, int fileMaxSizeKb, int loteMaxSizeKb, int documentosMaxQty) {
        // validar archivos seleccionados
        if (multipartFiles == null || multipartFiles.length == 0) {
            String mensaje = "No se han seleccionado archivos.";
            LOG.error(mensaje);
            throw new InvalidParameterException(mensaje);
        }

        // validar cantidad de archivos seleccionados
        if (multipartFiles.length > documentosMaxQty) {
            String mensaje = "La cantidad de documentos seleccionados supera la cantidad permitida (" + documentosMaxQty + ").";
            LOG.error(mensaje);
            throw new InvalidParameterException(mensaje);
        }

        long totalSizeBytes = 0;

        // validar tamaño de cada archivo
        for (int i = 0; i < multipartFiles.length; i++) {
            MultipartFile file = multipartFiles[i];

            if (file.getSize() > fileMaxSizeKb * 1024L) {
                String mensaje = "El tamaño del archivo " + file.getOriginalFilename() + " supera el tamaño máximo permitido (" + fileMaxSizeKb + "KB).";
                LOG.error(mensaje);
                throw new InvalidParameterException(mensaje);
            }

            totalSizeBytes += file.getSize();
        }

        // validar tamaño total del lote
        if (totalSizeBytes > loteMaxSizeKb * 1024L) {
            String mensaje = "El tamaño total de los archivos seleccionados supera el tamaño máximo permitido (" + loteMaxSizeKb + " KB).";
            LOG.error(mensaje);
            throw new InvalidParameterException(mensaje);
        }
    }

    private DoSolicitud validarYObtenerSolicitud(Integer id) {
        // validar existencia
        Optional<DoSolicitud> optional = repositorioSolicitud.findById(id);
        if (!optional.isPresent()) {
            String mensaje = "Intento de actualización de solicitud inexistente - id: " + id;
            LOG.error(mensaje);
            throw new InvalidParameterException(mensaje);
        }
        
        DoSolicitud entity = optional.get();
        
        // validar estado deshabilitado
        if (entity.getEstado().equals(Constantes.ESTADO_DESHABILITADO)) {
            String mensaje = "Intento de actualización de solicitud deshabilitada - id: " + id;
            LOG.error(mensaje);
            throw new InvalidParameterException(mensaje);
        }

        // validar que no se pueda modificar si el estado es C-Cerrado
        if(entity.getEstado().equals(Constantes.ESTADO_SOL_CERRADA)) {
            String mensaje = "No se puede modificar una solicitud cerrada - id: " + id;
            LOG.error(mensaje);
            throw new InvalidParameterException(mensaje);
        }

        return entity;
    }

    private DoInstitucion validarYObtenerInstitucion(Integer id) {

        // validar existencia
        Optional<DoInstitucion> optional = repositorioInstitucion.findById(id);
        if (!optional.isPresent()) {
            String mensaje = "No existe registro de la institución con id: " + id + ".";
            LOG.error(mensaje);
            throw new InvalidParameterException(mensaje);
        }

        DoInstitucion institucion = optional.get();

        // validar estado eliminado
        if (institucion.getEstado().equals(Constantes.ESTADO_ELIMINADO)) {
            String mensaje = "No se puede seleccionar una institución eliminada - id: " + id;
            LOG.error(mensaje);
            throw new InvalidParameterException(mensaje);
        }

        return institucion;
    }

    private DoGerencia validarYObtenerGerencia(Integer id) {
        // validar existencia
        Optional<DoGerencia> optional = repositorioGerencia.findById(id);
        if (!optional.isPresent()) {
            String mensaje = "No existe registro de la gerencia con id: " + id + ".";
            LOG.error(mensaje);
            throw new InvalidParameterException(mensaje);
        }

        DoGerencia gerencia = optional.get();

        // validar estado eliminado
        if (gerencia.getEstado().equals(Constantes.ESTADO_ELIMINADO)) {
            String mensaje = "No se puede seleccionar una gerencia eliminada - id: " + id;
            LOG.error(mensaje);
            throw new InvalidParameterException(mensaje);
        }

        return gerencia;
    }

    private List<RolMDto> obtenerRoles(String cedula) throws ServiceException {
        return beanConfig.clienteSeguridades()
                    .obtenerRolOficinaDto(
                            cedula,
                            Integer.parseInt(
                                    UtilPropertiesArquetipo.getIntances()
                                            .obtenerParametro(Constantes.WSDL_SISTEMA)
                            ),
                            null,
                            UtilProperties.getIntances()
                                    .obtenerParametro(Constantes.SEGURIDAD_ENDPOINT)
                    );
    }

    private boolean puedeVerTodo(String cedula) throws ServiceException {
        try {
            List<RolMDto> roles = obtenerRoles(cedula);

            return roles != null && roles.stream()
                    .anyMatch(rol ->
                        "Administrador".equalsIgnoreCase(rol.getNombreROl())
                        || "Ventanilla".equalsIgnoreCase(rol.getNombreROl()));

        } catch (Exception ex) {
            LOG.error("Error al obtener roles del usuario con cédula: " + cedula, ex);
            throw new ServiceException("Error al obtener roles del usuario");
        }
    }

    private boolean puedeEliminar(String cedula) throws ServiceException {
        try {
            List<RolMDto> roles = obtenerRoles(cedula);

            return roles != null && roles.stream()
                    .anyMatch(rol ->
                        "Administrador".equalsIgnoreCase(rol.getNombreROl()));

        } catch (Exception ex) {
            LOG.error("Error al obtener roles del usuario con cédula: " + cedula, ex);
            throw new ServiceException("Error al obtener roles del usuario");
        }
    }
    //#endregion

    public SolicitudDto toDto(DoSolicitud solEntity) {

        SolicitudDto dto = new SolicitudDto();

        dto.setId(solEntity.getId());

        if (solEntity.getInstitucion() != null) {
            dto.setIdInstitucion(solEntity.getInstitucion().getId());
            dto.setNombreInstitucion(solEntity.getInstitucion().getNombre());
        }

        if (solEntity.getGerenciaResponsable() != null) {
            dto.setIdGerenciaResponsable(solEntity.getGerenciaResponsable().getId());
            dto.setNombreGerenciaResponsable(solEntity.getGerenciaResponsable().getNombre());

            DoDelegado delegado = repositorioDelegado.findByGerenciaId(solEntity.getGerenciaResponsable().getId());
            if (delegado != null) {
                dto.setIdDelegadoResponsable(delegado.getId());
                dto.setNombreDelegadoResponsable(delegado.getNombreFuncionario());
            }

        }

        dto.setDocumentoInstitucion(solEntity.getDocumentoInstitucion());
        dto.setDocumentoInterno(solEntity.getDocumentoInterno());
        dto.setFechaIngreso(solEntity.getFechaIngreso());
        dto.setTipoPlazo(solEntity.getTipoPlazo());
        dto.setDiasPlazo(solEntity.getDiasPlazo());
        dto.setFechaMaximaAtencion(solEntity.getFechaMaximaAtencion());
        dto.setTituloDocumento(solEntity.getTituloDocumento());
        dto.setEstado(solEntity.getEstado());
        dto.setUsuarioResolucion(solEntity.getUsuarioResolucion());
        dto.setUsuarioCreacion(solEntity.getUsuarioCreacion());
        dto.setFuncionarioReceptor(solEntity.getFuncionarioReceptor());

        // destino final
        int maxAnios = Integer.parseInt(propFile.obtenerParametro(Constantes.MAX_ANIOS_CONSERVACION));
        LocalDate fechaCorte = LocalDate.now().minusYears(maxAnios);

        if (solEntity.getFechaIngreso().isBefore(fechaCorte)) {
            dto.setDestinoFinal(Constantes.DESTINO_FINAL_ELIMINACION);
        } else {
            dto.setDestinoFinal(Constantes.DESTINO_FINAL_CONSERVACION);
        }

        // mapear solo documentos de tipo "S" (Solicitud)
        if (solEntity.getDocumentos() != null && !solEntity.getDocumentos().isEmpty()) {
            List<DocumentoDto> documentos = new ArrayList<>();

            for (DoDocumento docEntity : solEntity.getDocumentos()) {                
                if (docEntity.getTipo().equals(Constantes.TIPO_DOCUMENTO_SOLICITUD)) {

                    DocumentoDto documento = new DocumentoDto();
                    documento.setId(docEntity.getId());
                    documento.setExtension(docEntity.getExtension());
                    documento.setNombreFisico(docEntity.getNombreFisico());
                    documento.setNombreOriginal(docEntity.getNombreOriginal());
                    documento.setRuta(docEntity.getRuta());
                    documento.setTamanoBytes(docEntity.getTamanoBytes());
                    documento.setTipo(docEntity.getTipo());
                    documento.setEstado(docEntity.getEstado());
                    documento.setIdSolicitud(dto.getId());
                    documento.setIdAvance(null);
                    
                    documentos.add(documento);
                }
            }
            
            dto.setDocumentos(documentos);
        }

        return dto;
    }

}
