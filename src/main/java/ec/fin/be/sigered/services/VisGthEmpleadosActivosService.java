/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.fin.be.sigered.services;

import ec.fin.be.comunes.excepciones.ServiceException;
import ec.fin.be.sigered.dto.EmpleadoDto;
import java.util.List;

/**
 *
 * @author rmoran
 */
public interface VisGthEmpleadosActivosService {
    
    List<EmpleadoDto> listaEmpleadosActivos() throws ServiceException;
    List<EmpleadoDto> buscarEmpleadosActivos(String nomEmpleadoEmp) throws ServiceException;
    
}
