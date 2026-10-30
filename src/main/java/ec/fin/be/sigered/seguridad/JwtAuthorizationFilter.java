/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.fin.be.sigered.seguridad;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import ec.fin.be.sigered.logger.LoggerSigered;


/**
 *
 * @author pvillarruel
 */
@Service
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private static final Logger LOG = LoggerSigered.getLogger(JwtAuthorizationFilter.class);
    private static final String HEADER_TOKEN_PREFIX = "Bearer ";
    private static final String HEADER_AUTHORIZATION = "Authorization";

    @Autowired
    private DetallesUsuarioService myUserDetailService;

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);

            if (authorizationHeader != null && authorizationHeader.startsWith(HEADER_TOKEN_PREFIX)) {

                String token = authorizationHeader.replace(HEADER_TOKEN_PREFIX, "");
                String username = jwtService.extractUsername(token);
                String cedula = jwtService.extractCedula(token);
                
                UserDetails userDetails = myUserDetailService.loadUserByUsername(username);
                if (jwtService.validateToken(token, username)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, userDetails.getAuthorities());
                    authentication.setDetails(cedula);
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                }
            }
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            LOG.error("Error en JwtAuthorizationFilter", e);
            throw new ServletException(e);
        }
    }

   
}
