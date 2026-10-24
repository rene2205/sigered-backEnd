package ec.fin.be.sigered.primary.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wochoa
 */
@Entity
@Table(name = "do_solicitud")
@Data
@AttributeOverride(name = "estado", column = @Column(name = "so_estado"))
@AttributeOverride(name = "fechaCreacion", column = @Column(name = "so_fecha_creacion"))
@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "so_usuario_creacion"))
@AttributeOverride(name = "fechaModificacion", column = @Column(name = "so_fecha_modificacion"))
@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "so_usuario_modificacion"))
@AttributeOverride(name = "fechaEliminacion", column = @Column(name = "so_fecha_eliminacion"))
@AttributeOverride(name = "usuarioEliminacion", column = @Column(name = "so_usuario_eliminacion"))
public class DoSolicitud extends AuditoriaEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "so_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "so_institucion")
    @JsonBackReference
    private DoInstitucion institucion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "so_gerencia_responsable")
    @JsonBackReference
    private DoGerencia gerenciaResponsable;

    @Column(name = "so_documento_institucion")
    private String documentoInstitucion;

    @Column(name = "so_documento_interno")
    private String documentoInterno;

    @Column(name = "so_fecha_ingreso")
    private LocalDate fechaIngreso;

    @Column(name = "so_tipo_plazo")
    private String tipoPlazo;

    @Column(name = "so_dias_plazo")
    private Integer diasPlazo;

    @Column(name = "so_fecha_maxima_atencion")
    private LocalDate fechaMaximaAtencion;

    @Column(name = "so_titulo_documento")
    private String tituloDocumento;

    @Column(name = "so_fecha_resolucion")
    private LocalDateTime fechaResolucion;

    @Column(name = "so_usuario_resolucion")
    private String usuarioResolucion;

    @Column(name = "so_funcionario_receptor")
    private String funcionarioReceptor;

    @Column(name = "so_razon_anulacion")
    private String razonAnulacion;

    @OneToMany(mappedBy = "solicitud", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<DoAvance> avances;

    @OneToMany(mappedBy = "solicitud", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<DoDocumento> documentos;
}