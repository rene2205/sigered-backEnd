/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.fin.be.sigered.primary.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author rmoran
 */
@Entity
@Table(name = "do_resolucion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DoResolucion.findAll", query = "SELECT d FROM DoResolucion d")
    , @NamedQuery(name = "DoResolucion.findByReId", query = "SELECT d FROM DoResolucion d WHERE d.reId = :reId")
    , @NamedQuery(name = "DoResolucion.findByReResolucion", query = "SELECT d FROM DoResolucion d WHERE d.reResolucion = :reResolucion")
    , @NamedQuery(name = "DoResolucion.findByReFechaResolucion", query = "SELECT d FROM DoResolucion d WHERE d.reFechaResolucion = :reFechaResolucion")
    , @NamedQuery(name = "DoResolucion.findByReEstadoActual", query = "SELECT d FROM DoResolucion d WHERE d.reEstadoActual = :reEstadoActual")
    , @NamedQuery(name = "DoResolucion.findByReFechaFinVigencia", query = "SELECT d FROM DoResolucion d WHERE d.reFechaFinVigencia = :reFechaFinVigencia")
    , @NamedQuery(name = "DoResolucion.findByReTipoVigencia", query = "SELECT d FROM DoResolucion d WHERE d.reTipoVigencia = :reTipoVigencia")
    , @NamedQuery(name = "DoResolucion.findByReResolucionInsubsistente", query = "SELECT d FROM DoResolucion d WHERE d.reResolucionInsubsistente = :reResolucionInsubsistente")
    , @NamedQuery(name = "DoResolucion.findByReFechaRegistro", query = "SELECT d FROM DoResolucion d WHERE d.reFechaRegistro = :reFechaRegistro")
    , @NamedQuery(name = "DoResolucion.findByReUsuarioRegistro", query = "SELECT d FROM DoResolucion d WHERE d.reUsuarioRegistro = :reUsuarioRegistro")
    , @NamedQuery(name = "DoResolucion.findByReFechaActualiza", query = "SELECT d FROM DoResolucion d WHERE d.reFechaActualiza = :reFechaActualiza")
    , @NamedQuery(name = "DoResolucion.findByReUsuarioActualiza", query = "SELECT d FROM DoResolucion d WHERE d.reUsuarioActualiza = :reUsuarioActualiza")})
public class DoResolucion implements Serializable {

    @Column(name = "re_fecha_resolucion")
    private LocalDate reFechaResolucion;
    @Size(max = 25)
    @Column(name = "re_suscrito")
    private String reSuscrito;
    @Size(max = 100)
    @Column(name = "re_nombre_suscrito")
    private String reNombreSuscrito;
    @Size(max = 100)
    @Column(name = "re_cargo_suscrito")
    private String reCargoSuscrito;
    @Lob
    @Size(max = 65535)
    @Column(name = "re_descripcion")
    private String reDescripcion;
    @Column(name = "re_fecha_fin_vigencia")
    private LocalDate reFechaFinVigencia;
    @Size(max = 25)
    @Column(name = "re_insubsistente")
    private String reInsubsistente;
    @Column(name = "re_fecha_registro")
    private LocalDateTime reFechaRegistro;
    @Column(name = "re_fecha_actualiza")
    private LocalDateTime reFechaActualiza;

