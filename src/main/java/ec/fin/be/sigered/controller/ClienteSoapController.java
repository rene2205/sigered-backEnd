/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.fin.be.sigered.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import ec.fin.be.autenticacion.ldap.utils.UtilProperties;
import ec.fin.be.sigered.dto.PropertiesDto;
import ec.fin.be.sigered.logger.LoggerSigered;
import ec.fin.be.sigered.utils.Constantes;
import ec.fin.be.autenticacion.ldap.ws.DtoMail;
import ec.fin.be.autenticacion.ldap.ws.MenuDto;
import ec.fin.be.autenticacion.ldap.ws.RolMDto;
import ec.fin.be.comunes.excepciones.ServiceException;
import ec.fin.be.comunes.log.LoggerBEUtil;
import ec.fin.be.sigered.UtilPropertiesArquetipo;
import ec.fin.be.sigered.BeanConfig;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 *
 * @author dnaranjo
 */

@RestController
@RequestMapping({"/soapclient"})
public class ClienteSoapController {
    private static final Logger LOG = LoggerSigered.getLogger(ClienteSoapController.class);
    public static final String MAILNOENCONTRADO = "Mail no enocontrado";

    @Autowired
    private BeanConfig beanConfig;

    @GetMapping(path = {"/{cedula}"})
    public String proxySoap(@PathVariable("cedula") Long cedula) {
        try {
            String cedulaCliente = String.valueOf(cedula);
            List<RolMDto> listRoles = beanConfig.clienteSeguridades().obtenerRolOficinaDto(cedulaCliente.length() < 10 ? "0".concat(cedulaCliente) : cedulaCliente, Integer.parseInt(UtilPropertiesArquetipo.getIntances().obtenerParametro(Constantes.WSDL_SISTEMA)), null, UtilProperties.getIntances().obtenerParametro(Constantes.SEGURIDAD_ENDPOINT));
            if (listRoles != null && !listRoles.isEmpty()) {
                return listRoles.get(0).getNombreROl();
            }
        } catch (ServiceException ex) {
            LOG.error("Error al obtener rol del cliente " + cedula, ex);
        }
        return "No existe asignación de rol";
    }

    @GetMapping(path = {"getRoles/{cedula}"})
    public List<RolMDto> getRoles(@PathVariable("cedula") Long cedula) {
        try {
            String cedulaCliente = String.valueOf(cedula);
            return beanConfig.clienteSeguridades().obtenerRolOficinaDto(cedulaCliente.length() < 10 ? "0".concat(cedulaCliente) : cedulaCliente, Integer.parseInt(UtilPropertiesArquetipo.getIntances().obtenerParametro(Constantes.WSDL_SISTEMA)), null, UtilProperties.getIntances().obtenerParametro(Constantes.SEGURIDAD_ENDPOINT));
        } catch (ServiceException ex) {
            LOG.error("Error al obtener rol del cliente " + cedula, ex);
            return Collections.emptyList();
        }
        
    }

    @GetMapping(path = {"usuarioXSistema/{nombreRol}"})
    public String usuarioXSistema(@PathVariable("nombreRol") String nombreRol) throws ServiceException {
        Gson gson = new Gson();
        return gson.toJson(beanConfig.clienteSeguridades().usuarioXSistema(Integer.parseInt(UtilPropertiesArquetipo.getIntances().obtenerParametro(Constantes.WSDL_SISTEMA)), nombreRol, UtilProperties.getIntances().obtenerParametro(Constantes.SEGURIDAD_ENDPOINT)));
    }

