package ec.fin.be.sigered.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("squid:S1068")
public class ResponseDto {
    private boolean exito;
    private String codigoError;
    private String mensajeError;
    private Object data;
     
}
