/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.fin.be.sigered.primary.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ec.fin.be.sigered.primary.entity.DoCatalogo;
import ec.fin.be.sigered.primary.entity.DoCatalogoId;
import ec.fin.be.sigered.primary.entity.DoTabla;
import java.util.List;
/**
 *
 * @author rmoran
 */
public interface DoCatalogoRepositorio extends JpaRepository<DoCatalogo,Integer>{
    
    @Override
    DoCatalogo save(DoCatalogo doCatalogo);
    DoCatalogo findById(DoCatalogoId Id);
    
    List<DoCatalogo> findByTablaAndEstado(DoTabla tabla, String estado);
    List<DoCatalogo> findByTabla(DoTabla tabla);
    
    
    
}
