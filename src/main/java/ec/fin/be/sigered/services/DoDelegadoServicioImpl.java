package ec.fin.be.sigered.services;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ec.fin.be.comunes.excepciones.ServiceException;
import ec.fin.be.sigered.dto.DelegadoDto;
import ec.fin.be.sigered.dto.DelegadoFiltroDto;
import ec.fin.be.sigered.primary.entity.DoDelegado;
import ec.fin.be.sigered.primary.entity.DoGerencia;
import ec.fin.be.sigered.logger.LoggerSigered;
import ec.fin.be.sigered.primary.repository.DoDelegadoRepositorio;
import ec.fin.be.sigered.primary.repository.DoGerenciaRepositorio;
import ec.fin.be.sigered.specification.DoDelegadoSpecification;
import ec.fin.be.sigered.utils.Constantes;

@Service
public class DoDelegadoServicioImpl implements DoDelegadoServicio {
    private static final Logger LOG = LoggerSigered.getLogger(DoDelegadoServicioImpl.class);
    
    @Autowired
    private DoDelegadoRepositorio repositorioDelegado;
    @Autowired
    private DoGerenciaRepositorio repositorioGerencia;

    @Override
    public List<DelegadoDto> obtenerDelegados(String estado) throws ServiceException {
        LOG.info("Inicio obtenerDelegados - estado: " + estado);

        try {
            List<DoDelegado> lista;

            if (estado != null && !estado.trim().isEmpty()) {
                lista = repositorioDelegado.findByEstado(estado);
            } else {
                lista = repositorioDelegado.findAll();
            }

            LOG.info("Total delegados obtenidas: " + lista.size());

            return lista.stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());

        } catch (Exception ex) {
            LOG.error("Error en obtenerDelegados", ex);
            throw new ServiceException(ex);
        }
    }

    @Override
    public List<DelegadoDto> buscarDelegados(DelegadoFiltroDto filtro) throws ServiceException {
        LOG.info("Inicio buscarDelegados");

        try {
            List<DoDelegado> lista = repositorioDelegado.findAll(DoDelegadoSpecification.buscar(filtro));

            LOG.info("Total delegados obtenidos: " + lista.size());

            return lista.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        } catch (Exception ex) {
            LOG.error("Error en buscarDelegados", ex);
            throw new ServiceException(ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public DelegadoDto obtenerDelegado(int id) throws ServiceException {
        LOG.info("Inicio obtenerDelegado - id: " + id);

        try {
            DoDelegado entity = repositorioDelegado.findById(id);
            if (entity == null) {
                LOG.warn("No se encontró delegado con id: " + id);
                return null;
            }

            return toDto(entity);
        } catch (Exception ex) {
            LOG.error("Error en obtenerDelegado - id: " + id, ex);
            throw new ServiceException(ex);
        }
    }

    @Override
    public int guardarDelegado(DelegadoDto dto, String usuario) throws ServiceException {
        LOG.info("Inicio guardarDelegado - usuario: " + usuario + ", id: " + dto.getId());

        try {
            DoDelegado entity;

            if (dto.getId() != null && dto.getId() > 0) {
                LOG.debug("Actualizando delegado - id: " + dto.getId());

                entity = validarYObtenerDelegado(dto.getId());
                entity.setFechaModificacion(LocalDateTime.now());
                entity.setUsuarioModificacion(usuario);
            } else {
                LOG.debug("Creando nueva delegado - codigo: " + dto.getCodigo());

                entity = new DoDelegado();
                entity.setFechaCreacion(LocalDateTime.now());
                entity.setUsuarioCreacion(usuario);
            }
            
            validarCedulaRepetida(dto, entity);
            validarGerenciaNoTomada(dto, entity);
            validarCorreoObligatorio(dto.getCorreo());

            // mapping DTO → Entity
            entity.setCodigo(dto.getCodigo());
            entity.setNombreFuncionario(dto.getNombreFuncionario());
            entity.setEstado(Constantes.ESTADO_ACTIVO);
            entity.setCedula(dto.getCedula());
            entity.setCorreo(dto.getCorreo());
            entity.setGerencia(validarYObtenerGerencia(dto.getGerenciaId()));
            entity = repositorioDelegado.save(entity);

            LOG.info("delegado guardado correctamente - id: " + entity.getId() + ", codigo: " + entity.getCodigo());            
            return entity.getId();

        } catch (Exception ex) {
            LOG.error("Error en guardarDelegado - usuario: " + usuario, ex);
            throw new ServiceException(ex);
        }
    }

    private void validarCedulaRepetida(DelegadoDto dto, DoDelegado entity) {
        
        if (!esCedulaEcuatoriana(dto.getCedula())) {
            String mensaje = "Número de identificación inválido.";
            LOG.error(mensaje);
            throw new InvalidParameterException(mensaje);
        }

        // validar que cédula no se repita        
        DoDelegado delegadoExistente = repositorioDelegado.findByCedula(dto.getCedula());

        if (delegadoExistente != null 
            && !delegadoExistente.getId().equals(entity.getId()) 
            && Constantes.ESTADO_ACTIVO.equals(delegadoExistente.getEstado())) {
            String mensaje = "El número de cédula ya se encuentra registrado.";
            LOG.error(mensaje);
            throw new InvalidParameterException(mensaje);
        }
    }

    private void validarGerenciaNoTomada(DelegadoDto dto, DoDelegado entity) {
        // validar que gerencia no esté tomada por otro delegado
        if (dto.getGerenciaId() != null && dto.getGerenciaId() > 0) {
            DoDelegado delegadoEnGerencia = repositorioDelegado.findByGerenciaId(dto.getGerenciaId());

            if (delegadoEnGerencia != null && !delegadoEnGerencia.getId().equals(entity.getId())) {
                String mensaje = "La gerencia ya se encuentra tomada por otro delegado.";
                LOG.error(mensaje);
                throw new InvalidParameterException(mensaje);
            }
        }
    }

    private void validarCorreoObligatorio(String correo) throws ServiceException {
        if (correo == null || correo.trim().isEmpty()) {
            throw new ServiceException("El correo electrónico es obligatorio.");
        }

        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)+$";

        if (!correo.trim().matches(regex)) {
            throw new ServiceException("El correo electrónico no tiene un formato válido.");
        }
    }

    @Override
    public boolean eliminarDelegado(int id, String usuario) throws ServiceException {
        LOG.info("Inicio eliminarDelegado - id: " + id + ", usuario: " + usuario);

        try {
            DoDelegado entity = repositorioDelegado.findById(id);

            if (entity == null) {
                String mensaje = "No existe delegado con id: " + id + ".";
                LOG.error(mensaje);
                throw new InvalidParameterException(mensaje);
            }

            // 👇 Eliminación lógica
            entity.setEstado(Constantes.ESTADO_ELIMINADO);
            entity.setFechaEliminacion(LocalDateTime.now());
            entity.setUsuarioEliminacion(usuario);
            repositorioDelegado.save(entity);

            LOG.info("Delegado eliminada lógicamente - id: " + id);
            return true;

        } catch (Exception ex) {
            LOG.error("Error en eliminarDelegado - id: " + id + ", usuario: " + usuario, ex);
            throw new ServiceException(ex);
        }
    }

    //#region validar cedula ecuatoriana
    /**
     * Valida la cedula ecuatoriana
     * @param cedula
     * @return
     */
    public static boolean esCedulaEcuatoriana(String cedula) {
        if (!esFormatoCedulaValido(cedula)) {
            return false;
        }

        int primerosDosDigitos = Integer.parseInt(cedula.substring(0, 2));

        if (!esRegionValida(primerosDosDigitos)) {
            return false;
        }

        int total = calcularTotalVerificacion(cedula);

        int cifraVerificadora = calcularDigitoVerificador(total);

        int ultimoDigito = Character.getNumericValue(cedula.charAt(9));

        return cifraVerificadora == ultimoDigito;
    }

    private static boolean esFormatoCedulaValido(String cedula) {
        return cedula != null
                && !cedula.trim().isEmpty()
                && cedula.length() == 10
                && cedula.matches("\\d+");
    }

    private static boolean esRegionValida(int primerosDosDigitos) {
        return primerosDosDigitos <= 25 || primerosDosDigitos == 30;
    }

    private static int calcularTotalVerificacion(String cedula) {
        int total = 0;

        for (int i = 0; i < 9; i++) {
            int cifra = Character.getNumericValue(cedula.charAt(i));

            if (i % 2 == 0) {
                cifra *= 2;

                if (cifra > 9) {
                    cifra -= 9;
                }
            }

            total += cifra;
        }

        return total;
    }

    private static int calcularDigitoVerificador(int total) {
        int resultado = total % 10;

        return resultado > 0 ? 10 - resultado : 0;
    }
    //#endregion

    //#region validaciones
    private DoDelegado validarYObtenerDelegado(Integer id) {
        // validar existencia
        Optional<DoDelegado> optional = repositorioDelegado.findById(id);
        if (!optional.isPresent()) {
            String mensaje = "Intento de actualización de delegado inexistente - id: " + id;
            LOG.error(mensaje);
            throw new InvalidParameterException(mensaje);
        }

        DoDelegado entity = optional.get();                
        
        // validar estado eliminado
        if (entity.getEstado().equals(Constantes.ESTADO_ELIMINADO)) {
            String mensaje = "Intento de actualización de delegado eliminado - id: " + id;
            LOG.error(mensaje);
            throw new InvalidParameterException(mensaje);
        }

        return entity;
    }

    private DoGerencia validarYObtenerGerencia(Integer id) {
        if(id == null || id == 0){
            String mensaje = "Debe seleccionar una gerencia.";
            LOG.error(mensaje);
            throw new InvalidParameterException(mensaje);
        }

        Optional<DoGerencia> opGerencia = repositorioGerencia.findById(id);
        if (!opGerencia.isPresent()) {
            String mensaje = "No existe la gerencia con id: " + id + ".";
            LOG.error(mensaje);
            throw new InvalidParameterException(mensaje);
        }

        return opGerencia.get();
    }
    
    //#endregion

    // =========================
    // MAPPERS
    // =========================

    private DelegadoDto toDto(DoDelegado entity) {
        if (entity == null) {
             return null;
        }

        DelegadoDto dto = new DelegadoDto();
        dto.setId(entity.getId());
        dto.setCodigo(entity.getCodigo());
        dto.setNombreFuncionario(entity.getNombreFuncionario());
        dto.setEstado(entity.getEstado());
        dto.setCedula(entity.getCedula());
        dto.setCorreo(entity.getCorreo());

        DoGerencia gerencia = entity.getGerencia();
        if (gerencia != null) {
            dto.setGerenciaId(gerencia.getId());
            dto.setNombreGerencia(gerencia.getNombre());
        }

        return dto;
    }

}