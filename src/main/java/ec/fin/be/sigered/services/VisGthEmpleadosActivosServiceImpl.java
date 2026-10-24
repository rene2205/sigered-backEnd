/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.fin.be.sigered.services;

import ec.fin.be.comunes.excepciones.ServiceException;
import ec.fin.be.sigered.dto.EmpleadoDto;
import ec.fin.be.sigered.logger.LoggerSigered;
import ec.fin.be.sigered.secondary.repository.VisGthEmpleadosActivosRepository;
import ec.fin.be.sigered.secondary.entity.VisGthEmpleadosActivos;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author rmoran
 */
@Service
public class VisGthEmpleadosActivosServiceImpl implements VisGthEmpleadosActivosService{
 private static final Logger LOG = LoggerSigered.getLogger(VisGthEmpleadosActivosServiceImpl.class);
    @Autowired
    VisGthEmpleadosActivosRepository repositorioEmpleado;
    
    @Override
    public List<EmpleadoDto> listaEmpleadosActivos()throws ServiceException {
        LOG.info("[VisGthEmpleadosActivosServiceImpl][listaEmpleadosActivos]");
        List <VisGthEmpleadosActivos> listaEmpleados;
        listaEmpleados = repositorioEmpleado.findAllByOrderByNomEmpleadoEmpAsc();
        return listaEmpleados.stream().map(this::entityToDto).collect(Collectors.toList());
    }
    
     @Override
    public List<EmpleadoDto> buscarEmpleadosActivos(String nomEmpleadoEmp) throws ServiceException {
         List <VisGthEmpleadosActivos> listaEmpleados;
         listaEmpleados = repositorioEmpleado.findByNomEmpleadoEmpContainingOrderByNomEmpleadoEmpAsc(nomEmpleadoEmp);
        return listaEmpleados.stream().map(this::entityToDto).collect(Collectors.toList());
    }
    
     private EmpleadoDto entityToDto(VisGthEmpleadosActivos entity){
         EmpleadoDto empleado= new EmpleadoDto();
         empleado.setSecuencia(entity.getCodigoSecuencial());
         empleado.setCedula(entity.getNumCedulaEmp());
         empleado.setNombre(entity.getNomEmpleadoEmp());
         empleado.setCargo(entity.getDesCargo());
         
         return empleado;
         
     }

   
    
}
