/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.fin.be.sigered.services;

import ec.fin.be.comunes.excepciones.ServiceException;
import ec.fin.be.sigered.dto.ResolucionDocumentoDto;
import ec.fin.be.sigered.dto.ResponseDto;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;



/**
 *
 * @author rmoran
 */
public interface DoResolucionDocumentoService {
    
     ResolucionDocumentoDto grabarDocumentoResolucion (ResolucionDocumentoDto resolucionDocumento,MultipartFile multipartFile) throws ServiceException;
     List<ResolucionDocumentoDto> listarDocumento(int resolucion) throws ServiceException;
     boolean eliminarDocumentoResolucion(int id) throws ServiceException;
     ResponseDto obtenerDocumentoString(int id) throws ServiceException;
     
}
