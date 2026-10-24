/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.fin.be.sigered.secondary.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rmoran
 */
@Entity
@Table(name = "vis_gth_empleados_activos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "VisGthEmpleadosActivos.findAll", query = "SELECT v FROM VisGthEmpleadosActivos v")
    , @NamedQuery(name = "VisGthEmpleadosActivos.findByCodigoSecuencial", query = "SELECT v FROM VisGthEmpleadosActivos v WHERE v.codigoSecuencial = :codigoSecuencial")
    , @NamedQuery(name = "VisGthEmpleadosActivos.findByNumCedulaEmp", query = "SELECT v FROM VisGthEmpleadosActivos v WHERE v.numCedulaEmp = :numCedulaEmp")
    , @NamedQuery(name = "VisGthEmpleadosActivos.findByNomEmpleadoEmp", query = "SELECT v FROM VisGthEmpleadosActivos v WHERE v.nomEmpleadoEmp = :nomEmpleadoEmp")
    , @NamedQuery(name = "VisGthEmpleadosActivos.findByFecNacimientoEmp", query = "SELECT v FROM VisGthEmpleadosActivos v WHERE v.fecNacimientoEmp = :fecNacimientoEmp")
    , @NamedQuery(name = "VisGthEmpleadosActivos.findByStsSexoEmp", query = "SELECT v FROM VisGthEmpleadosActivos v WHERE v.stsSexoEmp = :stsSexoEmp")
    , @NamedQuery(name = "VisGthEmpleadosActivos.findByNumValor", query = "SELECT v FROM VisGthEmpleadosActivos v WHERE v.numValor = :numValor")
    , @NamedQuery(name = "VisGthEmpleadosActivos.findByTxtDirDomicilioEmp", query = "SELECT v FROM VisGthEmpleadosActivos v WHERE v.txtDirDomicilioEmp = :txtDirDomicilioEmp")
    , @NamedQuery(name = "VisGthEmpleadosActivos.findByNomCiudad", query = "SELECT v FROM VisGthEmpleadosActivos v WHERE v.nomCiudad = :nomCiudad")
    , @NamedQuery(name = "VisGthEmpleadosActivos.findByCodOficina", query = "SELECT v FROM VisGthEmpleadosActivos v WHERE v.codOficina = :codOficina")
    , @NamedQuery(name = "VisGthEmpleadosActivos.findByNomOficina", query = "SELECT v FROM VisGthEmpleadosActivos v WHERE v.nomOficina = :nomOficina")
    , @NamedQuery(name = "VisGthEmpleadosActivos.findByCodOficinaBnf", query = "SELECT v FROM VisGthEmpleadosActivos v WHERE v.codOficinaBnf = :codOficinaBnf")
    , @NamedQuery(name = "VisGthEmpleadosActivos.findByCategorizacion", query = "SELECT v FROM VisGthEmpleadosActivos v WHERE v.categorizacion = :categorizacion")
    , @NamedQuery(name = "VisGthEmpleadosActivos.findByCondicion", query = "SELECT v FROM VisGthEmpleadosActivos v WHERE v.condicion = :condicion")
    , @NamedQuery(name = "VisGthEmpleadosActivos.findByCodDependencia", query = "SELECT v FROM VisGthEmpleadosActivos v WHERE v.codDependencia = :codDependencia")
    , @NamedQuery(name = "VisGthEmpleadosActivos.findByNomDependencia", query = "SELECT v FROM VisGthEmpleadosActivos v WHERE v.nomDependencia = :nomDependencia")
    , @NamedQuery(name = "VisGthEmpleadosActivos.findByCodCargo", query = "SELECT v FROM VisGthEmpleadosActivos v WHERE v.codCargo = :codCargo")
    , @NamedQuery(name = "VisGthEmpleadosActivos.findByDesCargo", query = "SELECT v FROM VisGthEmpleadosActivos v WHERE v.desCargo = :desCargo")})
