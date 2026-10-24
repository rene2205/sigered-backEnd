/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.fin.be.sigered.controller;

import ec.fin.be.comunes.excepciones.ServiceException;
import ec.fin.be.sigered.dto.EmpleadoDto;
import ec.fin.be.sigered.services.VisGthEmpleadosActivosServiceImpl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author rmoran
 */

@RestController
@RequestMapping("/empleado")
public class EmpleadoController {
    
    @Autowired
    VisGthEmpleadosActivosServiceImpl servicioEmpleado;
    
    @GetMapping("/listarEmpleados")
    public List<EmpleadoDto> listarEmpleador() throws ServiceException{
        return servicioEmpleado.listaEmpleadosActivos();
    }
    
    @GetMapping("/buscarEmpleados/{nombre}")
    public List<EmpleadoDto> listarEmpleador(@PathVariable("nombre") String nombre) throws ServiceException{
        return servicioEmpleado.buscarEmpleadosActivos(nombre);
    }
}
