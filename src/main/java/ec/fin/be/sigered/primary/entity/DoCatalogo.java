package ec.fin.be.sigered.primary.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author wochoa
 */
@Entity
@Table(name = "do_catalogo")
@Data
public class DoCatalogo implements Serializable {

    @EmbeddedId
    private DoCatalogoId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ca_tabla", insertable = false, updatable = false)
    @JsonBackReference
    private DoTabla tabla;

    @Column(name = "ca_valor")
    private String valor;

    @Column(name = "ca_estado")
    private String estado;

    @Column(name = "ca_codigo_padre")
    private String codigoPadre;
}
