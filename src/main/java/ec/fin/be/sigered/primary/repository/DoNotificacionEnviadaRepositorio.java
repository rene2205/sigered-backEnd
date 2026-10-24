package ec.fin.be.sigered.primary.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ec.fin.be.sigered.primary.entity.DoNotificacionEnviada;

public interface DoNotificacionEnviadaRepositorio extends JpaRepository<DoNotificacionEnviada, Integer> {

    boolean existsByTipoAndDestinatarioAndFechaNotificacion(String tipo, String destinatario, LocalDate fechaNotificacion);

    List<DoNotificacionEnviada> findByTipoAndFechaNotificacion(String tipo, LocalDate fechaNotificacion);
}