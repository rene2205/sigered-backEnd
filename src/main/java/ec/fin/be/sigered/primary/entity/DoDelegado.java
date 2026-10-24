package ec.fin.be.sigered.primary.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author wochoa
 */
@Entity
@Table(name = "do_delegado")
@Data
@AttributeOverride(name = "estado", column = @Column(name = "de_estado"))
@AttributeOverride(name = "fechaCreacion", column = @Column(name = "de_fecha_creacion"))
@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "de_usuario_creacion"))
@AttributeOverride(name = "fechaModificacion", column = @Column(name = "de_fecha_modificacion"))
@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "de_usuario_modificacion"))
@AttributeOverride(name = "fechaEliminacion", column = @Column(name = "de_fecha_eliminacion"))
@AttributeOverride(name = "usuarioEliminacion", column = @Column(name = "de_usuario_eliminacion"))
public class DoDelegado extends AuditoriaEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "de_id")
    private Integer id;

    @Column(name = "de_codigo")
    private String codigo;

    @Column(name = "de_cedula")
    private String cedula;

    @Column(name = "de_nombre_funcionario")
    private String nombreFuncionario;

    @Column(name = "de_correo")
    private String correo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "de_gerencia")
    @JsonBackReference
    private DoGerencia gerencia;

}