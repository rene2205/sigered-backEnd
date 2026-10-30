/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.fin.be.sigered.services;

import ec.fin.be.comunes.excepciones.ServiceException;
import ec.fin.be.sigered.dto.ResponseDto;
import ec.fin.be.sigered.dto.SolicitudCambioDto;
import java.util.List;

/**
 *
 * @author rmoran
 */
public interface DoSolicitudCambioResolucionServicio {
    
    ResponseDto guardarResolucion(SolicitudCambioDto solicitudCambio)throws ServiceException;
    List<SolicitudCambioDto>consultaResolucionPorEstado(SolicitudCambioDto solicitudCambio)throws ServiceException;
    SolicitudCambioDto bucarSolicitudCambio(Integer idSolicitud);
    
    List<SolicitudCambioDto>consultaSolicitudCambioAprobada(SolicitudCambioDto solicitudCambio)throws ServiceException;
    
    
}
