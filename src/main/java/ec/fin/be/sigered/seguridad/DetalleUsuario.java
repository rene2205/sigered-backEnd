/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.fin.be.sigered.seguridad;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import ec.fin.be.autenticacion.ldap.cliente.ClienteSeguridades;
import ec.fin.be.autenticacion.ldap.utils.UtilProperties;
import ec.fin.be.autenticacion.ldap.ws.RolMDto;
import ec.fin.be.comunes.excepciones.ServiceException;
import ec.fin.be.sigered.UtilPropertiesArquetipo;
import ec.fin.be.sigered.logger.LoggerSigered;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author pvillarruel
 */
@Data
@AllArgsConstructor
@Service
public class DetalleUsuario implements UserDetails {
    private static final Logger LOG = LoggerSigered.getLogger(DetalleUsuario.class);

    private String cedula;
    private String username;

    public DetalleUsuario() {
        super();
    }

    public DetalleUsuario(String cedula) {
        this.cedula = cedula;
    }

    @Bean
    public ClienteSeguridades clienteSeguridades1() {
        return new ClienteSeguridades();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> roles = new ArrayList<>();
        try {
            String codSistema = UtilPropertiesArquetipo.getIntances().obtenerParametro("WSDL.CodSistema");
            
            String urlWSSeguridad = UtilProperties.getIntances().obtenerParametro("seguridad.endpoint.login");
            List<RolMDto> rolesWS = clienteSeguridades1().obtenerRolOficinaDto(cedula, Integer.parseInt(codSistema),
                    null, urlWSSeguridad);
            if (rolesWS != null && !rolesWS.isEmpty()) {
                for (RolMDto mDto : rolesWS) {
                    roles.add(new SimpleGrantedAuthority(mDto.getNombreROl()));
                }
            }
            return roles;
        } catch (ServiceException ex) {
            LOG.error("Error al obtener rol del cliente " + cedula, ex);
        }
        return roles;
    }

    @Override
    public String getPassword() {
        return "pass";
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
