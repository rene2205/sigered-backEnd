package ec.fin.be.sigered.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import ec.fin.be.comunes.excepciones.ServiceException;
import ec.fin.be.sigered.dto.SolicitudDto;
import ec.fin.be.sigered.dto.SolicitudFiltroDto;

public interface DoSolicitudServicio {
    
    List<SolicitudDto> obtenerSolicitudes() throws ServiceException;

    List<SolicitudDto> obtenerSolicitudesAEliminar() throws ServiceException;

    int obtenerCantidadSolicitudesAEliminar(String cedulaDelegado) throws ServiceException;
    
    SolicitudDto obtenerSolicitud(int id) throws ServiceException;

    List<SolicitudDto> buscarSolicitudes(SolicitudFiltroDto filtro, int pageSize, String cedulaUsuario) throws ServiceException;
    
    int guardarSolicitud(SolicitudDto solicitud, String usuario) throws ServiceException;

    boolean cerrarSolicitud(int id, String usuario) throws ServiceException;

    boolean prorrogarSolicitud(int id, int diasProrroga, String usuario) throws ServiceException;

    boolean reabrirSolicitud(int id, String usuario) throws ServiceException;

    boolean deshabilitarSolicitud(int id, String razonAnulacion, String usuario) throws ServiceException;

    boolean eliminarDefinitivamenteSolicitudYAvances(int id, String usuario, String cedula) throws ServiceException;

    int guardarSolicitudConDocumentos(SolicitudDto solicitud, MultipartFile[] multipartFiles, String usuario) throws ServiceException;
    
}
