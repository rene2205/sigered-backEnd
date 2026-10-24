/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.fin.be.sigered.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ec.fin.be.comunes.excepciones.ServiceException;
import ec.fin.be.sigered.dto.ResolucionDto;
import ec.fin.be.sigered.dto.ResolucionFiltroDto;
import ec.fin.be.sigered.dto.ResponseDto;
import ec.fin.be.sigered.logger.LoggerSigered;
import ec.fin.be.sigered.services.DoResolucionServicio;
import ec.fin.be.sigered.utils.Constantes;
import ec.fin.be.sigered.utils.Utils;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author rmoran
 */

@RestController
@RequestMapping("/resolucion")
public class ResolucionController {
    private static final Logger LOG = LoggerSigered.getLogger(ResolucionController.class);
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private DoResolucionServicio servicioResolucion;
    
    @PostMapping(value="/grabarResolucion",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDto grabarResolucion(@RequestParam("resolucion") String resolucionJson,
                                        @RequestParam(value = "multipartFile", required = false) MultipartFile multipartFile
                                     ){
        ResponseDto response = Utils.devolverRespuesta(Constantes.CODIGOS_RESPUESTA.ER_ERROR);
        ResolucionDto resolucionDto;
        
        try {
            resolucionDto = objectMapper.readValue(resolucionJson,ResolucionDto.class);
            LOG.info(" grabarResolucion "+ resolucionDto.getId());
            
            response= servicioResolucion.guardarResolucion(resolucionDto,multipartFile);
            
           
            return response;
        }catch (Exception ex){
            response.setMensajeError(ex.toString());
            LOG.error("[ERROR AL GUARDAR RESOLUCION ADMINISTRATIVA] "+ ex);
        }
        
       return response;
    }
    
    @GetMapping("/buscarResolucion/{id}")
    public ResolucionDto buscarResolucion(@PathVariable("id") Integer id )throws ServiceException{
          LOG.info("buscarResolucion "+ id);
        return servicioResolucion.buscarResolucion(id);
    }
    
    @GetMapping("/buscarResolucionFechaRegistro")
    public List <ResolucionDto> buscarResolucionFechaRegistro(@RequestBody ResolucionFiltroDto filtro)throws ServiceException{
          LOG.info("buscarResolucionFecha "+ filtro.getFechaInicial() + " " + filtro.getFechaFinal());
        return servicioResolucion.listarResolucionesByFechaRegistro(filtro);
    }
    
    @GetMapping("/buscarResolucionFechaResolucion")
    public List <ResolucionDto> buscarResolucionFechaResolucion(@RequestBody ResolucionFiltroDto filtro)throws ServiceException{
          LOG.info("buscarResolucionFecha "+ filtro.getFechaInicial() + " " + filtro.getFechaFinal());
        return servicioResolucion.listarResolucionesByFechaResolucion(filtro);
    }
    
    
    @GetMapping("/existeNumResolucion/{resolucion}")
    public boolean existeNumResolucion(@PathVariable("resolucion") String resolucion )throws ServiceException{
          LOG.info("existeNumResolucion "+ resolucion);
        return servicioResolucion.existeResolucion(resolucion);
    }
    
    @PostMapping("/buscarResolucionFiltro")
    public List<ResolucionDto>  buscarResolucionFiltro (@RequestBody ResolucionFiltroDto filtro)throws ServiceException{
        return servicioResolucion.buscarResolucionFiltro(filtro);
        
    }
    
     @PostMapping("/buscarResolucionInsubsistente/{id}")
    public List<ResolucionDto>  buscarResolucionInsubsistente (@PathVariable("id") Integer id )throws ServiceException{
        return servicioResolucion.listarResolucionInsubsistente("N");
        
    }
    
    @PostMapping("/reporteResolucionFiltro")
    public ResponseDto  reporteResolucionFiltro (@RequestBody ResolucionFiltroDto filtro)throws ServiceException{
        return servicioResolucion.reporteResolucion(filtro);
        
    }
    
        
    
}
