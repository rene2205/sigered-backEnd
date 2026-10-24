/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.fin.be.sigered.services;

import ec.fin.be.comunes.excepciones.ServiceException;
import ec.fin.be.sigered.UtilPropertiesArquetipo;
import ec.fin.be.sigered.dto.ResolucionDocumentoDto;

import ec.fin.be.sigered.dto.ResolucionDto;
import ec.fin.be.sigered.dto.ResolucionFiltroDto;
import ec.fin.be.sigered.dto.ResponseDto;
import ec.fin.be.sigered.dto.RowBackupDto;
import ec.fin.be.sigered.dto.SolicitudDto;
import ec.fin.be.sigered.primary.entity.DoResolucion;
import ec.fin.be.sigered.primary.entity.DoResolucionDocumento;
import ec.fin.be.sigered.logger.LoggerSigered;
import ec.fin.be.sigered.primary.entity.DoGerencia;
import ec.fin.be.sigered.primary.repository.DoResolucionesDocumentoRepositorio;
import ec.fin.be.sigered.primary.repository.DoResolucionesRepositorio;
import ec.fin.be.sigered.utils.Constantes;
import ec.fin.be.sigered.utils.Utils;
import java.io.File;
import java.security.InvalidParameterException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ec.fin.be.sigered.specification.DoResolucionSpecifications;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Sort;

/**
 *
 * @author rmoran
 */

@Service
public class DoResolucionImpl implements DoResolucionServicio{
     private static final Logger LOG = LoggerSigered.getLogger(DoResolucionImpl.class);
    @Autowired
    DoResolucionesRepositorio repositorioResoluciones;
    SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    @Autowired
    RptInventarioDocumentalServicio servicioReporte;
    
    @Autowired
    DoResolucionesDocumentoRepositorio documentoRepositorio;
    
