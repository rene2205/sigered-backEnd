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
import ec.fin.be.sigered.dto.GerenciaDto;
import ec.fin.be.sigered.dto.GerenciaFiltroDto;
import ec.fin.be.sigered.dto.ResponseDto;
import ec.fin.be.sigered.logger.LoggerSigered;
import ec.fin.be.sigered.services.DoGerenciaServicio;
import ec.fin.be.sigered.utils.Constantes;
import ec.fin.be.sigered.utils.Utils;

@RestController
@RequestMapping("/gerencias")
public class GerenciaController {
    private static final Logger LOG = LoggerSigered.getLogger(GerenciaController.class);

    @Autowired
    private DoGerenciaServicio gerenciaServicio;

    /**
     * Obtener lista de gerencias
     * Ej:
     * GET /api/gerencias
     * GET /api/gerencias?estado=A
     */
    @GetMapping
    public List<GerenciaDto> obtenerGerencias(@RequestParam(required = false) String estado) throws ServiceException {
        return gerenciaServicio.obtenerGerencias(estado);
    }

    /**
     * Buscar gerencias
     * Ej:
     * POST /api/gerencias/buscar
     */
    @PostMapping("/buscar")
    public List<GerenciaDto> buscarGerencias(@RequestBody GerenciaFiltroDto filtro) throws ServiceException {
        return gerenciaServicio.buscarGerencias(filtro);
    }

    /**
     * Obtener una institución por ID
     */
    @GetMapping("/{id}")
    public GerenciaDto obtenerGerencia(@PathVariable int id) throws ServiceException {
        return gerenciaServicio.obtenerGerencia(id);
    }

    /**
     * Crear o actualizar institución
     */
    @PostMapping
    public ResponseDto guardarGerencia(@RequestBody GerenciaDto dto, Principal principal) throws ServiceException {
        ResponseDto response = Utils.devolverRespuesta(Constantes.CODIGOS_RESPUESTA.ER_ERROR);

        try {
            String usuario = (principal != null) ? principal.getName() : Constantes.SYSTEM_USER;

            int id = gerenciaServicio.guardarGerencia(dto, usuario);    
            response = Utils.devolverRespuesta(Constantes.CODIGOS_RESPUESTA.OK_00);
            response.setData(id);
            return response;

        } catch (Exception e) {
            LOG.error("Error al guardar gerencia", e);
            response.setMensajeError(e.getMessage());
            return response;
        }
        
    }

    /**
     * Eliminación lógica
     */
    @DeleteMapping("/{id}")
    public ResponseDto eliminarGerencia(@PathVariable int id, Principal principal) throws ServiceException {
        ResponseDto response = Utils.devolverRespuesta(Constantes.CODIGOS_RESPUESTA.ER_ERROR);

        try {
            String usuario = (principal != null) ? principal.getName() : Constantes.SYSTEM_USER;

            boolean result = gerenciaServicio.eliminarGerencia(id, usuario);    
            response = Utils.devolverRespuesta(Constantes.CODIGOS_RESPUESTA.OK_00);
            response.setData(result);
            return response;

        } catch (Exception e) {
            LOG.error("Error al eliminar gerencia", e);
            response.setMensajeError(e.getMessage());
            return response;
        }

        
    }
}