package ec.fin.be.sigered.primary.entity;

import java.time.LocalDateTime;

import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class AuditoriaEntity {

    protected String estado;
    protected LocalDateTime fechaCreacion;
    protected String usuarioCreacion;
    protected LocalDateTime fechaModificacion;
    protected String usuarioModificacion;
    protected LocalDateTime fechaEliminacion;
    protected String usuarioEliminacion;
}