    @Size(max = 25)
    @Column(name = "re_area")
    private String reArea;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "rdResolucion")
    private List<DoResolucionDocumento> doResolucionDocumentoList;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "re_id")
    private Integer reId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "re_resolucion")
    private String reResolucion;
    @Basic(optional = false)
    @NotNull
    @Column(name = "re_estado_actual")
    private String reEstadoActual;
    @Size(max = 1)
    @Column(name = "re_tipo_vigencia")
    private String reTipoVigencia;
    @Column(name = "re_resolucion_insubsistente")
    private Integer reResolucionInsubsistente;
    @Lob
    @Size(max = 65535)
    @Column(name = "re_observacion")
    private String reObservacion;
    
    
    @Size(max = 25)
    @Column(name = "re_usuario_registro", updatable = false)
    private String reUsuarioRegistro;
    @Size(max = 25)
    @Column(name = "re_usuario_actualiza")
    private String reUsuarioActualiza;

    public DoResolucion() {
    }

    public DoResolucion(Integer reId) {
        this.reId = reId;
    }

    public DoResolucion(Integer reId, String reResolucion, String reEstadoActual) {
        this.reId = reId;
        this.reResolucion = reResolucion;
        this.reEstadoActual = reEstadoActual;
    }

    public Integer getReId() {
        return reId;
    }

    public void setReId(Integer reId) {
        this.reId = reId;
    }

    public String getReResolucion() {
        return reResolucion;
    }

    public void setReResolucion(String reResolucion) {
        this.reResolucion = reResolucion;
    }

    public LocalDate getReFechaResolucion() {
        return reFechaResolucion;
    }

    public void setReFechaResolucion(LocalDate reFechaResolucion) {
        this.reFechaResolucion = reFechaResolucion;
    }

    public String getReEstadoActual() {
        return reEstadoActual;
    }

    public void setReEstadoActual(String reEstadoActual) {
        this.reEstadoActual = reEstadoActual;
    }

    public LocalDate getReFechaFinVigencia() {
        return reFechaFinVigencia;
    }

    public void setReFechaFinVigencia(LocalDate reFechaFinVigencia) {
        this.reFechaFinVigencia = reFechaFinVigencia;
    }

    public String getReTipoVigencia() {
        return reTipoVigencia;
    }

    public void setReTipoVigencia(String reTipoVigencia) {
        this.reTipoVigencia = reTipoVigencia;
    }

    public Integer getReResolucionInsubsistente() {
        return reResolucionInsubsistente;
    }

    public void setReResolucionInsubsistente(Integer reResolucionInsubsistente) {
        this.reResolucionInsubsistente = reResolucionInsubsistente;
    }

    public String getReObservacion() {
        return reObservacion;
    }

    public void setReObservacion(String reObservacion) {
        this.reObservacion = reObservacion;
    }

    public LocalDateTime getReFechaRegistro() {
        return reFechaRegistro;
    }

    public void setReFechaRegistro(LocalDateTime reFechaRegistro) {
        this.reFechaRegistro = reFechaRegistro;
    }

    public String getReUsuarioRegistro() {
        return reUsuarioRegistro;
    }

    public void setReUsuarioRegistro(String reUsuarioRegistro) {
        this.reUsuarioRegistro = reUsuarioRegistro;
    }

    public LocalDateTime getReFechaActualiza() {
        return reFechaActualiza;
    }

    public void setReFechaActualiza(LocalDateTime reFechaActualiza) {
        this.reFechaActualiza = reFechaActualiza;
    }

    public String getReUsuarioActualiza() {
        return reUsuarioActualiza;
    }

    public void setReUsuarioActualiza(String reUsuarioActualiza) {
        this.reUsuarioActualiza = reUsuarioActualiza;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reId != null ? reId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DoResolucion)) {
            return false;
        }
        DoResolucion other = (DoResolucion) object;
        if ((this.reId == null && other.reId != null) || (this.reId != null && !this.reId.equals(other.reId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.fin.be.sigered.entity.DoResolucion[ reId=" + reId + " ]";
    }

    @XmlTransient
    public List<DoResolucionDocumento> getDoResolucionDocumentoList() {
        return doResolucionDocumentoList;
    }

    public void setDoResolucionDocumentoList(List<DoResolucionDocumento> doResolucionDocumentoList) {
        this.doResolucionDocumentoList = doResolucionDocumentoList;
    }

    public String getReArea() {
        return reArea;
    }

    public void setReArea(String reArea) {
        this.reArea = reArea;
    }

   

    public String getReSuscrito() {
        return reSuscrito;
    }

    public void setReSuscrito(String reSuscrito) {
        this.reSuscrito = reSuscrito;
    }

    public String getReNombreSuscrito() {
        return reNombreSuscrito;
    }

    public void setReNombreSuscrito(String reNombreSuscrito) {
        this.reNombreSuscrito = reNombreSuscrito;
    }

    public String getReCargoSuscrito() {
        return reCargoSuscrito;
    }

    public void setReCargoSuscrito(String reCargoSuscrito) {
        this.reCargoSuscrito = reCargoSuscrito;
    }

    public String getReDescripcion() {
        return reDescripcion;
    }

    public void setReDescripcion(String reDescripcion) {
        this.reDescripcion = reDescripcion;
    }

   
    public String getReInsubsistente() {
        return reInsubsistente;
    }

    public void setReInsubsistente(String reInsubsistente) {
        this.reInsubsistente = reInsubsistente;
    }

    

    

  
    
}
