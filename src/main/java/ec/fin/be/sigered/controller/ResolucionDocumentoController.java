/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.fin.be.sigered.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ec.fin.be.comunes.excepciones.ServiceException;
import ec.fin.be.sigered.dto.ResolucionDocumentoDto;
import ec.fin.be.sigered.dto.ResponseDto;
import ec.fin.be.sigered.logger.LoggerSigered;
import ec.fin.be.sigered.services.DoResolucionDocumentoService;
import ec.fin.be.sigered.utils.Constantes;
import ec.fin.be.sigered.utils.Utils;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author rmoran
 */

@RestController
@RequestMapping("/resolucionDocumento")
public class ResolucionDocumentoController {
       private static final Logger LOG = LoggerSigered.getLogger(ResolucionDocumentoController.class);
       @Autowired
       private ObjectMapper objectMapper;
       @Autowired
       private DoResolucionDocumentoService servicioResolucionDocumento;
       
       
    @PostMapping(value="/grabarResolucionDoc",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDto grabarResolucion(@RequestParam("resolucionDocumento") String resolucionJson,
                                        @RequestParam(value = "multipartFile", required = false) MultipartFile multipartFile
                                     ){
        ResponseDto response = Utils.devolverRespuesta(Constantes.CODIGOS_RESPUESTA.ER_ERROR);
        ResolucionDocumentoDto resolucionDto;
        
        try {
            resolucionDto = objectMapper.readValue(resolucionJson,ResolucionDocumentoDto.class);
            
            resolucionDto = servicioResolucionDocumento.grabarDocumentoResolucion(resolucionDto,multipartFile);
            
            response = Utils.devolverRespuesta(Constantes.CODIGOS_RESPUESTA.OK_00);
            response.setData(resolucionDto.getIdDocumento());
            return response;
        }catch (Exception sex){
            response.setMensajeError(sex.toString());
            LOG.error("[ERROR AL GUARDAR DOCUMENTO EN RESOLUCION ADMINISTRATIVA] "+ sex);
        }
        
       return response;
    }
    
    @GetMapping("/listarDocumentoResolucion/{idResolucion}")   
    public List<ResolucionDocumentoDto> listarDocumentoResolucion(@PathVariable("idResolucion")int idResolucion)throws ServiceException{
        return servicioResolucionDocumento.listarDocumento(idResolucion);
    }
    
    @DeleteMapping("/eliminarDocumentoResolucion/{idDocumento}")
    public boolean eliminarDocumentoResolucion(@PathVariable("idDocumento") int idDocumento)throws ServiceException{
        return servicioResolucionDocumento.eliminarDocumentoResolucion(idDocumento);
    }
    
    @GetMapping("/descargarDocumentoString/{idDocumento}")
    public ResponseDto descargarDocumentoString(@PathVariable("idDocumento") int idDocumento)throws ServiceException{
        return servicioResolucionDocumento.obtenerDocumentoString(idDocumento);
    }
}
