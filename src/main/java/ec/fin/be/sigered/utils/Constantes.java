package ec.fin.be.sigered.utils;

public class Constantes {

    public static final String SYSTEM_USER = "SYSTEM";
    public static final String ESTADO_DEVUELTO = "DEVUELTO";
    public static final String ESTADO_ANULADO = "ANULADO";
    public static final String ESTADO_RECHAZADO = "RECHAZADO";
    public static final String ESTADO_POR_ANULAR = "ANULAR";
    public static final String ESTADO_ENVIADO = "ENVIADO";
    public static final String FORMAT_DATE = "yyyy-MM-dd";
    public static final String EXIT = "200";
    public static final String ERROR = "500";
    public static final String FORMAT_DATE_JSON = "dd/MM/yyyy HH:mm:ss";
    public static final String FORMAT_DATE_TIME = "yyyy/MM/dd HH:mm:ss";
    public static final String FORMAT_DATE_JSON_CORTA = " dd/MM/yyyy";
    public static final String DOUBLE_PATH= "//";

    public static final String CODIGO_ERROR="10";
    public static final String CODIGO_EXITO="00";
    public static final String RESPONSE_SOAP_NOTIFICACION_GENERICA  = "WsNotificaCorreoGenericoRemitenteResponse";

    //keycloak
    public static final String KEYCLOAK = "keycloack";
    public static final String KEYCLOAK_CLIENT = "keycloackClient";
    public static final String KEYCLOAK_REALM = "keycloackRealm";
    public static final String KEYCLOAK_SECRET = "keycloackSecret";
    public static final String KEYCLOAK_SSL = "keycloackSSL";
    public static final String WSDL_SISTEMA = "WSDL.CodSistema";
    public static final String ENLACE_SISTEMA = "EnlaceSistema";
    public static final String CORREO_SISTEMA = "CorreoSistema";
    public static final String ESTADO_VIGENTE = "Vigente";

    //Codigos c_tabla catalogos cobis
    public static final Integer CODIGO_ZONAL = 7;
    public static final Integer CODIGO_ETNIAS = 1163;
    public static final Integer CODIGO_NIVEL_ESTUDIO = 77;
    public static final Integer CODIGO_SERVICIO_BASICO = 1337;
    public static final Integer CODIGO_PRODUCTOS = 259;

    //Estados de borrado lógico 
    public static final String ESTADO_ACTIVO = "A";
    public static final String ESTADO_ELIMINADO = "I";
    public static final String ESTADO_DESHABILITADO = "I";
    public static final String ESTADO_SOL_EN_PROCESO = "P";
    public static final String ESTADO_SOL_CERRADA = "C";
    
    public static final String CODIGO_PRINCIPAL = "S";
    public static final String ESTADO_POSTULADO = "P";
    public static final String ESTADO_CANCELADO = "C";
    public static final String ESTADO_INGRESADO = "I";
    public static final String ESTADO_ADJUDICADO = "A";
    public static final String ESTADO_ERROR = "E";
    
    public static final String APLICACIONPDFBASE64= "data:application/pdf;base64,";
    public static final String FORMATOPDF= "pdf";

    public static final String REPORTE_RUTA = "aplicacion.dir.report";
    public static final String DATASOURCE = "mysql.datasource.jndi-name";
    public static final String RUTA_DOCUMENTOS = "directorio.documentos";
    public static final String RUTA_PLANTILLAS_EXCEL = "documentos.plantillasExcel";
    public static final String PLANTILLA_EXCEL_INV_DOCUMENTOS = "plantillasExcel.InventarioDocumentos";
    public static final String PLANTILLA_EXCEL_RESOLUCIONES = "PlantillaInventarioDocumentos_RESOL_ADM.xlsx";
    
    public static final String DOCUMENTS_ROOT_FOLDER = "GEX";
    public static final String DOCUMENTOS_MAX_SIZE_KB = "documentos.fileMaxSizeKb";
    public static final String DOCUMENTOS_LOTE_MAX_SIZE_KB = "documentos.loteMaxSizeKb";
    public static final String DOCUMENTOS_MAX_QTY = "documentos.fileMaxQty";
    public static final String SEPARADOR_ARCHIVOS = "separador.archivos";
    public static final String RUTA_SERVICIO_SOAP="servicio.soap.cfn";
    

    public static final String CODIGO_EXITO_WS="0000";
    public static final String CODIGO_PAGADO_WS="0053";
    public static final String CODIGO_ERROR_WS="1001";
    public static final String SEGURIDAD_ENDPOINT="seguridad.endpoint.login";
    public static final String ZONA_GMT="GMT+0:00";
    public static final String PIE_SOAP="</soapenv:Body> </soapenv:Envelope>";
    public static final String TRANSACCION_SOAP="<TipoTransaccion>REG</TipoTransaccion>";
    public static final String SUBREPORTDIR="SUBREPORT_DIR";
    
    public static final String PREFIJO_SECUENCIA="prefijo.secuencia.remate";
    public static final String PADS_SECUENCIA= "pad.secuencia.remate";

    public static final String TIPO_DOCUMENTO_SOLICITUD="S";
    public static final String TIPO_DOCUMENTO_AVANCE="A";
    public static final String TIPO_AVANCE_RESOLUCION = "R";
    public static final String TIPO_AVANCE_PRORROGA = "P";

    public static final String URL_NOTIFICACION="url.notificaciones";

    public static final String WORKER_EXECUTION_TIME="workerExecutionTime";
    public static final String MAX_ANIOS_CONSERVACION="documentos.maxAniosConservacion";
    public static final String DESTINO_FINAL_CONSERVACION="CONSERVACIÓN";
    public static final String DESTINO_FINAL_ELIMINACION="ELIMINACIÓN";
    public static final String NOTIF_CONSERVACION_DOCS = "CONSERVACION_DOCS";

    public static final String LOGGER_IMPLEMENTACION="logger.implementacion";

    public static final int PAGE_SIZE = 200;
    public static final String TABLAS_AREAS="AREA_RESOLUCIONES";
    public static final String DESCRIPCION_AREAS="AREA DE RESOLUCIONES ADMINISTRATIVA";
    public static final String HORA_INICIO=" 00:00";
    public static final String HORA_FINAL=" 23:59";
    public static final String SUBDIRECTORIO="RESOLUCIONES_ADM";

    private Constantes() {
    }

    public enum CODIGOS_RESPUESTA {
        OK_00,
        ER_NO_HAY_RELACION_00001000,
        ER_ZONA_NO_PERMITIDA_00001001,
        ER_ERROR,
        ER_REGISTRO_DUPLICADO,
        ER_ELIMINAR,
        ER_EXISTS,
        ER_JSON
    }

    public enum DATOS_FTP {
        SFTP_SERVER,
        SFTP_USER,
        SFTP_PASSWORD,
        SFTP_RUTA
    }

}
