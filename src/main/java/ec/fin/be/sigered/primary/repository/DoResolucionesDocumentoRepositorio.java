/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.fin.be.sigered.primary.repository;

import ec.fin.be.sigered.primary.entity.DoResolucion;
import ec.fin.be.sigered.primary.entity.DoResolucionDocumento;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author rmoran
 */

public interface DoResolucionesDocumentoRepositorio extends JpaRepository<DoResolucionDocumento, Integer> { 

    @Override
    DoResolucionDocumento save(DoResolucionDocumento roResolucionDocumento);
    
    DoResolucionDocumento findByRdId(int rdId);
    
    List<DoResolucionDocumento> findByRdResolucion(DoResolucion rdResolucion);
    
    void deleteByRdId(int rdId);
    
    boolean existsByRdId(int rdId);
    
    boolean existsByRdResolucionAndRdNombreDocumento(DoResolucion rdResolucion,String nombreDocumento);
}
