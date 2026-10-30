/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.fin.be.sigered.primary.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rmoran
 */
@Entity
@Table(name = "do_solicitud_cambio_resolucion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DoSolicitudCambioResolucion.findAll", query = "SELECT d FROM DoSolicitudCambioResolucion d")
    , @NamedQuery(name = "DoSolicitudCambioResolucion.findByScId", query = "SELECT d FROM DoSolicitudCambioResolucion d WHERE d.scId = :scId")
    , @NamedQuery(name = "DoSolicitudCambioResolucion.findByScUsuarioSolicitante", query = "SELECT d FROM DoSolicitudCambioResolucion d WHERE d.scUsuarioSolicitante = :scUsuarioSolicitante")
    , @NamedQuery(name = "DoSolicitudCambioResolucion.findByScIdentificacionSolicitante", query = "SELECT d FROM DoSolicitudCambioResolucion d WHERE d.scIdentificacionSolicitante = :scIdentificacionSolicitante")
    , @NamedQuery(name = "DoSolicitudCambioResolucion.findByScNombreSolicitante", query = "SELECT d FROM DoSolicitudCambioResolucion d WHERE d.scNombreSolicitante = :scNombreSolicitante")
    , @NamedQuery(name = "DoSolicitudCambioResolucion.findByScCorreoSolicitante", query = "SELECT d FROM DoSolicitudCambioResolucion d WHERE d.scCorreoSolicitante = :scCorreoSolicitante")
    , @NamedQuery(name = "DoSolicitudCambioResolucion.findByScUsuarioAdministrador", query = "SELECT d FROM DoSolicitudCambioResolucion d WHERE d.scUsuarioAdministrador = :scUsuarioAdministrador")
    , @NamedQuery(name = "DoSolicitudCambioResolucion.findByScIdentificacionAdministrador", query = "SELECT d FROM DoSolicitudCambioResolucion d WHERE d.scIdentificacionAdministrador = :scIdentificacionAdministrador")
    , @NamedQuery(name = "DoSolicitudCambioResolucion.findByScFechaSolicitud", query = "SELECT d FROM DoSolicitudCambioResolucion d WHERE d.scFechaSolicitud = :scFechaSolicitud")
    , @NamedQuery(name = "DoSolicitudCambioResolucion.findByScFechaRespuesta", query = "SELECT d FROM DoSolicitudCambioResolucion d WHERE d.scFechaRespuesta = :scFechaRespuesta")
    , @NamedQuery(name = "DoSolicitudCambioResolucion.findByScEstadoSolicitud", query = "SELECT d FROM DoSolicitudCambioResolucion d WHERE d.scEstadoSolicitud = :scEstadoSolicitud")})
public class DoSolicitudCambioResolucion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "sc_id")
    private Integer scId;
    @Size(max = 25)
    @Column(name = "sc_usuario_solicitante")
    private String scUsuarioSolicitante;
   
    @Size(max = 25)
    @Column(name = "sc_num_resolucion")
    private String scNumResolucion;
      
    @Size(max = 25)
    @Column(name = "sc_identificacion_solicitante")
    private String scIdentificacionSolicitante;
    @Size(max = 100)
    @Column(name = "sc_nombre_solicitante")
    private String scNombreSolicitante;
    @Size(max = 100)
    @Column(name = "sc_correo_solicitante")
    private String scCorreoSolicitante;
    @Size(max = 25)
    @Column(name = "sc_usuario_administrador")
    private String scUsuarioAdministrador;
    @Size(max = 25)
    @Column(name = "sc_identificacion_administrador")
    private String scIdentificacionAdministrador;
    @Lob
    @Size(max = 65535)
    @Column(name = "sc_observacion")
    private String scObservacion;
    @Column(name = "sc_fecha_solicitud")
    
    private LocalDateTime scFechaSolicitud;
    @Column(name = "sc_fecha_respuesta")
    
    private LocalDateTime scFechaRespuesta;
    @Column(name = "sc_estado_solicitud")
    private Character scEstadoSolicitud;
    @JoinColumn(name = "sc_resolucion", referencedColumnName = "re_id")
    @ManyToOne
    private DoResolucion scResolucion;

    public DoSolicitudCambioResolucion() {
    }

    public DoSolicitudCambioResolucion(Integer scId) {
        this.scId = scId;
    }

    public Integer getScId() {
        return scId;
    }

    public void setScId(Integer scId) {
        this.scId = scId;
    }

    public String getScUsuarioSolicitante() {
        return scUsuarioSolicitante;
    }

    public void setScUsuarioSolicitante(String scUsuarioSolicitante) {
        this.scUsuarioSolicitante = scUsuarioSolicitante;
    }

    public String getScIdentificacionSolicitante() {
        return scIdentificacionSolicitante;
    }

    public void setScIdentificacionSolicitante(String scIdentificacionSolicitante) {
        this.scIdentificacionSolicitante = scIdentificacionSolicitante;
    }

      public String getScNumResolucion() {
        return scNumResolucion;
    }

    public void setScNumResolucion(String scNumResolucion) {
        this.scNumResolucion = scNumResolucion;
    }
    

    public String getScNombreSolicitante() {
        return scNombreSolicitante;
    }

    public void setScNombreSolicitante(String scNombreSolicitante) {
        this.scNombreSolicitante = scNombreSolicitante;
    }

    public String getScCorreoSolicitante() {
        return scCorreoSolicitante;
    }

    public void setScCorreoSolicitante(String scCorreoSolicitante) {
        this.scCorreoSolicitante = scCorreoSolicitante;
    }

    public String getScUsuarioAdministrador() {
        return scUsuarioAdministrador;
    }

    public void setScUsuarioAdministrador(String scUsuarioAdministrador) {
        this.scUsuarioAdministrador = scUsuarioAdministrador;
    }

    public String getScIdentificacionAdministrador() {
        return scIdentificacionAdministrador;
    }

    public void setScIdentificacionAdministrador(String scIdentificacionAdministrador) {
        this.scIdentificacionAdministrador = scIdentificacionAdministrador;
    }

    public String getScObservacion() {
        return scObservacion;
    }

    public void setScObservacion(String scObservacion) {
        this.scObservacion = scObservacion;
    }

    public LocalDateTime getScFechaSolicitud() {
        return scFechaSolicitud;
    }

    public void setScFechaSolicitud(LocalDateTime scFechaSolicitud) {
        this.scFechaSolicitud = scFechaSolicitud;
    }

    public LocalDateTime getScFechaRespuesta() {
        return scFechaRespuesta;
    }

    public void setScFechaRespuesta(LocalDateTime scFechaRespuesta) {
        this.scFechaRespuesta = scFechaRespuesta;
    }

    public Character getScEstadoSolicitud() {
        return scEstadoSolicitud;
    }

    public void setScEstadoSolicitud(Character scEstadoSolicitud) {
        this.scEstadoSolicitud = scEstadoSolicitud;
    }

    public DoResolucion getScResolucion() {
        return scResolucion;
    }

    public void setScResolucion(DoResolucion scResolucion) {
        this.scResolucion = scResolucion;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (scId != null ? scId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DoSolicitudCambioResolucion)) {
            return false;
        }
        DoSolicitudCambioResolucion other = (DoSolicitudCambioResolucion) object;
        if ((this.scId == null && other.scId != null) || (this.scId != null && !this.scId.equals(other.scId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.fin.be.sigered.primary.entity.DoSolicitudCambioResolucion[ scId=" + scId + " ]";
    }
    
}
