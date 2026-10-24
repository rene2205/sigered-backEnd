package ec.fin.be.sigered.services;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ec.fin.be.autenticacion.ldap.cliente.ClienteSeguridades;
import ec.fin.be.autenticacion.ldap.ws.DtoUsuarioXSistema;
import ec.fin.be.comunes.excepciones.ServiceException;
import ec.fin.be.sigered.BeanConfig;
import ec.fin.be.sigered.UtilPropertiesArquetipo;
import ec.fin.be.sigered.dto.DelegadoDto;
import ec.fin.be.sigered.dto.SolicitudDto;
import ec.fin.be.sigered.primary.entity.DoDelegado;
import ec.fin.be.sigered.primary.entity.DoNotificacionEnviada;
import ec.fin.be.sigered.logger.LoggerSigered;
import ec.fin.be.sigered.primary.repository.DoDelegadoRepositorio;
import ec.fin.be.sigered.primary.repository.DoNotificacionEnviadaRepositorio;
import ec.fin.be.sigered.utils.Constantes;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;

@Component
public class ConservacionDocsWorker {
    private static final Logger LOG = LoggerSigered.getLogger(ConservacionDocsWorker.class);
    private static final UtilPropertiesArquetipo propFile = UtilPropertiesArquetipo.getIntances();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private int threadId = System.identityHashCode(this);
    private ScheduledFuture<?> nextExecution;
    private volatile boolean active = true;

    @Autowired
    private DoSolicitudServicio doSolicitudServicio;
    @Autowired
    private DoDelegadoServicio doDelegadoServicio;
    @Autowired
    private DoDelegadoRepositorio doDelegadoRepositorio;
    @Autowired
    private NotificacionCorreoServicio notificacionCorreoServicio;
    @Autowired
    private DoNotificacionEnviadaRepositorio notificacionRepo;
    @Autowired
    private BeanConfig beanConfig;


    @PostConstruct
    public void start() {
        LOG.info("Creando ConservacionDocsWorker " + threadId);
        scheduleNextRun();
    }

    @PreDestroy
    public void stop() {
        LOG.info("Destruyendo ConservacionDocsWorker " + threadId);
        active = false;
        
        if (nextExecution != null) {
            nextExecution.cancel(true);
        }

        scheduler.shutdownNow();
    }

    private void scheduleNextRun() {
        if (!active) {
            return;
        }

        String horaConfig = propFile.obtenerParametro(Constantes.WORKER_EXECUTION_TIME);
        
        LocalTime horaEjecucion = LocalTime.parse(horaConfig);
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime proximaEjecucion = LocalDate.now().atTime(horaEjecucion);

        // si ya pasó la hora de hoy, programar para mañana
        if (!proximaEjecucion.isAfter(ahora)) {
            proximaEjecucion = proximaEjecucion.plusDays(1);
        }

        // Calcular la diferencia exacta en milisegundos
        long delayMillis = Duration.between(ahora, proximaEjecucion).toMillis();

        // Si por un margen de milisegundos el delay sigue siendo <= 0, forzar para mañana
        if (delayMillis <= 0) {
            proximaEjecucion = proximaEjecucion.plusDays(1);
            delayMillis = Duration.between(ahora, proximaEjecucion).toMillis();
        }

        LOG.info("Próxima ejecución de ConservacionDocsWorker " + threadId + " programada para: " + proximaEjecucion);

        nextExecution = scheduler.schedule(this::executeWorker, delayMillis, TimeUnit.MILLISECONDS);

    }

    private void executeWorker() {
        try {
            LOG.info("Ejecutando ConservacionDocsWorker " + threadId);

            runLogic();

        } catch (Exception e) {
            LOG.error("Error ejecutando ConservacionDocsWorker " + threadId, e);
        } finally {
            LOG.info("Finalizando ConservacionDocsWorker " + threadId);

            if (active) {
                scheduleNextRun();
            }
        }
    }

