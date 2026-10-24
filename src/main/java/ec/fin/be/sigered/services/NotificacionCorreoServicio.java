package ec.fin.be.sigered.services;

import ec.fin.be.sigered.dto.ResponseDto;

public interface NotificacionCorreoServicio {
    ResponseDto enviarCorreoNuevaSolicitud(String destinatario, String nroTramiteExt, String nroTramiteInt, String asunto, String fechaMaxAtencion);
    ResponseDto enviarNotificacionDocsAEliminar(String destinatario, String gerenciaResponsable, int cantidadDocumentosEliminar);
}