    @Override
    public ResponseDto guardarResolucion(ResolucionDto resolucionDto,MultipartFile multipartFile ) throws ServiceException {
        ResponseDto response = Utils.devolverRespuesta(Constantes.CODIGOS_RESPUESTA.ER_ERROR);
        DoResolucion resolucion= new DoResolucion();
        String serverPath = UtilPropertiesArquetipo.getIntances().obtenerParametro(Constantes.RUTA_DOCUMENTOS);
        String directorioPath=Constantes.SUBDIRECTORIO;
        
      try{  
            int fileMaxSizeKb = Integer.parseInt(UtilPropertiesArquetipo.getIntances().obtenerParametro(Constantes.DOCUMENTOS_MAX_SIZE_KB));
            
            if (resolucionDto.getId()==0){

                if (multipartFile == null || multipartFile.isEmpty()) {
                        String mensaje = "Debe seleccionar un archivo.";
                        LOG.error(mensaje);
                        throw new InvalidParameterException(mensaje);
                }

                if (multipartFile.getSize() > fileMaxSizeKb * 1024) {
                        String mensaje = "El tamaño del archivo supera el tamaño permitido.";
                        LOG.error(mensaje);
                        throw new InvalidParameterException(mensaje);
                }
            }



            if (resolucionDto.getId()!=null && resolucionDto.getId()>0){

                resolucion = repositorioResoluciones.getById(resolucionDto.getId());
                if (resolucion==null){
                    throw new ServiceException("No existe resolucin a modificar");
                }
                resolucion.setReUsuarioActualiza(resolucionDto.getUsuarioActualiza());
                resolucion.setReFechaActualiza(LocalDateTime.now());
            }else{
                resolucion.setReUsuarioRegistro(resolucionDto.getUsuarioRegistro());
                resolucion.setReFechaRegistro(LocalDateTime.now());
            }
            resolucion.setReArea(resolucionDto.getArea());
            resolucion.setReResolucion(resolucionDto.getResolucion());
            resolucion.setReFechaResolucion(resolucionDto.getFechaResolucion());
            resolucion.setReEstadoActual(resolucionDto.getEstadoActual());
            resolucion.setReFechaFinVigencia(resolucionDto.getFechaFinVigencia());
            resolucion.setReTipoVigencia(resolucionDto.getTipoVigencia());
            resolucion.setReResolucionInsubsistente(resolucionDto.getResolucion_insubsistente());
            resolucion.setReObservacion(resolucionDto.getObservacion());
            resolucion.setReDescripcion(resolucionDto.getDescripcion());
            resolucion.setReSuscrito(resolucionDto.getSuscrito());
            resolucion.setReNombreSuscrito(resolucionDto.getNombreSuscrito());
            resolucion.setReCargoSuscrito(resolucionDto.getCargoSuscrito());
            resolucion.setReInsubsistente(resolucionDto.getInsubsistente());
            resolucion = repositorioResoluciones.save(resolucion);
            
            if (resolucionDto.getId()==0){
                    boolean existsDocument = documentoRepositorio.existsByRdResolucionAndRdNombreDocumento(resolucion ,multipartFile.getOriginalFilename());
                    if (!existsDocument){
                         String ruta = serverPath+
                                 "/"+ directorioPath+
                                 "/"+ resolucion.getReId()+
                                 "/"
                                 ;
                        LOG.info("[RUTA] "+ ruta);
                        DoResolucionDocumento roResolucionDocumento = new DoResolucionDocumento();
                        roResolucionDocumento.setRdId(0);

                        roResolucionDocumento.setRdEstado(Constantes.ESTADO_ACTIVO);
                        roResolucionDocumento.setRdResolucion(resolucion);
                        roResolucionDocumento.setRdRuta(ruta);
                        roResolucionDocumento.setRdNombreDocumento(multipartFile.getOriginalFilename());

                        roResolucionDocumento.setRdExtension(multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf('.')));
                        roResolucionDocumento.setRdTamanoBytes(Integer.valueOf(String.valueOf(multipartFile.getSize())));
                        roResolucionDocumento.setRdFechaRegistro(new Date());
                        roResolucionDocumento.setRdUsuarioRegistro(resolucionDto.getUsuarioRegistro());


                         LOG.info(""+ roResolucionDocumento.getRdId());
                         LOG.info(""+ roResolucionDocumento.getRdResolucion().getReId());
                         LOG.info(""+roResolucionDocumento.getRdNombreDocumento());
                         LOG.info(""+roResolucionDocumento.getRdEstado());
                         LOG.info(""+roResolucionDocumento.getRdExtension());
                         LOG.info(""+roResolucionDocumento.getRdRuta());
                         LOG.info(""+roResolucionDocumento.getRdExtension());
                         LOG.info(""+roResolucionDocumento.getRdFechaRegistro());

                         documentoRepositorio.save(roResolucionDocumento);
                        LOG.info("[GRABANDO DOCUMENTO DE LA resolucion] ");
                        LOG.info("[roResolucionDocumento] "+ roResolucionDocumento.getRdId());
                        File directorio = new File(ruta);
                           if (!directorio.exists()) {
                               if (directorio.mkdirs()) {
                                   LOG.info("Directorio " + ruta + " creado");
                               } else {
                                   LOG.info("Error al crear directorio" + ruta);
                               }
                           }
                        File file = new File(ruta +  multipartFile.getOriginalFilename());
                        multipartFile.transferTo(file);
                    }else{
                        throw new ServiceException("Documento ya fue ingresado");
                    }
            }
             
             response = Utils.devolverRespuesta(Constantes.CODIGOS_RESPUESTA.OK_00);
             response.setData(resolucionDto.getId());
             entityToDto(resolucion);
       }catch(Exception ex){
           LOG.error("[ERROR AL GUARDAR RESOLUCION]"+ex);
           response.setMensajeError(ex.getMessage());
       }
            
