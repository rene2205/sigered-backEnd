package ec.fin.be.sigered.dto;

import lombok.Data;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("squid:S1068")
public class SolicitudFiltroDto {

    private Integer idInstitucion;
    private Integer idGerenciaResponsable;
    private String documentoInstitucion;
    private String documentoInterno;
    private String tituloDocumento;
    private LocalDate fechaIngresoDesde;
    private LocalDate fechaIngresoHasta;
    private Integer diasTermino;
    private String estado;
}