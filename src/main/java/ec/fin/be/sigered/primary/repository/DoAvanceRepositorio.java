package ec.fin.be.sigered.primary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ec.fin.be.sigered.primary.entity.DoAvance;

public interface DoAvanceRepositorio extends JpaRepository<DoAvance, Integer> {
    @Override
    DoAvance save(DoAvance avance);

    DoAvance findById(int id);

    // obtener avances de una solicitud
    List<DoAvance> findBySolicitudId(int idSolicitud);

    List<DoAvance> findAll();
}
