/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.fin.be.sigered.primary.repository;


import ec.fin.be.sigered.primary.entity.DoGerencia;
import ec.fin.be.sigered.primary.entity.DoSolicitud;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author wochoa
 */
public interface DoSolicitudRepositorio extends JpaRepository<DoSolicitud, Integer>,
        JpaSpecificationExecutor<DoSolicitud>  {
    
    @Override
    DoSolicitud save(DoSolicitud solicitud);
    
    DoSolicitud findById(int id);

    DoSolicitud findByDocumentoInterno(String documentoInterno);
    
    List<DoSolicitud> findAll();

    List<DoSolicitud> findByFechaIngresoBefore(LocalDate fechaCorte);

    int countByFechaIngresoBeforeAndGerenciaResponsable(LocalDate fechaCorte, DoGerencia gerenciaResponsable);
    
}
