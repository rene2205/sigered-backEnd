package ec.fin.be.sigered.primary.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author wochoa
 */
@Entity
@Table(name = "do_avance")
@Data
@AttributeOverride(name = "estado", column = @Column(name = "av_estado"))
@AttributeOverride(name = "fechaCreacion", column = @Column(name = "av_fecha_creacion"))
@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "av_usuario_creacion"))
@AttributeOverride(name = "fechaModificacion", column = @Column(name = "av_fecha_modificacion"))
@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "av_usuario_modificacion"))
@AttributeOverride(name = "fechaEliminacion", column = @Column(name = "av_fecha_eliminacion"))
@AttributeOverride(name = "usuarioEliminacion", column = @Column(name = "av_usuario_eliminacion"))
public class DoAvance extends AuditoriaEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "av_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "av_solicitud")
    @JsonBackReference
    private DoSolicitud solicitud;

    @Column(name = "av_tipo")
    private String tipo;

    @Column(name = "av_dias_prorroga")
    private Integer diasProrroga;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "av_delegado")
    @JsonBackReference
    private DoDelegado delegado;

    @Column(name = "av_comentario")
    private String comentario;

    @OneToMany(mappedBy = "avance", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<DoDocumento> documentos;
}