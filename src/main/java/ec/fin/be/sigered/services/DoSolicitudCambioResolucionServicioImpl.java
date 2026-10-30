/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.fin.be.sigered.services;

import ec.fin.be.comunes.excepciones.ServiceException;
import ec.fin.be.sigered.dto.ResponseDto;
import ec.fin.be.sigered.dto.SolicitudCambioDto;
import ec.fin.be.sigered.logger.LoggerSigered;
import ec.fin.be.sigered.primary.entity.DoResolucion;
import ec.fin.be.sigered.primary.entity.DoSolicitudCambioResolucion;
import ec.fin.be.sigered.primary.repository.DoResolucionesRepositorio;
import ec.fin.be.sigered.primary.repository.DoSolicitudCambioResolucionRepositorio;
import ec.fin.be.sigered.utils.Constantes;
import ec.fin.be.sigered.utils.Utils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author rmoran
 */
@Service
public class DoSolicitudCambioResolucionServicioImpl implements DoSolicitudCambioResolucionServicio {
    private static final Logger LOG = LoggerSigered.getLogger(DoSolicitudCambioResolucionServicioImpl.class);
     @Autowired
    DoResolucionesRepositorio repositorioResoluciones;
    @Autowired
    DoSolicitudCambioResolucionRepositorio doSolicitudCambioResolucionRepositorio;
    @Override
    public ResponseDto guardarResolucion(SolicitudCambioDto solicitudCambio) throws ServiceException {
        ResponseDto response = Utils.devolverRespuesta(Constantes.CODIGOS_RESPUESTA.ER_ERROR);
        
        try{
            DoSolicitudCambioResolucion solicitudCambioResolucion= new DoSolicitudCambioResolucion();
            DoResolucion doResolucion= repositorioResoluciones.findByReId(solicitudCambio.getResolucion());
            doResolucion.setReId(solicitudCambio.getResolucion());
                
            boolean esActualizacion = solicitudCambio.getIdSolicitud() != null && solicitudCambio.getIdSolicitud() > 0;
            boolean existSolPendiente= doSolicitudCambioResolucionRepositorio.existsByScResolucionAndScUsuarioSolicitanteAndScEstadoSolicitud(doResolucion, solicitudCambio.getUsuarioSolicitante(),Constantes.ESTADO_SOL_EN_PROCESO.charAt(0));
            
            
            if (esActualizacion){
                solicitudCambioResolucion= doSolicitudCambioResolucionRepositorio.findByScId(solicitudCambio.getIdSolicitud());
                
                solicitudCambioResolucion.setScFechaRespuesta(LocalDateTime.now());
                solicitudCambioResolucion.setScEstadoSolicitud(solicitudCambio.getEstadoSolicitud().charAt(0));
                solicitudCambioResolucion.setScObservacion(solicitudCambio.getObservacion());
                doSolicitudCambioResolucionRepositorio.save(solicitudCambioResolucion);
                response = Utils.devolverRespuesta(Constantes.CODIGOS_RESPUESTA.OK_00);
                LOG.debug("Actualizando solicitud - id: " + solicitudCambio.getIdSolicitud() );
            }else {
                
                if (!existSolPendiente){
                    LOG.debug("Creando Solicitud de Cambio " );
                    solicitudCambioResolucion.setScId(solicitudCambio.getIdSolicitud());
                    solicitudCambioResolucion.setScResolucion(doResolucion);
                    solicitudCambioResolucion.setScNombreSolicitante(solicitudCambio.getNombreSolicitante());
                    solicitudCambioResolucion.setScNumResolucion(solicitudCambio.getNumResolucion());
                    solicitudCambioResolucion.setScUsuarioSolicitante(solicitudCambio.getUsuarioSolicitante());
                    solicitudCambioResolucion.setScIdentificacionSolicitante(solicitudCambio.getIdentificacionSolicitante());
                    solicitudCambioResolucion.setScNombreSolicitante(solicitudCambio.getNombreSolicitante());
                    solicitudCambioResolucion.setScCorreoSolicitante(solicitudCambio.getCorreoSolicitante());
                    solicitudCambioResolucion.setScUsuarioAdministrador(solicitudCambio.getUsuarioAdministrador());
                    solicitudCambioResolucion.setScIdentificacionAdministrador(solicitudCambio.getIdentificacionAdministrador());
                    solicitudCambioResolucion.setScObservacion(solicitudCambio.getObservacion());
                    solicitudCambioResolucion.setScFechaSolicitud(LocalDateTime.now());
                    solicitudCambioResolucion.setScEstadoSolicitud(Constantes.ESTADO_SOL_EN_PROCESO.charAt(0));
                    solicitudCambioResolucion = doSolicitudCambioResolucionRepositorio.save(solicitudCambioResolucion);
                    LOG.debug("Solicitud de Cambio Creada con exito "+solicitudCambioResolucion.getScId()  );
                    response = Utils.devolverRespuesta(Constantes.CODIGOS_RESPUESTA.OK_00);
                    response.setData(solicitudCambioResolucion.getScId());
                    entityToDto(solicitudCambioResolucion);
                }else{
                    response= Utils.devolverRespuesta(Constantes.CODIGOS_RESPUESTA.ER_EXISTS);
                    response.setMensajeError("Ya existe una solicitud de cambio con estado Pendiente");
                }
                
                
                
                
            }
            
            
            
             
        }catch(Exception ex){
            LOG.error("Error al guardar Solicitud de Cambio " + ex);
        }
        
        return response;
    }

