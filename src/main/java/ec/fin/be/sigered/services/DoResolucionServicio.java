/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.fin.be.sigered.services;

import ec.fin.be.comunes.excepciones.ServiceException;
import ec.fin.be.sigered.dto.ResolucionDto;
import ec.fin.be.sigered.dto.ResolucionFiltroDto;
import ec.fin.be.sigered.dto.ResponseDto;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author rmoran
 */
public interface DoResolucionServicio {
    
   ResponseDto guardarResolucion(ResolucionDto resolucionDto, MultipartFile multiPartFile)throws ServiceException;
   
   ResolucionDto buscarResolucion(Integer id)throws ServiceException;
   
   List<ResolucionDto>listarResolucionesByFechaRegistro(ResolucionFiltroDto filtro)throws ServiceException;
   
   List<ResolucionDto>listarResolucionesByFechaResolucion(ResolucionFiltroDto filtro)throws ServiceException;
   
   boolean existeResolucion(String resolucion);
   
   List<ResolucionDto> buscarResolucionFiltro(ResolucionFiltroDto filtro)throws ServiceException;
   
   List<ResolucionDto> listarResolucionInsubsistente(String estado);
   
   ResponseDto  reporteResolucion(ResolucionFiltroDto filtro)throws ServiceException;
}
