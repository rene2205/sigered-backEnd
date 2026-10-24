/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.fin.be.sigered.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author rmoran
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("squid:S1068")
public class ResolucionDto {
    Integer id;
    String area;
    String resolucion;        
    LocalDate   fechaResolucion;
    String suscrito;
    String nombreSuscrito;
    String cargoSuscrito;
    String descripcion;

    String estadoActual;
    LocalDate fechaFinVigencia;
    String tipoVigencia;
    Integer resolucion_insubsistente;
    String insubsistente;
    String observacion;
    LocalDateTime fechaRegistro;
    String usuarioRegistro;
    LocalDateTime fechaActualiza;
    String usuarioActualiza;
    
    List <ResolucionDocumentoDto> documentos;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getResolucion() {
        return resolucion;
    }

    public void setResolucion(String resolucion) {
        this.resolucion = resolucion;
    }

    public LocalDate getFechaResolucion() {
        return fechaResolucion;
    }

    public void setFechaResolucion(LocalDate fechaResolucion) {
        this.fechaResolucion = fechaResolucion;
    }

    public String getSuscrito() {
        return suscrito;
    }

    public void setSuscrito(String suscrito) {
        this.suscrito = suscrito;
    }

    public String getNombreSuscrito() {
        return nombreSuscrito;
    }

    public void setNombreSuscrito(String nombreSuscrito) {
        this.nombreSuscrito = nombreSuscrito;
    }

    public String getCargoSuscrito() {
        return cargoSuscrito;
    }

    public void setCargoSuscrito(String cargoSuscrito) {
        this.cargoSuscrito = cargoSuscrito;
    }
    

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }    

    public String getEstadoActual() {
        return estadoActual;
    }

    public void setEstadoActual(String estadoActual) {
        this.estadoActual = estadoActual;
    }

    public LocalDate getFechaFinVigencia() {
        return fechaFinVigencia;
    }

    public void setFechaFinVigencia(LocalDate fechaFinVigencia) {
        this.fechaFinVigencia = fechaFinVigencia;
    }

    public String getTipoVigencia() {
        return tipoVigencia;
    }

    public void setTipoVigencia(String tipoVigencia) {
        this.tipoVigencia = tipoVigencia;
    }

    public Integer getResolucion_insubsistente() {
        return resolucion_insubsistente;
    }

    public void setResolucion_insubsistente(Integer resolucion_insubsistente) {
        this.resolucion_insubsistente = resolucion_insubsistente;
    }

    public String getInsubsistente() {
        return insubsistente;
    }

    public void setInsubsistente(String insubsistente) {
        this.insubsistente = insubsistente;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(String usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }

    public LocalDateTime getFechaActualiza() {
        return fechaActualiza;
    }

    public void setFechaActualiza(LocalDateTime fechaActualiza) {
        this.fechaActualiza = fechaActualiza;
    }

    public String getUsuarioActualiza() {
        return usuarioActualiza;
    }

    public void setUsuarioActualiza(String usuarioActualiza) {
        this.usuarioActualiza = usuarioActualiza;
    }

    public List<ResolucionDocumentoDto> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<ResolucionDocumentoDto> documentos) {
        this.documentos = documentos;
    }

   
   
}
