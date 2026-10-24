package ec.fin.be.sigered.primary.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author wochoa
 */
@Embeddable
@Data
public class DoCatalogoId implements Serializable {

    @Column(name = "ca_tabla")
    private String tabla;

    @Column(name = "ca_codigo")
    private String codigo;
}