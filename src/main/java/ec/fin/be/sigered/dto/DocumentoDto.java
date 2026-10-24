package ec.fin.be.sigered.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("squid:S1068")
public class DocumentoDto {
    private Integer id;
    private Integer idSolicitud;
    private Integer idAvance;
    private String nombreOriginal;
    private String nombreFisico;
    private String ruta;
    private Integer tamanoBytes;
    private String extension;
    private String tipo;
    private String estado;
}
