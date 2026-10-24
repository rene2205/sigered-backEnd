package ec.fin.be.sigered.primary.entity;

import lombok.Data;

import javax.persistence.*;

import java.io.Serializable;

/**
 * @author wochoa
 */
@Entity
@Table(name = "do_institucion")
@Data
@AttributeOverride(name = "id", column = @Column(name = "in_id"))
@AttributeOverride(name = "codigo", column = @Column(name = "in_codigo"))
@AttributeOverride(name = "nombre", column = @Column(name = "in_nombre"))
@AttributeOverride(name = "estado", column = @Column(name = "in_estado"))
@AttributeOverride(name = "fechaCreacion", column = @Column(name = "in_fecha_creacion"))
@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "in_usuario_creacion"))
@AttributeOverride(name = "fechaModificacion", column = @Column(name = "in_fecha_modificacion"))
@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "in_usuario_modificacion"))
@AttributeOverride(name = "fechaEliminacion", column = @Column(name = "in_fecha_eliminacion"))
@AttributeOverride(name = "usuarioEliminacion", column = @Column(name = "in_usuario_eliminacion"))
public class DoInstitucion extends BaseEntity implements Serializable {

}