    @GetMapping(path = {"listarMenu/{cedula}", "listarMenu/{cedula}/{rol}"})
    public String listarMenu(@PathVariable("cedula") String cedula, @PathVariable(value =  "rol", required = false) Integer rol) {
        Gson gson = new Gson();
        ec.fin.be.sigered.dto.MenuDto menu = new ec.fin.be.sigered.dto.MenuDto();
        try {
            List<MenuDto> menuAux;
            menuAux = beanConfig.clienteSeguridades().generarMenu(cedula.length() < 10 ? "0".concat(cedula) : cedula,
                    Integer.parseInt(UtilPropertiesArquetipo.getIntances().obtenerParametro(Constantes.WSDL_SISTEMA)),
                    null, rol, UtilProperties.getIntances().obtenerParametro(Constantes.SEGURIDAD_ENDPOINT));
            if (menuAux != null && !menuAux.isEmpty()) {
                MenuDto menuSeguridades = menuAux.get(0);
                menu.setLabel(menuSeguridades.getNombre());
                menu.setRouterLink(menuSeguridades.getUrl());
                menu.setExpanded(Boolean.TRUE);
                generarMenu(menuSeguridades, menu, 0);

            }

        } catch (NumberFormatException e) {//NOSONAR
            LOG.error("Error en listarMenu - cedula: " + cedula, e);
        } catch (ServiceException e) {//NOSONAR
            LOG.error("Error en listarMenu - cedula: " + cedula, e);
        }
        return "[" + gson.toJson(menu) + "]";
    }

    @GetMapping(path = {"porcodigo/{codParam}"})
    public String tramite(@PathVariable("codParam") String codParam) {
        try {
            String codSistema = UtilPropertiesArquetipo.getIntances().obtenerParametro(Constantes.WSDL_SISTEMA);
            
            String urlWSSeguridad = UtilProperties.getIntances().obtenerParametro(Constantes.SEGURIDAD_ENDPOINT);
            return beanConfig.clienteSeguridades().obtenerParamXSisXCod(Integer.parseInt(codSistema), codParam, urlWSSeguridad);
        } catch (ServiceException ex) {//NOSONAR
            return ex.getMensaje();
        }
    }

    @PostMapping(path = {"/enviarmail"})
    public void enviarEmail(@RequestBody String dtoMailJson) throws ServiceException {
        try {
            DtoMail dtoMail = new Gson().fromJson(dtoMailJson, DtoMail.class);
            dtoMail.setCodSistmea(Integer.parseInt(UtilPropertiesArquetipo.getIntances().obtenerParametro(Constantes.WSDL_SISTEMA)));
            dtoMail.getListaImagenes().add("logo.png");
            beanConfig.clienteSeguridades().enviarCorreo(dtoMail);
        } catch (ServiceException ex) {//NOSONAR
            LoggerBEUtil.error(this.getClass(), "Error en el envio del correo electronico: " + ex.getMensajeTecnico(), ex);
            throw ex;
        }
    }

    public ec.fin.be.sigered.dto.MenuDto generarMenu(MenuDto menuSeguridades, ec.fin.be.sigered.dto.MenuDto menu, Integer cont) {
        ec.fin.be.sigered.dto.MenuDto hijo;
        List<ec.fin.be.sigered.dto.MenuDto> listHijos = new ArrayList<>();
        if (!menuSeguridades.getHijos().isEmpty()) {
            for (MenuDto m : menuSeguridades.getHijos()) {

                hijo = new ec.fin.be.sigered.dto.MenuDto();
                hijo.setLabel(m.getNombre());
                hijo.setRouterLink(m.getUrl());
                listHijos.add(hijo);
                hijo.setExpanded(Boolean.FALSE);
                if (cont == 0) {
                    hijo.setExpanded(Boolean.TRUE);
                }

                cont++;
                generarMenu(m, hijo, cont);

            }
            menu.setItems(listHijos);
        }
        return menu;
    }

    @GetMapping(path = {"getProperties"})
    public List<PropertiesDto> getProperties() {
        List<PropertiesDto> propiedades = new ArrayList<>();
        propiedades.add(crearPropiedad(Constantes.KEYCLOAK));
        propiedades.add(crearPropiedad(Constantes.KEYCLOAK_CLIENT));
        propiedades.add(crearPropiedad(Constantes.KEYCLOAK_REALM));
        propiedades.add(crearPropiedad(Constantes.KEYCLOAK_SECRET));
        propiedades.add(crearPropiedad(Constantes.KEYCLOAK_SSL));

        return propiedades;
    }

    private PropertiesDto crearPropiedad(String constante) {
        String valor = UtilPropertiesArquetipo.getIntances().getProperties(constante);
        PropertiesDto propiedad = new PropertiesDto();
        propiedad.setName(constante);
        propiedad.setValue(valor);
        return propiedad;
    }

}
