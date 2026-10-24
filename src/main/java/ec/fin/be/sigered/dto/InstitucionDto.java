package ec.fin.be.sigered.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("squid:S1068")
public class InstitucionDto {
    private Integer id;
    private String codigo;
    private String nombre;
    private String estado;
}
