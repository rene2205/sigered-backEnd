package ec.fin.be.sigered.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import ec.fin.be.comunes.utils.FechaUtil;
import ec.fin.be.sigered.UtilPropertiesArquetipo;
import ec.fin.be.sigered.dto.ResponseDto;
import ec.fin.be.sigered.logger.LoggerSigered;

import org.apache.log4j.Logger;
import ec.fin.be.sigered.utils.Constantes.CODIGOS_RESPUESTA;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class Utils {
    private static final Logger LOG = LoggerSigered.getLogger(Utils.class);

    private static final String HEADER_SOAP = "<soapenv:Header/>\n" + "<soapenv:Body>\n" ;
    private static final String CABECERA_SOAP_NOTIFICACION = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:not=\"http://notificaciones.org/\">\n" + HEADER_SOAP;
    private static final String PIE_SOAP_NOTIFICACION = "</soapenv:Body>\n" + "</soapenv:Envelope>";
    private static final String HTML_END_CELL_ROW = "</td></tr>";

    private static final UtilPropertiesArquetipo propFile = UtilPropertiesArquetipo.getIntances();

    private Utils() {
        super();
    }

    public static Date stringToDate(String dateValueAsStr, String dateFromat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFromat);
        try {
            return formatter.parse(dateValueAsStr);
        } catch (ParseException e) {//NOSONAR
            throw new RuntimeException("No se logro convertir a fecha el valor: " + dateValueAsStr);//NOSONAR
        }
    }

    public static Integer contarSinFinSemana(Date fechaIni, int dias) {
        int diasContadr = 1;
        for (int i = 0; i < dias; i++) {
            Date fechafor = FechaUtil.sumarRestarDiasFecha(fechaIni, i);
            Calendar cal = Calendar.getInstance();
            cal.setTime(fechafor);
            if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY
                    && cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
                diasContadr++;
            }
        }
        return diasContadr;
    }

    private static String devolverCodigoError(CODIGOS_RESPUESTA respuesta) {
        return respuesta.toString().substring(respuesta.toString().lastIndexOf('_') + 1);
    }

    private static String devolverMensajeError(CODIGOS_RESPUESTA respuesta) {
        if (respuesta.toString().chars().filter(ch -> ch == '_').count() > 1.0) {
            return respuesta.toString().substring(respuesta.toString().indexOf('_') + 1, respuesta.toString().lastIndexOf('_'));
        } else {
            return respuesta.toString().substring(0, respuesta.toString().lastIndexOf('_'));
        }
    }

    public static ResponseDto devolverRespuesta(CODIGOS_RESPUESTA respuesta) {
        ResponseDto resp = new ResponseDto();
        resp.setCodigoError(devolverCodigoError(respuesta));
        resp.setMensajeError(devolverMensajeError(respuesta));
        

        resp.setExito(respuesta == CODIGOS_RESPUESTA.OK_00);

        return resp;

    }
    
    public static boolean grabarStringArchivo(String directorio, String archivo, String fileName, int codigo){
        String base64Image = archivo.split(",")[1];
        crearDirectorio(directorio+UtilPropertiesArquetipo.getIntances().obtenerParametro(Constantes.SEPARADOR_ARCHIVOS),codigo);
        boolean exito;
        File file = new File(directorio+UtilPropertiesArquetipo.getIntances().obtenerParametro(Constantes.SEPARADOR_ARCHIVOS) +
                            codigo+UtilPropertiesArquetipo.getIntances().obtenerParametro(Constantes.SEPARADOR_ARCHIVOS) + 
                            fileName );
        try ( FileOutputStream fos = new FileOutputStream(file); ) {
          byte[] decoder = Base64.getDecoder().decode(base64Image);
          fos.write(decoder);
          exito=true;
        } catch (Exception e) {
          LOG.error("Error al grabar Archivo", e);
          exito=false;
        }
        
        return exito;
    }
    
    public static boolean  eliminarArchivo(String nombreArchivo){
        try {
            Path path = Paths.get(nombreArchivo);
            LOG.info("Eliminando Archivo: "+ nombreArchivo);
            Files.delete(path);
            return true;
        } catch (IOException ex) {
            LOG.error("Error al eliminar Archivo: "+ nombreArchivo, ex);
        }
         
         return false;
    }
    
    public static void crearDirectorio(String raiz, int codigo){
        File directorio = new File(raiz+codigo);
        if (!directorio.exists()) {
            if (directorio.mkdirs()) {
                LOG.info("Directorio creado");
            } else {
                LOG.info("Error al crear directorio");
            }
        }
        if (directorio.setReadable(true,false)){
            LOG.info("Permiso de Lectura ejecuta con Exito {0}" + directorio.getName());
        }
        if (directorio.setExecutable(true,false)){
            LOG.info("Permiso de Ejecución ejecuta con Exito {0}" + directorio.getName());
        }
        if (directorio.setWritable(true,false)){
            LOG.info("Permiso de Escritura ejecuta con Exito {0}" + directorio.getName());
        }
        
    }
    
    public static String obtenerStringArchivo(String nombreArchivo){
        String url="";
        try {
            
            File file= new File(nombreArchivo);
            byte[] fileContent = Files.readAllBytes(file.toPath());
            url = Base64.getEncoder().encodeToString(fileContent);
            
            return url;
        } catch (IOException ex) {
            LOG.error("Error al obtener Archivo: "+ nombreArchivo, ex);
        }
        return null;
    }

    public static String procesarConsultaClienteSoap(String request, URL url, String soapAccion){
        String response = "";

        try{
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
            connection.setRequestProperty("SOAPAction", soapAccion);
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = request.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { 
                StringBuilder stringBuffer;
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    stringBuffer = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        stringBuffer.append(inputLine);
                    }
                response = stringBuffer.toString();
            } 

        } catch (Exception ex) {
            LOG.error("Error en procesarConsultaClienteSoap", ex);
        } 

        return response;
    }

    public static String parsearRespuestaNotificacionXml(String response,String wsResponse) {
        String respuesta="";
        StringBuilder xmlStringBuilder = new StringBuilder();
        xmlStringBuilder.append(response);
        

        try (ByteArrayInputStream input = new ByteArrayInputStream(xmlStringBuilder.toString().getBytes(StandardCharsets.UTF_8));
        ){
            final Document doc = obtenerDocumento(input);
            if (doc==null){
                return respuesta;
            }
                doc.getDocumentElement().normalize();

                XPath xpathConTrans = XPathFactory.newInstance().newXPath();
                
                    XPathExpression xPathEstado = xpathConTrans.compile(Constantes.DOUBLE_PATH+wsResponse+"/return/estado");//NOSONAR
                    NodeList nodesEstado = (NodeList)xPathEstado.evaluate(doc, XPathConstants.NODESET);
                    
                    for (int i = 0; i < nodesEstado.getLength(); i++) {
                        respuesta = nodesEstado.item(i).getTextContent();
                    }
 
               
        } catch (UnsupportedEncodingException ex) {
            LOG.error("Error en parsearRespuestaNotificacionXml UnsupportedEncodingException", ex);
        } catch (IOException e){
            LOG.error("Error en parsearRespuestaNotificacionXml IOException", e);
        } catch (XPathExpressionException ex) {
            LOG.error("Error en parsearRespuestaNotificacionXml XPathExpressionException", ex);
        }
        
        return respuesta;
    }

    private static Document obtenerDocumento(ByteArrayInputStream input ){
        Document document = null;
        try {
            
            DocumentBuilderFactory documentBuilderFactory = newDocumentBuilderFactory();
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();

            document = builder.parse(input);

        } catch (Exception ex) {
             LOG.error("Error en obtenerDocumento", ex);
        }

        return document;
    }

    public static DocumentBuilderFactory newDocumentBuilderFactory() {

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();//NOSONAR
        documentBuilderFactory.setXIncludeAware(false);
        documentBuilderFactory.setExpandEntityReferences(false);
        
        try {
            documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl",true);
            documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities",false);
            documentBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities",false);

        } catch(ParserConfigurationException exp){
            LOG.error("Error en newDocumentBuilderFactory", exp);
        }

        return documentBuilderFactory;
    }

    public static String getRequestNuevaSolicitud(String destinatario, String nroTramiteExt, String nroTramiteInt, String asuntoSolicitud, String fechaMaxAtencion) {
        
        String idSistema = propFile.obtenerParametro(Constantes.WSDL_SISTEMA);
        String enlaceSistema = propFile.obtenerParametro(Constantes.ENLACE_SISTEMA);
        String correoRemitente = propFile.obtenerParametro(Constantes.CORREO_SISTEMA);
        String contenidoMensaje = construirMensajeNuevaSolicitud(nroTramiteExt, nroTramiteInt, fechaMaxAtencion, asuntoSolicitud, enlaceSistema);
        String asuntoCorreo = "[SIGERED] Nueva solicitud registrada - Trámite " + (nroTramiteExt.isEmpty() ? nroTramiteInt : nroTramiteExt);

        return CABECERA_SOAP_NOTIFICACION + 
               "      <not:WsNotificaCorreoGenericoRemitente>\n" +
               "      <id>"+ idSistema +"</id>\n" +
               "      <correoDestinatario>" + destinatario + "</correoDestinatario>\n" +
               "      <asunto>" + asuntoCorreo + "</asunto>\n" +
               "      <mensaje><![CDATA[" + contenidoMensaje + "]]></mensaje>\n" +
               "      <correoRemitente>" + correoRemitente + "</correoRemitente>\n" +
               "      </not:WsNotificaCorreoGenericoRemitente>\n" +
               PIE_SOAP_NOTIFICACION;
    }

    public static String getRequestDocsAEliminarPorPlazosConservacion(String destinatario, String gerenciaResponsable, int cantidadDocumentosEliminar) {

        String idSistema = propFile.obtenerParametro(Constantes.WSDL_SISTEMA);
        String enlaceSistema = propFile.obtenerParametro(Constantes.ENLACE_SISTEMA);
        String correoRemitente = propFile.obtenerParametro(Constantes.CORREO_SISTEMA);
        String contenidoMensaje = construirMensajeDocsAEliminarPorPlazosConservacion(gerenciaResponsable, cantidadDocumentosEliminar, enlaceSistema);
        String asuntoCorreo = "[SIGERED] Documentos a eliminar por plazos de conservación";

        return CABECERA_SOAP_NOTIFICACION + 
               "      <not:WsNotificaCorreoGenericoRemitente>\n" +
               "      <id>"+ idSistema +"</id>\n" +
               "      <correoDestinatario>" + destinatario + "</correoDestinatario>\n" +
               "      <asunto>" + asuntoCorreo + "</asunto>\n" +
               "      <mensaje><![CDATA[" + contenidoMensaje + "]]></mensaje>\n" +
               "      <correoRemitente>" + correoRemitente + "</correoRemitente>\n" +
               "      </not:WsNotificaCorreoGenericoRemitente>\n" +
               PIE_SOAP_NOTIFICACION;
    }

    private static String construirMensajeNuevaSolicitud(
        String nroTramiteExt,
        String nroTramiteInt,
        String fechaMaxAtencion,
        String asuntoSolicitud,
        String enlaceSistema) {

        return new StringBuilder()
                .append("<html>")
                .append("<body>")
                .append("<p>Se ha registrado una nueva solicitud en el sistema <b>SIGERED</b>. A continuación se presentan los detalles para su gestión:</p>")
                .append("<table border=\"0\" cellpadding=\"5\">")
                .append("<tr><td><b>Número de trámite externo:</b></td><td>")
                .append(nroTramiteExt)
                .append(HTML_END_CELL_ROW)
                .append("<tr><td><b>Número de trámite interno:</b></td><td>")
                .append(nroTramiteInt)
                .append(HTML_END_CELL_ROW)
                .append("<tr><td><b>Asunto de la solicitud:</b></td><td>")
                .append(asuntoSolicitud)
                .append(HTML_END_CELL_ROW)
                .append("<tr><td><b>Fecha máxima de atención:</b></td><td>")
                .append(fechaMaxAtencion)
                .append(HTML_END_CELL_ROW)
                .append("</table>")
                .append("<br/>")
                .append("<p>")
                .append("Puede gestionar el documento ingresando al siguiente enlace: ")
                .append("<a href=\"")
                .append(enlaceSistema)
                .append("\">")
                .append(enlaceSistema)
                .append("</a>")
                .append("</p>")
                .append("</body>")
                .append("</html>")
                .toString();
    }

    private static String construirMensajeDocsAEliminarPorPlazosConservacion(String gerenciaResponsable, int cantidadDocumentosEliminar, String enlaceSistema) {

        return new StringBuilder()
                .append("<html>")
                .append("<body>")
                .append("<p>Se han identificado ")
                .append("<b>")
                .append(cantidadDocumentosEliminar)
                .append("</b>" )
                .append(" documentos de la gerencia ")
                .append("<b>")
                .append(gerenciaResponsable)
                .append("</b>")
                .append(" que han cumplido su plazo de conservación y se encuentran pendientes de eliminación en el sistema <b>SIGERED</b>.</p>")
                .append("<p>")
                .append("Puede gestionar los documentos ingresando al siguiente enlace: ")
                .append("<a href=\"")
                .append(enlaceSistema)
                .append("\">")
                .append(enlaceSistema)
                .append("</a>")
                .append("</p>")
                .append("</body>")
                .append("</html>")
                .toString();
    }
}
