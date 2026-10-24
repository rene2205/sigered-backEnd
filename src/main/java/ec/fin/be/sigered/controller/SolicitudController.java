package ec.fin.be.sigered.controller;

import java.security.Principal;
import java.util.List;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import ec.fin.be.comunes.excepciones.ServiceException;
import ec.fin.be.sigered.dto.ResponseDto;
import ec.fin.be.sigered.dto.SolicitudDto;
import ec.fin.be.sigered.dto.SolicitudFiltroDto;
import ec.fin.be.sigered.logger.LoggerSigered;
import ec.fin.be.sigered.services.DoSolicitudServicio;
import ec.fin.be.sigered.services.RptInventarioDocumentalServicio;
import ec.fin.be.sigered.utils.Constantes;
import ec.fin.be.sigered.utils.Constantes.CODIGOS_RESPUESTA;
import ec.fin.be.sigered.utils.Utils;

@RestController
@RequestMapping({"/solicitudes"})
public class SolicitudController {
    private static final Logger LOG = LoggerSigered.getLogger(SolicitudController.class);

    @Autowired
    private DoSolicitudServicio solicitudServicio;
    @Autowired
    private RptInventarioDocumentalServicio inventarioDocumentalServicio;
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Obtener todas las solicitudes
     */
    @GetMapping("/obtenerSolicitudes")
    public List<SolicitudDto> obtenerSolicitudes() throws ServiceException {
        return solicitudServicio.obtenerSolicitudes();    

    }

    /**
     * Obtener solicitud por ID
     */
    @GetMapping("/{id}")
    public SolicitudDto obtenerSolicitud(@PathVariable("id") int id) throws ServiceException {
        return solicitudServicio.obtenerSolicitud(id);

    }

