package ec.fin.be.sigered.services;

import java.util.List;

import ec.fin.be.comunes.excepciones.ServiceException;
import ec.fin.be.sigered.dto.GerenciaDto;
import ec.fin.be.sigered.dto.GerenciaFiltroDto;

public interface DoGerenciaServicio {

    List<GerenciaDto> obtenerGerencias(String estado) throws ServiceException;

    List<GerenciaDto> buscarGerencias(GerenciaFiltroDto filtro) throws ServiceException;

    GerenciaDto obtenerGerencia(int id) throws ServiceException;

    int guardarGerencia(GerenciaDto gerencia, String usuario) throws ServiceException;

    boolean eliminarGerencia(int id, String usuario) throws ServiceException;
}
