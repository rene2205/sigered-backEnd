package ec.fin.be.sigered.primary.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.io.Serializable;
import java.util.List;

/**
 * @author wochoa
 */
@Entity
@Table(name = "do_gerencia")
@Data
@AttributeOverride(name = "id", column = @Column(name = "ge_id"))
@AttributeOverride(name = "codigo", column = @Column(name = "ge_codigo"))
@AttributeOverride(name = "nombre", column = @Column(name = "ge_nombre"))
@AttributeOverride(name = "estado", column = @Column(name = "ge_estado"))
@AttributeOverride(name = "fechaCreacion", column = @Column(name = "ge_fecha_creacion"))
@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "ge_usuario_creacion"))
@AttributeOverride(name = "fechaModificacion", column = @Column(name = "ge_fecha_modificacion"))
@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "ge_usuario_modificacion"))
@AttributeOverride(name = "fechaEliminacion", column = @Column(name = "ge_fecha_eliminacion"))
@AttributeOverride(name = "usuarioEliminacion", column = @Column(name = "ge_usuario_eliminacion"))
public class DoGerencia extends BaseEntity implements Serializable {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ge_padre_id")
    @JsonBackReference
    @EqualsAndHashCode.Exclude
    private DoGerencia padre;

    @OneToMany(mappedBy = "padre", fetch = FetchType.LAZY)
    @JsonManagedReference
    @EqualsAndHashCode.Exclude
    private List<DoGerencia> subgerencias;

}