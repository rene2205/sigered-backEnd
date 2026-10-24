package ec.fin.be.sigered.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("squid:S1068")
public class NotificacionEnviadaDto {
    private Integer id;
    private String tipo;
    private String destinatario;
    private LocalDate fechaNotificacion;
    private LocalDateTime fechaRegistro;
    private String notas;
}
