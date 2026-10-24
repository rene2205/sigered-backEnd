package ec.fin.be.sigered.controller;

import java.security.Principal;
import java.util.List;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ec.fin.be.comunes.excepciones.ServiceException;
import ec.fin.be.sigered.dto.DocumentoDto;
import ec.fin.be.sigered.dto.ResponseDto;
import ec.fin.be.sigered.logger.LoggerSigered;
import ec.fin.be.sigered.services.DoDocumentoServicio;
import ec.fin.be.sigered.utils.Constantes;
import ec.fin.be.sigered.utils.Constantes.CODIGOS_RESPUESTA;
import ec.fin.be.sigered.utils.Utils;

@RestController
@RequestMapping("/documentos")
public class DocumentoController {
    private static final Logger LOG = LoggerSigered.getLogger(DocumentoController.class);

    @Autowired
    private DoDocumentoServicio documentoServicio;

    /**
     * Obtener documentos por solicitud
     */
    @GetMapping("/solicitud/{idSolicitud}/{tipo}")
    public List<DocumentoDto> obtenerDocumentosPorSolicitud(
        @PathVariable int idSolicitud, 
        @PathVariable String tipo,
        @RequestParam(required = false) String estado) throws ServiceException {

        return documentoServicio.obtenerDocumentosPorSolicitud(idSolicitud, tipo, estado);
    }

    /**
     * Obtener documentos por avance
     */
    @GetMapping("/avance/{idAvance}")
    public List<DocumentoDto> obtenerDocumentosPorAvance(@PathVariable int idAvance, @RequestParam(required = false) String estado) throws ServiceException {

        return documentoServicio.obtenerDocumentosPorAvance(idAvance, estado);
    }

    /**
     * Obtener documento por ID
     */
    @GetMapping("/{id}")
    public DocumentoDto obtenerDocumento(@PathVariable int id) throws ServiceException {

        return documentoServicio.obtenerDocumento(id);
    }

    /**
     * Crear o actualizar documento
     */
    @PostMapping
    public ResponseDto guardarDocumento(@RequestBody DocumentoDto dto, Principal principal) throws ServiceException {
        ResponseDto response = Utils.devolverRespuesta(CODIGOS_RESPUESTA.ER_ERROR);

        try {
            String usuario = (principal != null) ? principal.getName() : Constantes.SYSTEM_USER;

            int id = documentoServicio.guardarDocumento(dto, usuario);    
            response = Utils.devolverRespuesta(CODIGOS_RESPUESTA.OK_00);
            response.setData(id);
            return response;

        } catch (Exception e) {
            LOG.error("Error al guardar documento", e);
            response.setMensajeError(e.getMessage());
            return response;
        }
        
    }

    @GetMapping("/{id}/descargar-base64")
    public ResponseDto descargarBase64(@PathVariable int id) throws ServiceException {
        try {
            return documentoServicio.descargarDocumentoBase64(id);
        } catch (Exception e) {
            LOG.error("Error al descargar documento", e);
            ResponseDto response = Utils.devolverRespuesta(CODIGOS_RESPUESTA.ER_ERROR);
            response.setMensajeError(e.getMessage());
            return response;
        }
    }
    
}