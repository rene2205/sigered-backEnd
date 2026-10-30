/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.fin.be.sigered.secondary.repository;

import ec.fin.be.sigered.secondary.entity.VisGthEmpleadosActivos;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author rmoran
 */
public interface VisGthEmpleadosActivosRepository extends JpaRepository<VisGthEmpleadosActivos,Integer>{
    
    
    List <VisGthEmpleadosActivos> findAllByOrderByNomEmpleadoEmpAsc();
    
    List <VisGthEmpleadosActivos> findByNomEmpleadoEmpContainingOrderByNomEmpleadoEmpAsc(String nomEmpleadoEmp);
    
    VisGthEmpleadosActivos findByNumCedulaEmp(String numCedulaEmp);
    
}