    /**
     * Busqueda advanzada de solicitudes
     */
    @PostMapping("/buscar")
    public ResponseDto buscarSolicitudes(@RequestBody SolicitudFiltroDto filtro) throws ServiceException {
        ResponseDto response = Utils.devolverRespuesta(CODIGOS_RESPUESTA.ER_ERROR);

        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String cedulaUsuario = (String) authentication.getDetails();

            List<SolicitudDto> solicitudes = solicitudServicio.buscarSolicitudes(filtro, Constantes.PAGE_SIZE, cedulaUsuario);
            response = Utils.devolverRespuesta(CODIGOS_RESPUESTA.OK_00);
            response.setData(solicitudes);

            return response;

        } catch (Exception e) {
            LOG.error("Error al buscar solicitudes", e);
            response.setMensajeError(e.getMessage());
            return response;
        }
    }

    @PostMapping("/generarReporteInventarioDocumentos") 
    public ResponseDto generarReporteInventarioDocumentos(@RequestBody SolicitudFiltroDto filtro) throws ServiceException {
        ResponseDto response = Utils.devolverRespuesta(CODIGOS_RESPUESTA.ER_ERROR);

        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String cedulaUsuario = (String) authentication.getDetails();

            response = inventarioDocumentalServicio.generarReporteInventarioDocumentos(filtro, cedulaUsuario);
            return response;

        } catch (Exception e) {
            LOG.error("Error al generar reporte", e);
            response.setMensajeError(e.getMessage());
            return response;
        }
    }

    /**
     * Crear o actualizar solicitud
     */
    @PostMapping
    public ResponseDto guardarSolicitud(@RequestBody SolicitudDto dto, Principal principal) throws ServiceException {
        ResponseDto response = Utils.devolverRespuesta(CODIGOS_RESPUESTA.ER_ERROR);

        try {
            String usuario = (principal != null) ? principal.getName() : Constantes.SYSTEM_USER;

            int id = solicitudServicio.guardarSolicitud(dto, usuario);
            response = Utils.devolverRespuesta(CODIGOS_RESPUESTA.OK_00);
            response.setData(id);
            return response;

        } catch (Exception e) {
            LOG.error("Error al guardar solicitud", e);
            response.setMensajeError(e.getMessage());
            return response;
        }
        
    }

    @PostMapping(value = "/con-documentos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDto guardarSolicitudConDocumentos(
            @RequestPart("solicitud") String solicitudJson,
            @RequestParam(value = "multipartFiles", required = false) MultipartFile[] multipartFiles,
            Principal principal) throws ServiceException {

        ResponseDto response = Utils.devolverRespuesta(CODIGOS_RESPUESTA.ER_ERROR);

        try {
            String usuario = (principal != null) ? principal.getName() : Constantes.SYSTEM_USER;

            // Convertir JSON → DTO
            SolicitudDto dto = objectMapper.readValue(solicitudJson, SolicitudDto.class);

            // Llamar servicio
            int id = solicitudServicio.guardarSolicitudConDocumentos(dto, multipartFiles, usuario);

            response = Utils.devolverRespuesta(CODIGOS_RESPUESTA.OK_00);
            response.setData(id);
            return response;

        } catch (Exception e) {
            LOG.error("Error al guardar solicitud con documento", e);
            response.setMensajeError(e.getMessage());
            return response;
        }
        
    }
    
    /**
     * Deshabilitar solicitud
     */
    @DeleteMapping("/deshabilitar/{id}/{razonAnulacion}")
    public ResponseDto deshabilitarSolicitud(@PathVariable int id, @PathVariable String razonAnulacion, Principal principal) throws ServiceException {
        ResponseDto response = Utils.devolverRespuesta(CODIGOS_RESPUESTA.ER_ERROR);

        try {
            String usuario = (principal != null) ? principal.getName() : Constantes.SYSTEM_USER;

            boolean result = solicitudServicio.deshabilitarSolicitud(id, razonAnulacion, usuario);
            response = Utils.devolverRespuesta(CODIGOS_RESPUESTA.OK_00);
            response.setData(result);
            return response;

        } catch (Exception e) {
            LOG.error("Error al eliminar solicitud", e);
            response.setMensajeError(e.getMessage());
            return response;
        }
        
    }

    @GetMapping("/obtenerCantidadSolicitudesAEliminar/{cedulaDelegado}")
    public int obtenerCantidadSolicitudesAEliminar(@PathVariable String cedulaDelegado) throws ServiceException {
        return solicitudServicio.obtenerCantidadSolicitudesAEliminar(cedulaDelegado);            
    }

    /**
     * Eliminación definitiva
     */
    @DeleteMapping("/eliminar/{id}")
    public ResponseDto eliminarDefinitivamenteSolicitudYAvances(@PathVariable int id, Principal principal) throws ServiceException {
        ResponseDto response = Utils.devolverRespuesta(CODIGOS_RESPUESTA.ER_ERROR);

        try {
            String usuario = (principal != null) ? principal.getName() : Constantes.SYSTEM_USER;
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String cedulaUsuario = (String) authentication.getDetails();

            boolean result = solicitudServicio.eliminarDefinitivamenteSolicitudYAvances(id, usuario, cedulaUsuario);
            response = Utils.devolverRespuesta(CODIGOS_RESPUESTA.OK_00);
            response.setData(result);
            return response;

        } catch (Exception e) {
            LOG.error("Error al eliminar definitivamente solicitud y avances", e);
            response.setMensajeError(e.getMessage());
            return response;
        }
    }

    /**
     * reabrir solicitud (cambia estado de "cerrada" a "en proceso")
     */
    @PostMapping("/reabrir/{id}")
    public ResponseDto reabrirSolicitud(@PathVariable int id, Principal principal) throws ServiceException {
        ResponseDto response = Utils.devolverRespuesta(CODIGOS_RESPUESTA.ER_ERROR);

        try {
            String usuario = (principal != null) ? principal.getName() : Constantes.SYSTEM_USER;

            boolean result = solicitudServicio.reabrirSolicitud(id, usuario);
            response = Utils.devolverRespuesta(CODIGOS_RESPUESTA.OK_00);
            response.setData(result);
            return response;

        } catch (Exception e) {
            LOG.error("Error al reabrir solicitud", e);
            response.setMensajeError(e.getMessage());
            return response;
        }
        
    }
    
}