    private void runLogic() throws ServiceException {
        List<SolicitudDto> solicitudes = doSolicitudServicio.obtenerSolicitudesAEliminar();

        // Agrupar por delegado responsable
        Map<Integer, List<SolicitudDto>> porDelegado = new HashMap<>();

        for (SolicitudDto sol : solicitudes) {
            porDelegado.computeIfAbsent(sol.getIdDelegadoResponsable(), k -> new ArrayList<>()).add(sol);
        }

        // 2. Enviar correos agrupados por delegado
        for (Map.Entry<Integer, List<SolicitudDto>> entry : porDelegado.entrySet()) {

            int idDelegado = entry.getKey();
            int cantidadDocs = entry.getValue().size();

            DelegadoDto delegado = doDelegadoServicio.obtenerDelegado(idDelegado);
            boolean yaNotificado = notificacionRepo
                .existsByTipoAndDestinatarioAndFechaNotificacion(
                    Constantes.NOTIF_CONSERVACION_DOCS, 
                    delegado.getCorreo(), 
                    LocalDate.now());

            // Si no se encuentra el correo del delegado o ya se notificó hoy, no enviar correo
            if (delegado.getCorreo() == null || yaNotificado) {

                if (delegado.getCorreo() == null) {
                    LOG.info("No se encuentra el correo del delegado: " + idDelegado);
                }

                if (yaNotificado) {
                    LOG.info("Ya se notificó hoy a: " + delegado.getCorreo());
                }
                
                continue;
            }

            // Enviar correo
            notificacionCorreoServicio.enviarNotificacionDocsAEliminar(delegado.getCorreo(), delegado.getNombreGerencia(), cantidadDocs);

            registrarNotificacionEnviada(cantidadDocs, delegado);

            enviarNotificacionesAdministradores(delegado.getNombreGerencia(), cantidadDocs, delegado.getCorreo());
        }
    }

    private void enviarNotificacionesAdministradores(String gerenciaResponsable, int cantidadDocs, String correoDelegado) throws ServiceException {
        List<String> correosAdmin = obtenerCorreosAdministradores();

        for (String correoAdmin : correosAdmin) {

            if (correoDelegado != null && correoAdmin != null && correoAdmin.equalsIgnoreCase(correoDelegado)) {
                LOG.info("Administrador ya notificado como delegado: " + correoAdmin + " para la gerencia: " + gerenciaResponsable);
                continue;
            }
            
            notificacionCorreoServicio.enviarNotificacionDocsAEliminar(correoAdmin, gerenciaResponsable, cantidadDocs);
        }
    }

    private List<String> obtenerCorreosAdministradores() throws ServiceException {
        ClienteSeguridades seguridades = beanConfig.clienteSeguridades();
        int idSistema = Integer.parseInt(propFile.obtenerParametro(Constantes.WSDL_SISTEMA));
        String urlWSSeguridad = propFile.obtenerParametro(Constantes.SEGURIDAD_ENDPOINT);

        List<DtoUsuarioXSistema> usuarios = seguridades.usuarioXSistema(idSistema, "Administrador", urlWSSeguridad);

        if (usuarios == null || usuarios.isEmpty()) {
            LOG.warn("No se encontraron administradores");
            return new ArrayList<>();
        }

        List<String> correos = new ArrayList<>();

        for (DtoUsuarioXSistema usuario : usuarios) {
            agregarCorreoAdministrador(correos, usuario);
        }

        LOG.info("Correos de administradores: " + correos);

        return correos;
    }

    private void agregarCorreoAdministrador(List<String> correos, DtoUsuarioXSistema usuario) {
        // Solo administradores activos
        if (usuario == null || usuario.getEstado() == null || usuario.getEstado() != 0) {
            return;
        }

        DoDelegado delegado = doDelegadoRepositorio.findByCedula(usuario.getIdentificacion());

        if (delegado != null 
            && "A".equals(delegado.getEstado())
            && delegado.getCorreo() != null 
            && !delegado.getCorreo().trim().isEmpty()) {

            correos.add(delegado.getCorreo());
        }
    }

    private void registrarNotificacionEnviada(int cantidadDocs, DelegadoDto delegado) {
        // Guardar notificación enviada en BD
        DoNotificacionEnviada notificacion = new DoNotificacionEnviada();
        notificacion.setDestinatario(delegado.getCorreo());
        notificacion.setTipo(Constantes.NOTIF_CONSERVACION_DOCS);
        notificacion.setFechaNotificacion(LocalDate.now());
        notificacion.setFechaRegistro(LocalDateTime.now());
        notificacion.setNotas("Cantidad de documentos: " + cantidadDocs);

        notificacionRepo.save(notificacion);
    }

}