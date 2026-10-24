package ec.fin.be.sigered.primary.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author wochoa
 */
@Entity
@Table(name = "do_documento")
@Data
@AttributeOverride(name = "estado", column = @Column(name = "do_estado"))
@AttributeOverride(name = "fechaCreacion", column = @Column(name = "do_fecha_creacion"))
@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "do_usuario_creacion"))
@AttributeOverride(name = "fechaModificacion", column = @Column(name = "do_fecha_modificacion"))
@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "do_usuario_modificacion"))
@AttributeOverride(name = "fechaEliminacion", column = @Column(name = "do_fecha_eliminacion"))
@AttributeOverride(name = "usuarioEliminacion", column = @Column(name = "do_usuario_eliminacion"))
public class DoDocumento extends AuditoriaEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "do_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "do_solicitud")
    @JsonBackReference
    private DoSolicitud solicitud;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "do_avance")
    @JsonBackReference
    private DoAvance avance;

    @Column(name = "do_nombre_original")
    private String nombreOriginal;

    @Column(name = "do_nombre_fisico")
    private String nombreFisico;

    @Column(name = "do_ruta")
    private String ruta;

    @Column(name = "do_tamano_bytes")
    private Integer tamanoBytes;

    @Column(name = "do_extension")
    private String extension;

    @Column(name = "do_tipo")
    private String tipo;
    
}