package ec.fin.be.sigered.services;

import java.net.MalformedURLException;
import java.net.URL;


import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import ec.fin.be.sigered.UtilPropertiesArquetipo;
import ec.fin.be.sigered.dto.ResponseDto;
import ec.fin.be.sigered.logger.LoggerSigered;
import ec.fin.be.sigered.utils.Constantes;
import ec.fin.be.sigered.utils.Utils;


@Service
public class NotificacionCorreoServicioImpl implements NotificacionCorreoServicio {
    private static final Logger LOG = LoggerSigered.getLogger(NotificacionCorreoServicioImpl.class);

    @Override
    public ResponseDto enviarCorreoNuevaSolicitud(String destinatario, String nroTramiteExt, String nroTramiteInt, String asunto, String fechaMaxAtencion) {
        String requestNotificacion = Utils.getRequestNuevaSolicitud(destinatario, nroTramiteExt, nroTramiteInt, asunto, fechaMaxAtencion);
        String responseSoapNotificacion = Constantes.RESPONSE_SOAP_NOTIFICACION_GENERICA;

        LOG.info("Enviando correo de nueva solicitud a destinatario: " + destinatario);

        return enviaNotificacion(requestNotificacion, responseSoapNotificacion);                
    }

    @Override
    public ResponseDto enviarNotificacionDocsAEliminar(String destinatario, String gerenciaResponsable,  int cantidadDocumentosEliminar) {
        String requestNotificacion = Utils.getRequestDocsAEliminarPorPlazosConservacion(destinatario, gerenciaResponsable, cantidadDocumentosEliminar);
        String responseSoapNotificacion = Constantes.RESPONSE_SOAP_NOTIFICACION_GENERICA;

        LOG.info("Enviando correo de documentos a eliminar por plazos conservacion a destinatario: " + destinatario);

        return enviaNotificacion(requestNotificacion, responseSoapNotificacion);                
    }

    private ResponseDto enviaNotificacion(String requestSoapNotificacion, String responseSoapNotificacion){
        ResponseDto responseDto = new ResponseDto();
        URL urlWsdl = null;
            
        String respuestaNotificacion="";
        String response="";

        try {
                urlWsdl = new URL(UtilPropertiesArquetipo.getIntances().obtenerParametro(Constantes.URL_NOTIFICACION));
                respuestaNotificacion = Utils.procesarConsultaClienteSoap(requestSoapNotificacion, urlWsdl, "");
                
                response = Utils.parsearRespuestaNotificacionXml(respuestaNotificacion, responseSoapNotificacion);
                if (response.contains("OK")){
                    responseDto.setCodigoError(Constantes.CODIGO_EXITO);
                    responseDto.setExito(true);
                }
                
        } catch(MalformedURLException ex){
                LOG.error("Error en enviaNotificacion MalformedURLException", ex);
                responseDto.setCodigoError(Constantes.CODIGO_ERROR);
                responseDto.setExito(false);
        }

        return responseDto;
    }


}
