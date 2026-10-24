/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.fin.be.sigered.primary.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rmoran
 */
@Entity
@Table(name = "do_resolucion_documento")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DoResolucionDocumento.findAll", query = "SELECT d FROM DoResolucionDocumento d")
    , @NamedQuery(name = "DoResolucionDocumento.findByRdId", query = "SELECT d FROM DoResolucionDocumento d WHERE d.rdId = :rdId")
    , @NamedQuery(name = "DoResolucionDocumento.findByRdNombreDocumento", query = "SELECT d FROM DoResolucionDocumento d WHERE d.rdNombreDocumento = :rdNombreDocumento")
    , @NamedQuery(name = "DoResolucionDocumento.findByRdRuta", query = "SELECT d FROM DoResolucionDocumento d WHERE d.rdRuta = :rdRuta")
    , @NamedQuery(name = "DoResolucionDocumento.findByRdTamanoBytes", query = "SELECT d FROM DoResolucionDocumento d WHERE d.rdTamanoBytes = :rdTamanoBytes")
    , @NamedQuery(name = "DoResolucionDocumento.findByRdExtension", query = "SELECT d FROM DoResolucionDocumento d WHERE d.rdExtension = :rdExtension")
    , @NamedQuery(name = "DoResolucionDocumento.findByRdEstado", query = "SELECT d FROM DoResolucionDocumento d WHERE d.rdEstado = :rdEstado")
    , @NamedQuery(name = "DoResolucionDocumento.findByRdFechaRegistro", query = "SELECT d FROM DoResolucionDocumento d WHERE d.rdFechaRegistro = :rdFechaRegistro")
    , @NamedQuery(name = "DoResolucionDocumento.findByRdUsuarioRegistro", query = "SELECT d FROM DoResolucionDocumento d WHERE d.rdUsuarioRegistro = :rdUsuarioRegistro")
    , @NamedQuery(name = "DoResolucionDocumento.findByRdFechaModifica", query = "SELECT d FROM DoResolucionDocumento d WHERE d.rdFechaModifica = :rdFechaModifica")
    , @NamedQuery(name = "DoResolucionDocumento.findByRdUsuarioModifica", query = "SELECT d FROM DoResolucionDocumento d WHERE d.rdUsuarioModifica = :rdUsuarioModifica")})
public class DoResolucionDocumento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "rd_id")
    private Integer rdId;
    @Size(max = 200)
    @Column(name = "rd_nombre_documento")
    private String rdNombreDocumento;
    @Size(max = 200)
    @Column(name = "rd_ruta")
    private String rdRuta;
    @Column(name = "rd_tamano_bytes")
    private Integer rdTamanoBytes;
    @Size(max = 10)
    @Column(name = "rd_extension")
    private String rdExtension;
    @Size(max = 1)
    @Column(name = "rd_estado")
    private String rdEstado;
    @Column(name = "rd_fecha_registro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date rdFechaRegistro;
    @Size(max = 25)
    @Column(name = "rd_usuario_registro")
    private String rdUsuarioRegistro;
    @Column(name = "rd_fecha_modifica")
    @Temporal(TemporalType.TIMESTAMP)
    private Date rdFechaModifica;
    @Size(max = 25)
    @Column(name = "rd_usuario_modifica")
    private String rdUsuarioModifica;
    @JoinColumn(name = "rd_resolucion", referencedColumnName = "re_id")
    @ManyToOne(optional = false)
    private DoResolucion rdResolucion;

    public DoResolucionDocumento() {
    }

    public DoResolucionDocumento(Integer rdId) {
        this.rdId = rdId;
    }

    public Integer getRdId() {
        return rdId;
    }

    public void setRdId(Integer rdId) {
        this.rdId = rdId;
    }

    public String getRdNombreDocumento() {
        return rdNombreDocumento;
    }

    public void setRdNombreDocumento(String rdNombreDocumento) {
        this.rdNombreDocumento = rdNombreDocumento;
    }

    public String getRdRuta() {
        return rdRuta;
    }

    public void setRdRuta(String rdRuta) {
        this.rdRuta = rdRuta;
    }

    public Integer getRdTamanoBytes() {
        return rdTamanoBytes;
    }

    public void setRdTamanoBytes(Integer rdTamanoBytes) {
        this.rdTamanoBytes = rdTamanoBytes;
    }

    public String getRdExtension() {
        return rdExtension;
    }

    public void setRdExtension(String rdExtension) {
        this.rdExtension = rdExtension;
    }

    public String getRdEstado() {
        return rdEstado;
    }

    public void setRdEstado(String rdEstado) {
        this.rdEstado = rdEstado;
    }

    public Date getRdFechaRegistro() {
        return rdFechaRegistro;
    }

    public void setRdFechaRegistro(Date rdFechaRegistro) {
        this.rdFechaRegistro = rdFechaRegistro;
    }

    public String getRdUsuarioRegistro() {
        return rdUsuarioRegistro;
    }

    public void setRdUsuarioRegistro(String rdUsuarioRegistro) {
        this.rdUsuarioRegistro = rdUsuarioRegistro;
    }

    public Date getRdFechaModifica() {
        return rdFechaModifica;
    }

    public void setRdFechaModifica(Date rdFechaModifica) {
        this.rdFechaModifica = rdFechaModifica;
    }

    public String getRdUsuarioModifica() {
        return rdUsuarioModifica;
    }

    public void setRdUsuarioModifica(String rdUsuarioModifica) {
        this.rdUsuarioModifica = rdUsuarioModifica;
    }

    public DoResolucion getRdResolucion() {
        return rdResolucion;
    }

    public void setRdResolucion(DoResolucion rdResolucion) {
        this.rdResolucion = rdResolucion;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (rdId != null ? rdId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DoResolucionDocumento)) {
            return false;
        }
        DoResolucionDocumento other = (DoResolucionDocumento) object;
        if ((this.rdId == null && other.rdId != null) || (this.rdId != null && !this.rdId.equals(other.rdId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.fin.be.sigered.entity.DoResolucionDocumento[ rdId=" + rdId + " ]";
    }
    
}
