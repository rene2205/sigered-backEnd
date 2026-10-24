package ec.fin.be.sigered.services;

import java.util.List;

import ec.fin.be.comunes.excepciones.ServiceException;
import ec.fin.be.sigered.dto.DocumentoDto;
import ec.fin.be.sigered.dto.ResponseDto;

public interface DoDocumentoServicio {

    List<DocumentoDto> obtenerDocumentosPorSolicitud(int idSolicitud, String tipo, String estado) throws ServiceException;

    List<DocumentoDto> obtenerDocumentosPorAvance(int idAvance, String estado) throws ServiceException;

    DocumentoDto obtenerDocumento(int id) throws ServiceException;

    int guardarDocumento(DocumentoDto documento, String usuario) throws ServiceException;

    ResponseDto descargarDocumentoBase64(int id) throws ServiceException;
    
    boolean eliminarDocumentos(int idSolicitud, String tipo, String usuario) throws ServiceException;
}
