package ec.fin.be.sigered.services;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.fin.be.comunes.excepciones.ServiceException;
import ec.fin.be.sigered.dto.GerenciaDto;
import ec.fin.be.sigered.dto.GerenciaFiltroDto;
import ec.fin.be.sigered.primary.entity.DoGerencia;
import ec.fin.be.sigered.logger.LoggerSigered;
import ec.fin.be.sigered.primary.repository.DoGerenciaRepositorio;
import ec.fin.be.sigered.specification.DoGerenciaSpecification;
import ec.fin.be.sigered.utils.Constantes;

@Service
public class DoGerenciaServicioImpl implements DoGerenciaServicio {
    private static final Logger LOG = LoggerSigered.getLogger(DoGerenciaServicioImpl.class);
    
    @Autowired
    private DoGerenciaRepositorio repositorioGerencia;

    @Override
    public List<GerenciaDto> obtenerGerencias(String estado) throws ServiceException {
        LOG.info("Inicio obtenerGerencias - estado: " + estado);

        try {
            List<DoGerencia> lista;

            if (estado != null && !estado.trim().isEmpty()) {
                lista = repositorioGerencia.findByEstado(estado);
            } else {
                lista = repositorioGerencia.findAll();
            }

            LOG.info("Total gerencias obtenidas: " + lista.size());

            return lista.stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());

        } catch (Exception ex) {
            LOG.error("Error en obtenerGerencias", ex);
            throw new ServiceException(ex);
        }
    }

    @Override
    public List<GerenciaDto> buscarGerencias(GerenciaFiltroDto filtro) throws ServiceException {
        LOG.info("Inicio buscarGerencias");

        try {
            List<DoGerencia> lista = repositorioGerencia.findAll(DoGerenciaSpecification.buscar(filtro));

            LOG.info("Total gerencias obtenidas: " + lista.size());

            return lista.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        } catch (Exception ex) {
            LOG.error("Error en buscarGerencias", ex);
            throw new ServiceException(ex);
        }
    }

    @Override
    public GerenciaDto obtenerGerencia(int id) throws ServiceException {
        LOG.info("Inicio obtenerGerencia - id: " + id);

        try {
            DoGerencia entity = repositorioGerencia.findById(id);
            if (entity == null) {
                LOG.warn("No se encontró gerencia con id: " + id);
                return null;
            }

            return toDto(entity);
        } catch (Exception ex) {
            LOG.error("Error en obtenerGerencia - id: " + id, ex);
            throw new ServiceException(ex);
        }
    }

    @Override
    public int guardarGerencia(GerenciaDto dto, String usuario) throws ServiceException {
        LOG.info("Inicio guardarGerencia - usuario: " + usuario + ", id: " + dto.getId());

        try {
            DoGerencia entity;

            if (dto.getId() != null && dto.getId() > 0) {
                LOG.debug("Actualizando gerencia - id: " + dto.getId());

                entity = validarYObtenerGerencia(dto.getId());
                entity.setFechaModificacion(LocalDateTime.now());
                entity.setUsuarioModificacion(usuario);
            } else {
                LOG.debug("Creando nueva gerencia - codigo: " + dto.getCodigo());

                entity = new DoGerencia();
                entity.setFechaCreacion(LocalDateTime.now());
                entity.setUsuarioCreacion(usuario);
            }

            //validar código de gerencia duplicado
            DoGerencia gerenciaExistente = repositorioGerencia.findByCodigo(dto.getCodigo());
            if (gerenciaExistente != null 
                && !gerenciaExistente.getId().equals(entity.getId()) 
                && Constantes.ESTADO_ACTIVO.equals(gerenciaExistente.getEstado())) {
                String mensaje = "El código de gerencia ya se encuentra registrado.";
                LOG.error(mensaje);
                throw new InvalidParameterException(mensaje);
            }
            
            // mapping DTO → Entity
            entity.setCodigo(dto.getCodigo());
            entity.setNombre(dto.getNombre());
            entity.setEstado(Constantes.ESTADO_ACTIVO);

            // set padre de la gerencia si existe
            if (dto.getPadreId() != null && dto.getPadreId() > 0) {
                Optional<DoGerencia> padre = repositorioGerencia.findById(dto.getPadreId());

                if (padre.isPresent()) {
                    entity.setPadre(padre.get());
                }
            }

            entity = repositorioGerencia.save(entity);

            LOG.info("gerencia guardada correctamente - id: " + entity.getId() + ", codigo: " + entity.getCodigo());            
            return entity.getId();

        } catch (Exception ex) {
            LOG.error("Error en guardarGerencia - usuario: " + usuario, ex);
            throw new ServiceException(ex);
        }
    }

    @Override
    public boolean eliminarGerencia(int id, String usuario) throws ServiceException {
        LOG.info("Inicio eliminarGerencia - id: " + id + ", usuario: " + usuario);

        try {
            DoGerencia entity = repositorioGerencia.findById(id);

            if (entity == null) {
                String mensaje = "No existe la gerencia con id: " + id + ".";
                LOG.error(mensaje);
                throw new InvalidParameterException(mensaje);
            }

            // validar si tiene subgerencias en estado activo
            List<DoGerencia> subgerencias = repositorioGerencia.findByPadreAndEstado(entity, Constantes.ESTADO_ACTIVO);
            if (subgerencias != null && !subgerencias.isEmpty()) {
                String mensaje = "No se puede eliminar la gerencia con id: " + id + " porque tiene subgerencias en estado activo.";
                LOG.error(mensaje);
                throw new InvalidParameterException(mensaje);
            }

            // Eliminación lógica
            entity.setEstado(Constantes.ESTADO_ELIMINADO);
            entity.setFechaEliminacion(LocalDateTime.now());
            entity.setUsuarioEliminacion(usuario);

            repositorioGerencia.save(entity);

            LOG.info("Gerencia eliminada lógicamente - id: " + id);
            return true;

        } catch (Exception ex) {
            LOG.error("Error en eliminarGerencia - id: " + id + ", usuario: " + usuario, ex);
            throw new ServiceException(ex);
        }
    }

    private DoGerencia validarYObtenerGerencia(Integer id) {
        // validar existencia
        Optional<DoGerencia> optional = repositorioGerencia.findById(id);
        if (!optional.isPresent()) {
            String mensaje = "No existe registro de la gerencia con id: " + id + ".";
            LOG.error(mensaje);
            throw new InvalidParameterException(mensaje);
        }

        DoGerencia entity = optional.get();
        
        // validar estado eliminado
        if (entity.getEstado().equals(Constantes.ESTADO_ELIMINADO)) {
            String mensaje = "Intento de actualización de gerencia eliminada - id: " + id;
            LOG.error(mensaje);
            throw new InvalidParameterException(mensaje);
        }

        return entity;
    }

    // =========================
    // MAPPERS
    // =========================

    private GerenciaDto toDto(DoGerencia entity) {
        if (entity == null) {
            return null;
        }
        
        GerenciaDto dto = new GerenciaDto();
        dto.setId(entity.getId());
        dto.setCodigo(entity.getCodigo());
        dto.setNombre(entity.getNombre());
        dto.setEstado(entity.getEstado());

        // set padre
        DoGerencia padre = entity.getPadre();
        if (padre != null) {
            dto.setPadreId(padre.getId());
            dto.setPadreCodigo(padre.getCodigo());
            dto.setPadreNombre(padre.getNombre());
        }

        return dto;
    }

}