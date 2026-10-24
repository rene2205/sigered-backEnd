/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.fin.be.sigered.primary.repository;

import ec.fin.be.sigered.primary.entity.DoResolucion;
import java.time.LocalDate;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
/**
 *
 * @author rmoran
 */
public interface DoResolucionesRepositorio extends JpaRepository<DoResolucion,Integer>,JpaSpecificationExecutor<DoResolucion>{
    
     @Override
     DoResolucion save(DoResolucion doResolucion);
     
     DoResolucion findByReId(Integer reId);
     
     DoResolucion findByReResolucion(String reResolucion);
     
     boolean existsByReResolucion(String reResolucion);
     
     List<DoResolucion> findByReFechaRegistroBetween(LocalDate fechaInicio, LocalDate fechaFin);
     
     List<DoResolucion> findByReFechaResolucionBetween(LocalDate fechaInicio, LocalDate fechaFin);
     
     List<DoResolucion> findByReEstadoActual(String reEstadoActual);
}
