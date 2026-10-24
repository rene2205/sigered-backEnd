/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.fin.be.sigered;

import javax.annotation.PostConstruct;


import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ec.fin.be.autenticacion.ldap.cliente.ClienteSeguridades;
import ec.fin.be.sigered.logger.LoggerSigered;
/**
 *
 * @author dnaranjo
 */
@Configuration
public class BeanConfig {
    private static final Logger LOG = LoggerSigered.getLogger(BeanConfig.class);

    @PostConstruct
    public void init() {

        LOG.info("========================================");
        LOG.info("Iniciando configuración de SIGERED");
        LOG.info("jboss.banecuador.logsApps={}" + System.getProperty("jboss.banecuador.logsApps"));
        LOG.info("jboss.banecuador.nivellog={}" + System.getProperty("jboss.banecuador.nivellog"));
        LOG.info("Logger implementation={}" + LOG.getClass().getName());
        LOG.info("========================================");

    }

    @Bean
    public ClienteSeguridades clienteSeguridades() {
        return new ClienteSeguridades();
    }
    
    
   

}
