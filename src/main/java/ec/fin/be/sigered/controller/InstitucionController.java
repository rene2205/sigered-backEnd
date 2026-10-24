package ec.fin.be.sigered.controller;

import java.security.Principal;
import java.util.List;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ec.fin.be.comunes.excepciones.ServiceException;
import ec.fin.be.sigered.dto.InstitucionDto;
import ec.fin.be.sigered.dto.InstitucionFiltroDto;
import ec.fin.be.sigered.dto.ResponseDto;
import ec.fin.be.sigered.logger.LoggerSigered;
import ec.fin.be.sigered.services.DoInstitucionServicio;
import ec.fin.be.sigered.utils.Constantes;
import ec.fin.be.sigered.utils.Utils;

@RestController
@RequestMapping("/instituciones")
public class InstitucionController {
    private static final Logger LOG = LoggerSigered.getLogger(InstitucionController.class);

    @Autowired
    private DoInstitucionServicio institucionServicio;

    /**
     * Obtener instituciones por estado
     */
    @GetMapping
    public List<InstitucionDto> obtenerInstituciones(@RequestParam(required = false) String estado) throws ServiceException {
        return institucionServicio.obtenerInstituciones(estado);
    }

    /**
     * Buscar instituciones
     * Ej:
     * POST /api/instituciones/buscar
     */
    @PostMapping("/buscar")
    public List<InstitucionDto> buscarInstituciones(@RequestBody InstitucionFiltroDto filtro) throws ServiceException {
        return institucionServicio.buscarInstituciones(filtro);
    }

    /**
     * Obtener una institución por ID
     */
    @GetMapping("/{id}")
    public InstitucionDto obtenerInstitucion(@PathVariable int id) throws ServiceException {
        return institucionServicio.obtenerInstitucion(id);
    }

    /**
     * Crear o actualizar institución
     */
    @PostMapping
    public ResponseDto guardarInstitucion(@RequestBody InstitucionDto dto, Principal principal) throws ServiceException {
        ResponseDto response = Utils.devolverRespuesta(Constantes.CODIGOS_RESPUESTA.ER_ERROR);

        try {
            String usuario = (principal != null) ? principal.getName() : Constantes.SYSTEM_USER;
            
            int id = institucionServicio.guardarInstitucion(dto, usuario);    
            response = Utils.devolverRespuesta(Constantes.CODIGOS_RESPUESTA.OK_00);
            response.setData(id);
            return response;

        } catch (Exception e) {
            LOG.error("Error al guardar institucion", e);
            response.setMensajeError(e.getMessage());
            return response;
        }
        
    }

    /**
     * Eliminación lógica
     */
    @DeleteMapping("/{id}")
    public ResponseDto eliminarInstitucion(@PathVariable int id, Principal principal) throws ServiceException {
        ResponseDto response = Utils.devolverRespuesta(Constantes.CODIGOS_RESPUESTA.ER_ERROR);

        try {
            String usuario = (principal != null) ? principal.getName() : Constantes.SYSTEM_USER;

            boolean result = institucionServicio.eliminarInstitucion(id, usuario);
            response = Utils.devolverRespuesta(Constantes.CODIGOS_RESPUESTA.OK_00);
            response.setData(result);
            return response;

        } catch (Exception e) {
            LOG.error("Error al eliminar institucion", e);
            response.setMensajeError(e.getMessage());
            return response;
        }
        
    }
}