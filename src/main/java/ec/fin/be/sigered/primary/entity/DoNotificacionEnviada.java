package ec.fin.be.sigered.primary.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Registro de notificaciones enviadas.
 *
 * @author wochoa
 */
@Entity
@Table(name = "do_notificacion_enviada")
@Data
public class DoNotificacionEnviada implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ne_id")
    private Integer id;

    @Column(name = "ne_tipo", nullable = false, length = 50)
    private String tipo;

    @Column(name = "ne_destinatario", nullable = false, length = 200)
    private String destinatario;

    @Column(name = "ne_fecha_notificacion", nullable = false)
    private LocalDate fechaNotificacion;

    @Column(name = "ne_fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "ne_notas", length = 300)
    private String notas;
}