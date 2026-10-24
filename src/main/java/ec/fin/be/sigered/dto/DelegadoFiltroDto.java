package ec.fin.be.sigered.dto;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("squid:S1068")
public class DelegadoFiltroDto {
    private Integer idGerencia;
    private String nombreFuncionario;
    private String identificacionFuncionario;
}