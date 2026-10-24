/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.fin.be.sigered.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;

/**
 *
 * @author rmoran
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("squid:S1068")
public class ResolucionDocumentoDto{

   Integer idDocumento;
   Integer idResolucion;
   String  nombreDocumento;
   String  rutaDocumento;
   Integer tamanoDocumento;
   String  extensionDocumento;
   String  estadoDocumento;
   Date    fechaRegistroDocumento;
   String  usuarioRegistraDocumento;
   Date    fechaModificaDocumento;
   String  usuarioModificaDocumento;

    public Integer getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(Integer idDocumento) {
        this.idDocumento = idDocumento;
    }

    public Integer getIdResolucion() {
        return idResolucion;
    }

    public void setIdResolucion(Integer idResolucion) {
        this.idResolucion = idResolucion;
    }

    public String getNombreDocumento() {
        return nombreDocumento;
    }

    public void setNombreDocumento(String nombreDocumento) {
        this.nombreDocumento = nombreDocumento;
    }

    public String getRutaDocumento() {
        return rutaDocumento;
    }

    public void setRutaDocumento(String rutaDocumento) {
        this.rutaDocumento = rutaDocumento;
    }

    public Integer getTamanoDocumento() {
        return tamanoDocumento;
    }

    public void setTamanoDocumento(Integer tamanoDocumento) {
        this.tamanoDocumento = tamanoDocumento;
    }

    public String getExtensionDocumento() {
        return extensionDocumento;
    }

    public void setExtensionDocumento(String extensionDocumento) {
        this.extensionDocumento = extensionDocumento;
    }

    public String getEstadoDocumento() {
        return estadoDocumento;
    }

    public void setEstadoDocumento(String estadoDocumento) {
        this.estadoDocumento = estadoDocumento;
    }

    public Date getFechaRegistroDocumento() {
        return fechaRegistroDocumento;
    }

    public void setFechaRegistroDocumento(Date fechaRegistroDocumento) {
        this.fechaRegistroDocumento = fechaRegistroDocumento;
    }

    public String getUsuarioRegistraDocumento() {
        return usuarioRegistraDocumento;
    }

    public void setUsuarioRegistraDocumento(String usuarioRegistraDocumento) {
        this.usuarioRegistraDocumento = usuarioRegistraDocumento;
    }

    public Date getFechaModificaDocumento() {
        return fechaModificaDocumento;
    }

    public void setFechaModificaDocumento(Date fechaModificaDocumento) {
        this.fechaModificaDocumento = fechaModificaDocumento;
    }

    public String getUsuarioModificaDocumento() {
        return usuarioModificaDocumento;
    }

    public void setUsuarioModificaDocumento(String usuarioModificaDocumento) {
        this.usuarioModificaDocumento = usuarioModificaDocumento;
    }
    
    
}
