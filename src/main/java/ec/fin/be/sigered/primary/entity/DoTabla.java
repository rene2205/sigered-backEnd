package ec.fin.be.sigered.primary.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author wochoa
 */
@Entity
@Table(name = "do_tabla")
@Data
public class DoTabla implements Serializable {

    @Id
    @Column(name = "ta_codigo")
    private String codigo;

    @Column(name = "ta_descripcion")
    private String descripcion;
}