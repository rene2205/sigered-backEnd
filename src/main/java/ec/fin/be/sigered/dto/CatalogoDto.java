/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.fin.be.sigered.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 *
 * @author rmoran
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("squid:S1068")
public class CatalogoDto {
    String pCodigo;
    String pValor;
    String pEstado;
    String pCodigoPadre;
    String pTabla;

    public String getpTabla() {
        return pTabla;
    }

    public void setpTabla(String pTabla) {
        this.pTabla = pTabla;
    }

    public String getpCodigo() {
        return pCodigo;
    }

    public void setpCodigo(String pCodigo) {
        this.pCodigo = pCodigo;
    }

    public String getpValor() {
        return pValor;
    }

    public void setpValor(String pValor) {
        this.pValor = pValor;
    }

    public String getpEstado() {
        return pEstado;
    }

    public void setpEstado(String pEstado) {
        this.pEstado = pEstado;
    }

    public String getpCodigoPadre() {
        return pCodigoPadre;
    }

    public void setpCodigoPadre(String pCodigoPadre) {
        this.pCodigoPadre = pCodigoPadre;
    }
}
