package ec.fin.be.sigered.services;

import java.util.List;

import ec.fin.be.comunes.excepciones.ServiceException;
import ec.fin.be.sigered.dto.DelegadoDto;
import ec.fin.be.sigered.dto.DelegadoFiltroDto;

public interface DoDelegadoServicio {

    List<DelegadoDto> obtenerDelegados(String estado) throws ServiceException;

    List<DelegadoDto> buscarDelegados(DelegadoFiltroDto filtro) throws ServiceException;

    DelegadoDto obtenerDelegado(int id) throws ServiceException;

    int guardarDelegado(DelegadoDto delegado, String usuario) throws ServiceException;

    boolean eliminarDelegado(int id, String usuario) throws ServiceException;
}