public class VisGthEmpleadosActivos implements Serializable {
   
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "codigo_secuencial")
    private Integer codigoSecuencial;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "num_cedula_emp")
    private String numCedulaEmp;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "nom_empleado_emp")
    private String nomEmpleadoEmp;
    @Column(name = "fec_nacimiento_emp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecNacimientoEmp;
    @Column(name = "sts_sexo_emp")
    private Character stsSexoEmp;
    @Size(max = 200)
    @Column(name = "num_valor")
    private String numValor;
    @Size(max = 258)
    @Column(name = "txt_dir_domicilio_emp")
    private String txtDirDomicilioEmp;
    @Size(max = 80)
    @Column(name = "nom_ciudad")
    private String nomCiudad;
    @Column(name = "cod_oficina")
    private Short codOficina;
    @Size(max = 100)
    @Column(name = "nom_oficina")
    private String nomOficina;
    @Size(max = 7)
    @Column(name = "cod_oficina_bnf")
    private String codOficinaBnf;
    @Size(max = 50)
    @Column(name = "categorizacion")
    private String categorizacion;
    @Size(max = 30)
    @Column(name = "condicion")
    private String condicion;
    @Size(max = 9)
    @Column(name = "cod_dependencia")
    private String codDependencia;
    @Size(max = 70)
    @Column(name = "nom_dependencia")
    private String nomDependencia;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "cod_cargo")
    private String codCargo;
    @Size(max = 150)
    @Column(name = "des_cargo")
    private String desCargo;

    public VisGthEmpleadosActivos() {
    }

    public Integer getCodigoSecuencial() {
        return codigoSecuencial;
    }

    public void setCodigoSecuencial(Integer codigoSecuencial) {
        this.codigoSecuencial = codigoSecuencial;
    }

    public String getNumCedulaEmp() {
        return numCedulaEmp;
    }

    public void setNumCedulaEmp(String numCedulaEmp) {
        this.numCedulaEmp = numCedulaEmp;
    }

    public String getNomEmpleadoEmp() {
        return nomEmpleadoEmp;
    }

    public void setNomEmpleadoEmp(String nomEmpleadoEmp) {
        this.nomEmpleadoEmp = nomEmpleadoEmp;
    }

    public Date getFecNacimientoEmp() {
        return fecNacimientoEmp;
    }

    public void setFecNacimientoEmp(Date fecNacimientoEmp) {
        this.fecNacimientoEmp = fecNacimientoEmp;
    }

    public Character getStsSexoEmp() {
        return stsSexoEmp;
    }

    public void setStsSexoEmp(Character stsSexoEmp) {
        this.stsSexoEmp = stsSexoEmp;
    }

    public String getNumValor() {
        return numValor;
    }

    public void setNumValor(String numValor) {
        this.numValor = numValor;
    }

    public String getTxtDirDomicilioEmp() {
        return txtDirDomicilioEmp;
    }

    public void setTxtDirDomicilioEmp(String txtDirDomicilioEmp) {
        this.txtDirDomicilioEmp = txtDirDomicilioEmp;
    }

    public String getNomCiudad() {
        return nomCiudad;
    }

    public void setNomCiudad(String nomCiudad) {
        this.nomCiudad = nomCiudad;
    }

    public Short getCodOficina() {
        return codOficina;
    }

    public void setCodOficina(Short codOficina) {
        this.codOficina = codOficina;
    }

    public String getNomOficina() {
        return nomOficina;
    }

    public void setNomOficina(String nomOficina) {
        this.nomOficina = nomOficina;
    }

    public String getCodOficinaBnf() {
        return codOficinaBnf;
    }

    public void setCodOficinaBnf(String codOficinaBnf) {
        this.codOficinaBnf = codOficinaBnf;
    }

    public String getCategorizacion() {
        return categorizacion;
    }

    public void setCategorizacion(String categorizacion) {
        this.categorizacion = categorizacion;
    }

    public String getCondicion() {
        return condicion;
    }

    public void setCondicion(String condicion) {
        this.condicion = condicion;
    }

    public String getCodDependencia() {
        return codDependencia;
    }

    public void setCodDependencia(String codDependencia) {
        this.codDependencia = codDependencia;
    }

    public String getNomDependencia() {
        return nomDependencia;
    }

    public void setNomDependencia(String nomDependencia) {
        this.nomDependencia = nomDependencia;
    }

    public String getCodCargo() {
        return codCargo;
    }

    public void setCodCargo(String codCargo) {
        this.codCargo = codCargo;
    }

    public String getDesCargo() {
        return desCargo;
    }

    public void setDesCargo(String desCargo) {
        this.desCargo = desCargo;
    }
    
}
