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
import ec.fin.be.sigered.dto.DelegadoDto;
import ec.fin.be.sigered.dto.DelegadoFiltroDto;
import ec.fin.be.sigered.dto.ResponseDto;
import ec.fin.be.sigered.logger.LoggerSigered;
import ec.fin.be.sigered.services.DoDelegadoServicio;
import ec.fin.be.sigered.utils.Constantes;
import ec.fin.be.sigered.utils.Constantes.CODIGOS_RESPUESTA;
import ec.fin.be.sigered.utils.Utils;

@RestController
@RequestMapping("/delegados")
public class DelegadoController {
    private static final Logger LOG = LoggerSigered.getLogger(DelegadoController.class);

    @Autowired
    private DoDelegadoServicio delegadoServicio;

    /**
     * Obtener lista de delegados
     * Ej:
     * GET /api/delegados
     * GET /api/delegados?estado=A
     */
    @GetMapping
    public List<DelegadoDto> obtenerDelegados(@RequestParam(required = false) String estado) throws ServiceException {
        return delegadoServicio.obtenerDelegados(estado);
    }

    /**
     * Buscar delegados
     * Ej:
     * POST /api/delegados/buscar
     */
    @PostMapping("/buscar")
    public List<DelegadoDto> buscarDelegados(@RequestBody DelegadoFiltroDto filtro) throws ServiceException {
        return delegadoServicio.buscarDelegados(filtro);
    }

    /**
     * Obtener una institución por ID
     */
    @GetMapping("/{id}")
    public DelegadoDto obtenerDelegado(@PathVariable int id) throws ServiceException {
        return delegadoServicio.obtenerDelegado(id);
    }

    /**
     * Crear o actualizar institución
     */
    @PostMapping
    public ResponseDto guardarDelegado(@RequestBody DelegadoDto dto, Principal principal) throws ServiceException {
        ResponseDto response = Utils.devolverRespuesta(CODIGOS_RESPUESTA.ER_ERROR);
        
        try {
            String usuario = (principal != null) ? principal.getName() : Constantes.SYSTEM_USER;

            int id = delegadoServicio.guardarDelegado(dto, usuario);
            response = Utils.devolverRespuesta(CODIGOS_RESPUESTA.OK_00);
            response.setData(id);
            return response;

        } catch (Exception e) {
            LOG.error("Error al guardar delegado", e);
            response.setMensajeError(e.getMessage());
            return response;
        }
        
    }

    /**
     * Eliminación lógica
     */
    @DeleteMapping("/{id}")
    public ResponseDto eliminarDelegado(@PathVariable int id, Principal principal) throws ServiceException {
        ResponseDto response = Utils.devolverRespuesta(Constantes.CODIGOS_RESPUESTA.ER_ERROR);
        try {
            String usuario = (principal != null) ? principal.getName() : Constantes.SYSTEM_USER;

            boolean result = delegadoServicio.eliminarDelegado(id, usuario);    
            response = Utils.devolverRespuesta(Constantes.CODIGOS_RESPUESTA.OK_00);
            response.setData(result);
            return response;

        } catch (Exception e) {
            LOG.error("Error al eliminar delegado", e);
            response.setMensajeError(e.getMessage());
            return response;
        }
        
    }
}