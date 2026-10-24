package ec.fin.be.sigered.dto;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("squid:S1068")
public class GerenciaFiltroDto {
    private String codigo;
    private String nombre;
    private Integer idPadre;
}