package ec.fin.be.sigered.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("squid:S1068")
public class SolicitudDto {
    private Integer id;
    private Integer idInstitucion;
    private String nombreInstitucion;
    private Integer idGerenciaResponsable;
    private String nombreGerenciaResponsable;
    private Integer idDelegadoResponsable;
    private String nombreDelegadoResponsable;
    private String documentoInstitucion;
    private String documentoInterno;
    private LocalDate fechaIngreso;
    private String tipoPlazo;
    private Integer diasPlazo;
    private LocalDate fechaMaximaAtencion;
    private String tituloDocumento;
    private String estado;    
    private String destinoFinal;
    private String usuarioResolucion;
    private String usuarioCreacion;
    private String funcionarioReceptor;
    private String razonAnulacion;
    private List<DocumentoDto> documentos;
}
