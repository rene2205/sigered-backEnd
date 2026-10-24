package ec.fin.be.sigered.primary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ec.fin.be.sigered.primary.entity.DoDocumento;

public interface DoDocumentoRepositorio extends JpaRepository<DoDocumento, Integer> {
    @Override
    DoDocumento save(DoDocumento avance);

    DoDocumento findById(int id);

    // obtener documentos de una solicitud
    List<DoDocumento> findBySolicitudIdAndEstado(int idSolicitud, String estado);

    // obtener documentos de un avance
    List<DoDocumento> findByAvanceIdAndEstado(int idAvance, String estado);

    List<DoDocumento> findAll();

    boolean existsBySolicitudIdAndTipo(int idSolicitud, String tipo);

    List<DoDocumento> findBySolicitudIdAndTipo(int idSolicitud, String tipo);

    List<DoDocumento> findBySolicitudIdAndTipoAndEstado(int idSolicitud, String tipo, String estado);

    boolean existsBySolicitudIdAndTipoAndEstado(int idSolicitud, String tipo, String estado);

    // obtener documentos por tipo
    List<DoDocumento> findByTipoAndEstado(String tipo, String estado);
}
