package ec.fin.be.sigered.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("squid:S1068")
public class GerenciaDto {
    private Integer id;
    private String codigo;
    private String nombre;
    private Integer padreId;
    private String padreCodigo;
    private String padreNombre;
    private String estado;
}
