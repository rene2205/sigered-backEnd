/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.fin.be.sigered.seguridad;
import ec.fin.be.sigered.BeanConfig;

import java.util.Base64;
import java.util.Date;
import java.util.List;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ec.fin.be.sigered.dto.JwtDto;
import ec.fin.be.sigered.logger.LoggerSigered;
import ec.fin.be.autenticacion.ldap.cliente.ClienteSeguridades;
import ec.fin.be.autenticacion.ldap.ws.RolMDto;
import ec.fin.be.autenticacion.ldap.ws.WcsRol;
import ec.fin.be.comunes.excepciones.ServiceException;
import ec.fin.be.sigered.UtilPropertiesArquetipo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 *
 * @author pvillarruel
 */
@Service
public class JwtService {
    private static final Logger LOG = LoggerSigered.getLogger(JwtService.class);
    private static final int EXPIRATION_TIME = 1000 * 60 * 60;
    private static final String AUTHORITIES = "authorities";
    private static final String CEDULA = "cedula";
    private static final String CORREO = "correo";
    private final String secretKey;
    private static final String ER_VALIDATE_TOKEN = "Error al validar token";

    @Autowired
    private BeanConfig soapClient;

    public JwtService() {
        secretKey = Base64.getEncoder().encodeToString("key".getBytes());
    }

    public JwtDto createToken(String cedula, String usuario, String correo) throws ServiceException {
        try {
            String codSistema = UtilPropertiesArquetipo.getIntances().obtenerParametro("WSDL.CodSistema");
            String urlWSSeguridad = UtilPropertiesArquetipo.getIntances().obtenerParametro("seguridad.endpoint.login");
            ClienteSeguridades clienteSeguridades = soapClient.clienteSeguridades();
    
            // Validar que usuario tenga roles
            List<RolMDto> authorities = clienteSeguridades.obtenerRolOficinaDto(cedula, Integer.parseInt(codSistema), null, urlWSSeguridad);

            if (authorities == null || authorities.isEmpty()) {
                LOG.error("No se encontraron roles para la cédula: " + cedula);
                throw new ServiceException("No se encontraron roles");
            }
            
            String jwt = Jwts.builder()
                    .setSubject(usuario)
                    .claim(CEDULA, cedula)
                    .claim(CORREO, correo)
                    .claim(AUTHORITIES, authorities)
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .signWith(SignatureAlgorithm.HS512, secretKey)
                    .compact();

            return new JwtDto(cedula, usuario,correo ,jwt);
            
        } catch (Exception e) {
            LOG.error("Error al crear token", e);
            throw new ServiceException("Error al crear token");
        }
    }

    public boolean hasTokenExpired(String token) throws ServiceException {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration()
                    .before(new Date());
        } catch (Exception e) {
            LOG.error(ER_VALIDATE_TOKEN, e);
            throw new ServiceException(ER_VALIDATE_TOKEN);
        }
    }

    public boolean validateToken(String token, String user) throws ServiceException {
        try {
            String username = extractUsername(token);
            return (user.equals(username) && !hasTokenExpired(token));

        } catch (Exception e) {
            LOG.error(ER_VALIDATE_TOKEN, e);
            throw new ServiceException(ER_VALIDATE_TOKEN);
        }

    }

    public String extractUsername(String token) throws ServiceException {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();

        } catch (Exception e) {
            LOG.error("Error al extraer username", e);
            throw new ServiceException("Error al extraer username");
        }
    }

    public String extractCedula(String token) throws ServiceException {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();

            return claims.get(CEDULA, String.class);

        } catch (Exception e) {
            LOG.error("Error al extraer cedula", e);
            throw new ServiceException("Error al extraer cedula");
        }
    }
    
    public String extractCorreo(String token) throws ServiceException {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();

            return claims.get(CORREO, String.class);

        } catch (Exception e) {
            LOG.error("Error al extraer correo", e);
            throw new ServiceException("Error al extraer correo");
        }
    }

    public List<WcsRol> getAuthorities(String token) throws ServiceException {
        try {
            Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
            return (List<WcsRol>) claims.get(AUTHORITIES);

        } catch (Exception e) {
            LOG.error("Error al extraer authorities", e);
            throw new ServiceException("Error al extraer authorities");
        }
    }
}