    @Override
    public List<SolicitudCambioDto> consultaResolucionPorEstado(SolicitudCambioDto solicitudCambio) throws ServiceException {
        List<DoSolicitudCambioResolucion> listadoSolicitudes= doSolicitudCambioResolucionRepositorio.findByScEstadoSolicitud(solicitudCambio.getEstadoSolicitud().charAt(0));
        
        return listadoSolicitudes.stream().map(this::entityToDto).collect(Collectors.toList());
    }
    
    private SolicitudCambioDto entityToDto (DoSolicitudCambioResolucion solicitudCambio){
        SolicitudCambioDto solicitudCambioDto= new SolicitudCambioDto();
        
        solicitudCambioDto.setResolucion(solicitudCambio.getScResolucion().getReId());
        solicitudCambioDto.setIdSolicitud(solicitudCambio.getScId());
        solicitudCambioDto.setNumResolucion(solicitudCambio.getScNumResolucion());
        solicitudCambioDto.setUsuarioSolicitante(solicitudCambio.getScUsuarioSolicitante());
        solicitudCambioDto.setIdentificacionSolicitante(solicitudCambio.getScIdentificacionSolicitante());
        solicitudCambioDto.setNombreSolicitante(solicitudCambio.getScNombreSolicitante());
        solicitudCambioDto.setCorreoSolicitante(solicitudCambio.getScCorreoSolicitante());
        solicitudCambioDto.setUsuarioSolicitante(solicitudCambio.getScUsuarioSolicitante());
        solicitudCambioDto.setUsuarioAdministrador(solicitudCambio.getScUsuarioAdministrador());
        solicitudCambioDto.setIdentificacionAdministrador(solicitudCambio.getScIdentificacionAdministrador());
        solicitudCambioDto.setObservacion(solicitudCambio.getScObservacion());
        solicitudCambioDto.setFechaSolicitud(solicitudCambio.getScFechaSolicitud());
        solicitudCambioDto.setFechaRespuesta(solicitudCambio.getScFechaRespuesta());
        solicitudCambioDto.setEstadoSolicitud(solicitudCambio.getScEstadoSolicitud().toString());
        
        return solicitudCambioDto;
    }

    @Override
    public SolicitudCambioDto bucarSolicitudCambio(Integer idSolicitud) {
        DoSolicitudCambioResolucion solicitudCambioResolucion;
        solicitudCambioResolucion = doSolicitudCambioResolucionRepositorio.findByScId(idSolicitud);

        return entityToDto(solicitudCambioResolucion);
    }

    @Override
    public List<SolicitudCambioDto> consultaSolicitudCambioAprobada(SolicitudCambioDto solicitudCambio) throws ServiceException {
        List<DoSolicitudCambioResolucion> listadoSolicitudes= doSolicitudCambioResolucionRepositorio.buscarSolicitudesAprobadas(solicitudCambio.getResolucion(), solicitudCambio.getUsuarioSolicitante());
       
        return listadoSolicitudes.stream().map(this::entityToDto).collect(Collectors.toList());
        
    }
    
}
