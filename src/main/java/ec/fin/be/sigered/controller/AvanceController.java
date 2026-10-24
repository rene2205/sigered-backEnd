package ec.fin.be.sigered.controller;

import java.security.Principal;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import ec.fin.be.comunes.excepciones.ServiceException;
import ec.fin.be.sigered.dto.AvanceDto;
import ec.fin.be.sigered.dto.ResponseDto;
import ec.fin.be.sigered.logger.LoggerSigered;
import ec.fin.be.sigered.services.DoAvanceServicio;
import ec.fin.be.sigered.utils.Utils;
import ec.fin.be.sigered.utils.Constantes;
import ec.fin.be.sigered.utils.Constantes.CODIGOS_RESPUESTA;

@RestController
@RequestMapping("/avances")
public class AvanceController {
    private static final Logger LOG = LoggerSigered.getLogger(AvanceController.class);

    @Autowired
    private DoAvanceServicio avanceServicio;
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Obtener avances por solicitud
     */
    @GetMapping
    public List<AvanceDto> obtenerAvances(@RequestParam int idSolicitud) throws ServiceException {

        return avanceServicio.obtenerAvances(idSolicitud);
    }

    /**
     * Obtener un avance por ID
     */
    @GetMapping("/{id}")
    public AvanceDto obtenerAvance(@PathVariable int id) throws ServiceException {

        return avanceServicio.obtenerAvance(id);
    }

    /**
     * Crear o actualizar avance
     */
    @PostMapping
    public ResponseDto guardarAvance(@RequestBody AvanceDto dto, Principal principal) throws ServiceException {
        ResponseDto response = Utils.devolverRespuesta(CODIGOS_RESPUESTA.ER_ERROR);

        try {
            String usuario = (principal != null) ? principal.getName() : Constantes.SYSTEM_USER;
    
            int id = avanceServicio.guardarAvance(dto, usuario);
            response = Utils.devolverRespuesta(CODIGOS_RESPUESTA.OK_00);
            response.setData(id);
            return response;
            
        } catch (Exception e) {
            LOG.error("Error al guardar avance", e);
            response.setMensajeError(e.getMessage());
            return response;
        }
    }

    @PostMapping(value = "/con-documento", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDto guardarAvanceConDocumento(
            @RequestParam("avance") String avanceJson,
            @RequestParam(value = "multipartFile", required = false) MultipartFile multipartFile,
            Principal principal) throws ServiceException {
        ResponseDto response = Utils.devolverRespuesta(CODIGOS_RESPUESTA.ER_ERROR);
        try {
            String usuario = (principal != null) ? principal.getName() : Constantes.SYSTEM_USER;

            // Convertir JSON → DTO
            AvanceDto dto = objectMapper.readValue(avanceJson, AvanceDto.class);

            // Llamar servicio
            int id = avanceServicio.guardarAvanceConDocumento(dto, multipartFile, usuario);

            response = Utils.devolverRespuesta(CODIGOS_RESPUESTA.OK_00);
            response.setData(id);
            return response;

        } catch (Exception e) {
            LOG.error("Error al guardar avance con documento", e);
            response.setMensajeError(e.getMessage());
            return response;
        }
        
    }

    /**
     * Eliminación lógica
     */
    @DeleteMapping("/{id}")
    public ResponseDto eliminarAvance(@PathVariable int id, Principal principal) throws ServiceException {
        ResponseDto response = Utils.devolverRespuesta(CODIGOS_RESPUESTA.ER_ERROR);

        try {
            String usuario = (principal != null) ? principal.getName() : Constantes.SYSTEM_USER;

            boolean result = avanceServicio.eliminarAvance(id, usuario);
            response = Utils.devolverRespuesta(CODIGOS_RESPUESTA.OK_00);
            response.setData(result);
            return response;

        } catch (Exception e) {
            LOG.error("Error al eliminar avance", e);
            response.setMensajeError(e.getMessage());
            return response;
        }

        
    }

}