        return response;
    }
    
    @Override
    public ResolucionDto buscarResolucion(Integer id) throws ServiceException {
        DoResolucion resolucion;
        resolucion = repositorioResoluciones.findByReId(id);
        return entityToDto(resolucion);
    }

    @Override
    public List<ResolucionDto> listarResolucionesByFechaRegistro(ResolucionFiltroDto filtro) throws ServiceException {
        List <DoResolucion> listadoResolucion = repositorioResoluciones.findByReFechaRegistroBetween(filtro.getFechaInicial(), filtro.getFechaFinal());
        return listadoResolucion.stream().map(this::entityToDto).collect(Collectors.toList());
    }

    @Override
    public List<ResolucionDto> listarResolucionesByFechaResolucion(ResolucionFiltroDto filtro) throws ServiceException {
         List <DoResolucion> listado = repositorioResoluciones.findByReFechaResolucionBetween(filtro.getFechaInicial(), filtro.getFechaFinal());
        return listado.stream().map(this::entityToDto).collect(Collectors.toList());
    }
    
    private Date stringToDate(String fecha){
        
         try {
             return formato.parse(fecha);
         } catch (ParseException ex) {
             java.util.logging.Logger.getLogger(DoResolucionImpl.class.getName()).log(Level.SEVERE, null, ex);
         }
         
         return null;
    }
    
        private ResolucionDto entityToDto(DoResolucion entity){
        ResolucionDto resolucionDto= new ResolucionDto();
        
        resolucionDto.setId(entity.getReId());
        resolucionDto.setArea(entity.getReArea());
        resolucionDto.setResolucion(entity.getReResolucion());
        resolucionDto.setFechaResolucion(entity.getReFechaResolucion());
        resolucionDto.setEstadoActual(entity.getReEstadoActual());
        resolucionDto.setTipoVigencia(entity.getReTipoVigencia());
        resolucionDto.setResolucion_insubsistente(entity.getReResolucionInsubsistente());
        resolucionDto.setObservacion(entity.getReObservacion());
        resolucionDto.setFechaRegistro(entity.getReFechaRegistro());
        resolucionDto.setUsuarioRegistro(entity.getReUsuarioRegistro());
        resolucionDto.setFechaActualiza(entity.getReFechaActualiza());
        resolucionDto.setUsuarioActualiza(entity.getReUsuarioActualiza());
        resolucionDto.setFechaFinVigencia(entity.getReFechaFinVigencia());
        resolucionDto.setSuscrito(entity.getReSuscrito());
        resolucionDto.setNombreSuscrito(entity.getReNombreSuscrito());
        resolucionDto.setCargoSuscrito(entity.getReCargoSuscrito());
        resolucionDto.setInsubsistente(entity.getReInsubsistente());
        resolucionDto.setDescripcion(entity.getReDescripcion());
        if (entity.getDoResolucionDocumentoList()!=null && !entity.getDoResolucionDocumentoList().isEmpty()){
            List<ResolucionDocumentoDto> documentosDto = new ArrayList();
             for ( DoResolucionDocumento  documento : entity.getDoResolucionDocumentoList()){
                 ResolucionDocumentoDto documentoDto= new ResolucionDocumentoDto();
                 documentoDto.setIdDocumento(documento.getRdId());
                 documentoDto.setIdResolucion(documento.getRdResolucion().getReId());
                 documentoDto.setNombreDocumento(documento.getRdNombreDocumento());
                 documentoDto.setRutaDocumento(documento.getRdRuta());
                 documentoDto.setTamanoDocumento(documento.getRdTamanoBytes());
                 documentoDto.setExtensionDocumento(documento.getRdExtension());
                 documentoDto.setEstadoDocumento(documento.getRdEstado());
                 documentoDto.setFechaRegistroDocumento(documento.getRdFechaRegistro());
                 documentoDto.setUsuarioRegistraDocumento(documento.getRdUsuarioRegistro());
                 documentosDto.add(documentoDto);
             }
             resolucionDto.setDocumentos(documentosDto);
            
        }
        
        return resolucionDto;
    }

    @Override
    public boolean existeResolucion(String resolucion) {
        return repositorioResoluciones.existsByReResolucion(resolucion);
    }

    @Override
    public List<ResolucionDto> buscarResolucionFiltro(ResolucionFiltroDto filtro) throws ServiceException {
        List<DoResolucion> listadoResolucionesAdm;
        
        Specification<DoResolucion> specificationResoluciones = DoResolucionSpecifications.buscarFiltro(filtro);
        listadoResolucionesAdm = repositorioResoluciones.findAll(specificationResoluciones,Sort.by(Sort.Direction.DESC, "reFechaResolucion"));
        
        return listadoResolucionesAdm.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResolucionDto> listarResolucionInsubsistente(String estado) {
        List<DoResolucion> listadoResolucionesAdm;
        listadoResolucionesAdm = repositorioResoluciones.findByReEstadoActual(estado);
        return listadoResolucionesAdm.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ResponseDto reporteResolucion(ResolucionFiltroDto filtro) throws ServiceException {
        try{
            ResponseDto response;
            List<DoResolucion> listadoResolucionesAdm;

            Specification<DoResolucion> specificationResoluciones = DoResolucionSpecifications.buscarFiltro(filtro);
            listadoResolucionesAdm = repositorioResoluciones.findAll(specificationResoluciones,Sort.by(Sort.Direction.DESC, "reFechaResolucion"));


            byte[] archivoExcel= generarReporteResolucion(listadoResolucionesAdm,filtro);

                String base64 = Base64.getEncoder().encodeToString(archivoExcel);

                Map<String, Object> data = new HashMap<>();
                data.put("nombreArchivo", "InventarioDocumentos_Resolucion_Adm.xlsx");
                data.put("extension", "xlsx");
                data.put("base64", base64);
                data.put("tamanoBytes", archivoExcel.length);

            response = Utils.devolverRespuesta(Constantes.CODIGOS_RESPUESTA.OK_00);
            response.setData(data);
            response.setExito(true);

            return response;
        } catch (Exception ex) {
            LOG.error("Error en generarReporteInventarioDocumentos", ex);
            throw new ServiceException(ex);
        }            
    }
    
    private byte[] generarReporteResolucion(List<DoResolucion> resoluciones, ResolucionFiltroDto filtro)throws IOException {
         String serverPath = UtilPropertiesArquetipo.getIntances().obtenerParametro(Constantes.RUTA_DOCUMENTOS);
        String templatesPath = UtilPropertiesArquetipo.getIntances().obtenerParametro(Constantes.RUTA_PLANTILLAS_EXCEL);
        String templateFileName = Constantes.PLANTILLA_EXCEL_RESOLUCIONES;
        String rutaPlantilla = serverPath + "/" + templatesPath + "/" + templateFileName;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // validar que el archivo de plantilla exista en el servidor
        File file = new File(rutaPlantilla);
        if (!file.exists()) {
            String mensaje = "El archivo de plantilla no existe en el servidor. " + rutaPlantilla;
            LOG.error(mensaje);
            throw new FileNotFoundException(mensaje);
        }
         try (FileInputStream is = new FileInputStream(rutaPlantilla);
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            XSSFSheet sheet = workbook.getSheetAt(0);
            
            CellStyle estiloDetalle = crearEstiloDetalles(workbook);

            insertHeader(resoluciones, sheet,filtro);
             
            int filaDetalle = 6;
            insertarDetalles(resoluciones, dateFormatter, sheet, estiloDetalle, filaDetalle);
            
            workbook.write(baos);
            return baos.toByteArray();
        }
        
    }
    
    private CellStyle crearEstiloDetalles(XSSFWorkbook workbook) {
        CellStyle estiloBorde = workbook.createCellStyle();
        estiloBorde.setBorderTop(BorderStyle.THIN);
        estiloBorde.setBorderBottom(BorderStyle.THIN);
        estiloBorde.setBorderLeft(BorderStyle.THIN);
        estiloBorde.setBorderRight(BorderStyle.THIN);
        estiloBorde.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloBorde.setWrapText(true);

        XSSFFont font8 = workbook.createFont();
        font8.setFontHeightInPoints((short) 8);
        font8.setFontName("Calibri");
        estiloBorde.setFont(font8);
        return estiloBorde;
    }
    
    private void insertHeader(List<DoResolucion> resoluciones, XSSFSheet sheet,ResolucionFiltroDto filtro) {
        // Cabecera
        if (!resoluciones.isEmpty()) {
            XSSFCell cellD2 = sheet.getRow(1).getCell(3); // D2
            XSSFCell cellD3 = sheet.getRow(2).getCell(3); // D3
            XSSFCell cellD4 = sheet.getRow(3).getCell(3); // D4

            
            cellD2.setCellValue(filtro.getArea());
            cellD3.setCellValue("-");
            cellD4.setCellValue("SIGERED - RESOLUCIONES ADMINISTRATIVAS");
                         
        }
    }
    
    
        private void insertarDetalles(List<DoResolucion> resoluciones , DateTimeFormatter dateFormatter, XSSFSheet sheet,
            CellStyle estiloBorde, int filaDetalle) {
                
        for (int i = 0; i < resoluciones.size(); i++) {

            DoResolucion resolucion = resoluciones.get(i);
            XSSFRow row = sheet.createRow(filaDetalle + i);

            for (int j = 0; j < 12; j++) {
                XSSFCell cell = row.createCell(j);
                cell.setCellStyle(estiloBorde);
            }

            row.getCell(0).setCellValue("RESOLUCION ADMINISTRATIVA");
            row.getCell(2).setCellValue(resolucion.getReNombreSuscrito()!= null ? resolucion.getReNombreSuscrito() : "");
            row.getCell(3).setCellValue(resolucion.getReResolucion());
            row.getCell(4).setCellValue(resolucion.getReDescripcion() != null ? resolucion.getReDescripcion() : "");

            row.getCell(5).setCellValue(resolucion.getReFechaResolucion().format(dateFormatter));
            row.getCell(6).setCellValue(resolucion.getReFechaFinVigencia() != null ? resolucion.getReFechaFinVigencia().format(dateFormatter) : "");
            
            StringBuilder tamanos = obtenerCadenaTamanoDocumentos(resolucion);

            row.getCell(7).setCellValue(tamanos.toString());
            row.getCell(9).setCellValue("DIGITAL");

            StringBuilder rutas = obtenerRutasDocumentos(resolucion);
                    
            row.getCell(10).setCellValue(rutas.toString());
            row.getCell(11).setCellValue(resolucion.getReObservacion());

        }
    }
        
        
         private StringBuilder obtenerCadenaTamanoDocumentos(DoResolucion resolucion) {
        // tamaño de los documentos
        StringBuilder tamanos = new StringBuilder();
        List<DoResolucionDocumento> listaDocumentos = resolucion.getDoResolucionDocumentoList();

        for (DoResolucionDocumento d : listaDocumentos) {
            String tamanoMB = String.format("%.2f", d.getRdTamanoBytes() / 1024.0 / 1024.0);
            tamanos.append(tamanoMB).append(" MB\n");               
        }

        return tamanos;
    }
         
         
        private StringBuilder obtenerRutasDocumentos(DoResolucion resolucion) {
            StringBuilder rutas = new StringBuilder();
            for (DoResolucionDocumento d : resolucion.getDoResolucionDocumentoList()) {
                rutas.append(d.getRdRuta()).append(d.getRdNombreDocumento()).append("\n");
            }
            return rutas;
    }

}
