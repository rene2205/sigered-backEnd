package ec.fin.be.sigered.services;

import java.util.List;

import ec.fin.be.comunes.excepciones.ServiceException;
import ec.fin.be.sigered.dto.InstitucionDto;
import ec.fin.be.sigered.dto.InstitucionFiltroDto;

public interface DoInstitucionServicio {

    List<InstitucionDto> obtenerInstituciones(String estado) throws ServiceException;

    List<InstitucionDto> buscarInstituciones(InstitucionFiltroDto filtro) throws ServiceException;

    InstitucionDto obtenerInstitucion(int id) throws ServiceException;

    int guardarInstitucion(InstitucionDto institucionDto, String usuario) throws ServiceException;

    boolean eliminarInstitucion(int id, String usuario) throws ServiceException;

}