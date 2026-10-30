/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.fin.be.sigered.dto;

import lombok.Data;

/**
 *
 * @author pvillarruel
 */
@Data
@SuppressWarnings("squid:S1068")
public class JwtDto {
    private String cedula;
    private String usuario;
    private String correo;
    private String jwt;

    public JwtDto(String cedula, String usuario,String correo, String jwt) {
        this.cedula = cedula;
        this.usuario = usuario;
        this.correo = correo;
        this.jwt = jwt;
    }

    public JwtDto() {
    }
    
    
}
