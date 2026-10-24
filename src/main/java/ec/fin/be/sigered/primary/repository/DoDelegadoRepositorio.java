package ec.fin.be.sigered.primary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import ec.fin.be.sigered.primary.entity.DoDelegado;

public interface DoDelegadoRepositorio extends JpaRepository<DoDelegado, Integer>,
    JpaSpecificationExecutor<DoDelegado> {

    DoDelegado save(DoDelegado delegado);

    DoDelegado findById(int id);

    DoDelegado findByCedula(String cedula);

    List<DoDelegado> findByEstado(String estado);

    List<DoDelegado> findAll();

    DoDelegado findByGerenciaId(int gerenciaId);

}
