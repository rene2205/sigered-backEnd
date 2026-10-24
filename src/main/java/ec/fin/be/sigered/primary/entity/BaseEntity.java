package ec.fin.be.sigered.primary.entity;

import javax.persistence.*;

import lombok.Data;

@MappedSuperclass
@Data
public abstract class BaseEntity extends AuditoriaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    protected String codigo;
    protected String nombre;
}
