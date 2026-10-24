package ec.fin.be.sigered.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("squid:S1068")
public class DelegadoDto {
    private Integer id;
    private String codigo;
    private String cedula;
    private String nombreFuncionario;
    private String correo;
    private Integer gerenciaId;
    private String nombreGerencia;
    private String estado;
}
