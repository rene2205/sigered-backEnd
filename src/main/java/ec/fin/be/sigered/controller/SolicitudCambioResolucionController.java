/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.fin.be.sigered.controller;

import ec.fin.be.comunes.excepciones.ServiceException;
import ec.fin.be.sigered.dto.ResponseDto;
import ec.fin.be.sigered.dto.SolicitudCambioDto;
import ec.fin.be.sigered.logger.LoggerSigered;
import ec.fin.be.sigered.services.DoSolicitudCambioResolucionServicio;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author rmoran
 */
@RestController
@RequestMapping("/solicitudCambio")
public class SolicitudCambioResolucionController {
    private static final Logger LOG = LoggerSigered.getLogger(SolicitudCambioResolucionController.class);
    @Autowired
    DoSolicitudCambioResolucionServicio servicioSolicitudCambio;
    
    @PostMapping("/grabarSolicitud")
    public ResponseDto grabarSolicitud(@RequestBody SolicitudCambioDto solicitudCambio) throws ServiceException{
        LOG.info("grabando Solicitud de Cambio de Resolución Administrativa");
        return servicioSolicitudCambio.guardarResolucion(solicitudCambio);
    }
    
    @PostMapping("/listarSolicitudCambiosPendientes")
    public List<SolicitudCambioDto> listarSolicitudCambiosPendientes(@RequestBody SolicitudCambioDto solicitudCambio)throws ServiceException{
        return servicioSolicitudCambio.consultaResolucionPorEstado(solicitudCambio);
    }
    
     @PostMapping("/listarSolicitudCambiosAprobada")
    public List<SolicitudCambioDto> listarSolicitudCambiosAprobada(@RequestBody SolicitudCambioDto solicitudCambio)throws ServiceException{
        return servicioSolicitudCambio.consultaSolicitudCambioAprobada(solicitudCambio);
    }
    
}
