/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.fin.be.sigered.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ec.fin.be.sigered.dto.JwtDto;
import ec.fin.be.sigered.seguridad.JwtService;
import ec.fin.be.comunes.excepciones.ServiceException;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author pvillarruel
 */

@RestController
@RequiredArgsConstructor
public class PermisosUsuarioController {

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public JwtDto createToken(@RequestBody JwtDto dtoJwt) throws ServiceException {
        if (dtoJwt.getCedula() != null && !dtoJwt.getCedula().isEmpty()) {
            String cedula = dtoJwt.getCedula().length() < 10 ? "0".concat(dtoJwt.getCedula()) : dtoJwt.getCedula();

            return jwtService.createToken(cedula, dtoJwt.getUsuario());
        }
        throw new ServiceException("Cédula incorrecta");
    }

}
