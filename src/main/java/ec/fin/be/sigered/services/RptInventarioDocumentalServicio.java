package ec.fin.be.sigered.services;

import org.springframework.web.bind.annotation.RequestBody;

import ec.fin.be.comunes.excepciones.ServiceException;
import ec.fin.be.sigered.dto.ResponseDto;
import ec.fin.be.sigered.dto.SolicitudFiltroDto;

public interface RptInventarioDocumentalServicio {

    ResponseDto generarReporteInventarioDocumentos(@RequestBody SolicitudFiltroDto filtro, String cedulaUsuario) throws ServiceException;

}