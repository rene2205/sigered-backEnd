/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.fin.be.sigered.primary.repository;

import ec.fin.be.sigered.primary.entity.DoResolucion;
import ec.fin.be.sigered.primary.entity.DoSolicitudCambioResolucion;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author rmoran
 */

public interface DoSolicitudCambioResolucionRepositorio extends JpaRepository<DoSolicitudCambioResolucion,Integer> {
    
    @Override
    DoSolicitudCambioResolucion save(DoSolicitudCambioResolucion solicitudCambioResolucion);
    
    DoSolicitudCambioResolucion findByScId(Integer scId);
    
    List<DoSolicitudCambioResolucion> findByScEstadoSolicitud(Character scEstadoSolicitud);
    
    List<DoSolicitudCambioResolucion> findByScResolucionAndScUsuarioSolicitanteAndScEstadoSolicitudAndScFechaRespuestaBetween(DoResolucion scResolucion,String scUsuarioSolicitante ,Character estado, LocalDateTime fechaInicio, LocalDateTime fechaFin );
    
    boolean existsByScResolucionAndScUsuarioSolicitanteAndScEstadoSolicitud(DoResolucion scResolucion,String scUsuarioSolicitante ,Character estado);
    
    @Query(value="select * from do_solicitud_cambio_resolucion where sc_estado_solicitud='A' and sc_resolucion=?1 and sc_usuario_solicitante=?2 and sc_fecha_respuesta + INTERVAL 24 HOUR >= now() ", nativeQuery = true)
    List<DoSolicitudCambioResolucion> buscarSolicitudesAprobadas(Integer resolucion, String usuario);
    
    
}
