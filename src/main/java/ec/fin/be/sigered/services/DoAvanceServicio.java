package ec.fin.be.sigered.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import ec.fin.be.comunes.excepciones.ServiceException;
import ec.fin.be.sigered.dto.AvanceDto;

public interface DoAvanceServicio {

    List<AvanceDto> obtenerAvances(int idSolicitud) throws ServiceException;

    AvanceDto obtenerAvance(int id) throws ServiceException;

    int guardarAvance(AvanceDto avance, String usuario) throws ServiceException;

    int guardarAvanceConDocumento(AvanceDto avance, MultipartFile multipartFile, String usuario) throws ServiceException;

    boolean eliminarAvance(int id, String usuario) throws ServiceException;
    
}
