package ec.fin.be.sigered.primary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import ec.fin.be.sigered.primary.entity.DoGerencia;

public interface DoGerenciaRepositorio extends JpaRepository<DoGerencia, Integer>,
    JpaSpecificationExecutor<DoGerencia> {

    @Override
    DoGerencia save(DoGerencia gerencia);

    DoGerencia findById(int id);

    DoGerencia findByCodigo(String codigo);

    List<DoGerencia> findByEstado(String estado);

    List<DoGerencia> findAll();

    List<DoGerencia> findByPadreAndEstado(DoGerencia padre, String estado);

}
