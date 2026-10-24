package ec.fin.be.sigered.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("squid:S1068")
public class AvanceDto {
    private Integer id;
    private Integer idSolicitud;
    private String tipo;
    private Integer diasProrroga;
    private Integer idDelegado;
    private String nombreDelegado;
    private String comentario;
    private LocalDateTime fechaCreacion;
    private String estado;
    private DocumentoDto documento;
}
