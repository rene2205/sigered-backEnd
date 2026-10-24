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
import ec.fin.be.sigered.dto.InstitucionDto;
import ec.fin.be.sigered.dto.InstitucionFiltroDto;
import ec.fin.be.sigered.primary.entity.DoInstitucion;
import ec.fin.be.sigered.logger.LoggerSigered;
import ec.fin.be.sigered.primary.repository.DoInstitucionRepositorio;
import ec.fin.be.sigered.specification.DoInstitucionSpecification;
import ec.fin.be.sigered.utils.Constantes;

@Service
public class DoInstitucionServicioImpl implements DoInstitucionServicio {
    private static final Logger LOG = LoggerSigered.getLogger(DoInstitucionServicioImpl.class);
    
    @Autowired
    private DoInstitucionRepositorio repositorioInstitucion;

    @Override
    public List<InstitucionDto> obtenerInstituciones(String estado) throws ServiceException {
        LOG.info("Inicio obtenerInstituciones - estado: " + estado);

        try {
            List<DoInstitucion> lista;

            if (estado != null && !estado.trim().isEmpty()) {
                lista = repositorioInstitucion.findByEstado(estado);
            } else {
                lista = repositorioInstitucion.findAll();
            }

            LOG.info("Total instituciones obtenidas: " + lista.size());

            return lista.stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());

        } catch (Exception ex) {
            LOG.error("Error en obtenerInstituciones", ex);
            throw new ServiceException(ex);
        }
    }

    @Override
    public List<InstitucionDto> buscarInstituciones(InstitucionFiltroDto filtro) throws ServiceException {
        LOG.info("Inicio buscarInstituciones");

        try {
            List<DoInstitucion> lista = repositorioInstitucion.findAll(DoInstitucionSpecification.buscar(filtro));

            LOG.info("Total instituciones obtenidas: " + lista.size());

            return lista.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        } catch (Exception ex) {
            LOG.error("Error en buscarInstituciones", ex);
            throw new ServiceException(ex);
        }
    }

    @Override
    public InstitucionDto obtenerInstitucion(int id) throws ServiceException {
        LOG.info("Inicio obtenerInstitucion - id: " + id);

        try {
            DoInstitucion entity = repositorioInstitucion.findById(id);
            if (entity == null) {
                LOG.warn("No se encontró institución con id: " + id);
                return null;
            }

            return toDto(entity);
        } catch (Exception ex) {
            LOG.error("Error en obtenerInstitucion - id: " + id, ex);
            throw new ServiceException(ex);
        }
    }

    @Override
    public int guardarInstitucion(InstitucionDto dto, String usuario) throws ServiceException {
        LOG.info("Inicio guardarInstitucion - usuario: " + usuario + ", id: " + dto.getId());

        try {
            DoInstitucion entity;

            if (dto.getId() != null && dto.getId() > 0) {
                LOG.debug("Actualizando institución - id: " + dto.getId());

                entity = validarYObtenerInstitucion(dto.getId());
                entity.setFechaModificacion(LocalDateTime.now());
                entity.setUsuarioModificacion(usuario);
            } else {
                LOG.debug("Creando nueva institución - codigo: " + dto.getCodigo());

                entity = new DoInstitucion();
                entity.setFechaCreacion(LocalDateTime.now());
                entity.setUsuarioCreacion(usuario);
            }

            // validar que código no se repita
            DoInstitucion institucionExistente = repositorioInstitucion.findByCodigo(dto.getCodigo());
            if (institucionExistente != null 
                && !institucionExistente.getId().equals(entity.getId()) 
                && Constantes.ESTADO_ACTIVO.equals(institucionExistente.getEstado())) {
                String mensaje = "El código de institución ya se encuentra registrado.";
                LOG.error(mensaje);
                throw new InvalidParameterException(mensaje);
            }

            // mapping DTO → Entity
            entity.setCodigo(dto.getCodigo());
            entity.setNombre(dto.getNombre());
            entity.setEstado(Constantes.ESTADO_ACTIVO);

            repositorioInstitucion.save(entity);

            LOG.info("Institución guardada correctamente - id: " + entity.getId() + ", codigo: " + entity.getCodigo());
            return entity.getId();

        } catch (Exception ex) {
            LOG.error("Error en guardarInstitucion - usuario: " + usuario, ex);
            throw new ServiceException(ex);
        }
    }

    @Override
    public boolean eliminarInstitucion(int id, String usuario) throws ServiceException {
        LOG.info("Inicio eliminarInstitucion - id: " + id + ", usuario: " + usuario);

        try {
            DoInstitucion entity = repositorioInstitucion.findById(id);

            if (entity == null) {
                String mensaje = "No existe la institución con id: " + id + ".";
                LOG.error(mensaje);
                throw new InvalidParameterException(mensaje);
            }

            // Eliminación lógica
            entity.setEstado(Constantes.ESTADO_ELIMINADO);
            entity.setFechaEliminacion(LocalDateTime.now());
            entity.setUsuarioEliminacion(usuario);
            repositorioInstitucion.save(entity);

            LOG.info("Institución eliminada lógicamente - id: " + id);            
            return true;

        } catch (Exception ex) {
            LOG.error("Error en eliminarInstitucion - id: " + id + ", usuario: " + usuario, ex);
            throw new ServiceException(ex);
        }
    }

    private DoInstitucion validarYObtenerInstitucion(Integer id) {
        // validar existencia
        Optional<DoInstitucion> optional = repositorioInstitucion.findById(id);
        if (!optional.isPresent()) {
            String mensaje = "No existe registro de la institución con id: " + id + ".";
            LOG.error(mensaje);
            throw new InvalidParameterException(mensaje);
        }

        DoInstitucion entity = optional.get();                
        
        // validar estado eliminado
        if (entity.getEstado().equals(Constantes.ESTADO_ELIMINADO)) {
            String mensaje = "Intento de actualización de institución eliminada - id: " + id;
            LOG.error(mensaje);
            throw new InvalidParameterException(mensaje);
        }

        return optional.get();
    }

    // =========================
    // MAPPERS
    // =========================

    private InstitucionDto toDto(DoInstitucion entity) {
        if (entity == null) {
            return null;
        }
        
        InstitucionDto dto = new InstitucionDto();
        dto.setId(entity.getId());
        dto.setCodigo(entity.getCodigo());
        dto.setNombre(entity.getNombre());
        dto.setEstado(entity.getEstado());

        return dto;
    }

}