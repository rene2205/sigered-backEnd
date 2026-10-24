package ec.fin.be.sigered.primary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import ec.fin.be.sigered.primary.entity.DoInstitucion;

public interface DoInstitucionRepositorio extends JpaRepository<DoInstitucion, Integer>,
    JpaSpecificationExecutor<DoInstitucion> {

    @Override
    DoInstitucion save(DoInstitucion institucion);

    DoInstitucion findById(int id);

    DoInstitucion findByCodigo(String codigo);
    
    List<DoInstitucion> findByEstado(String estado);

    List<DoInstitucion> findAll();

}