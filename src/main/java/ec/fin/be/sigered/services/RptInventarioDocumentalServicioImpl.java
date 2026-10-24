package ec.fin.be.sigered.services;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ec.fin.be.comunes.excepciones.ServiceException;
import ec.fin.be.sigered.UtilPropertiesArquetipo;
import ec.fin.be.sigered.dto.CellBackupDto;
import ec.fin.be.sigered.dto.DocumentoDto;
import ec.fin.be.sigered.dto.ResponseDto;
import ec.fin.be.sigered.dto.RowBackupDto;
import ec.fin.be.sigered.dto.SolicitudDto;
import ec.fin.be.sigered.dto.SolicitudFiltroDto;
import ec.fin.be.sigered.primary.entity.DoDocumento;
import ec.fin.be.sigered.primary.entity.DoGerencia;
import ec.fin.be.sigered.logger.LoggerSigered;
import ec.fin.be.sigered.primary.repository.DoDocumentoRepositorio;
import ec.fin.be.sigered.primary.repository.DoGerenciaRepositorio;
import ec.fin.be.sigered.utils.Constantes;
import ec.fin.be.sigered.utils.Utils;

@Service
public class RptInventarioDocumentalServicioImpl implements RptInventarioDocumentalServicio {

    private static final Logger LOG = LoggerSigered.getLogger(RptInventarioDocumentalServicioImpl.class);
    private static final UtilPropertiesArquetipo propFile = UtilPropertiesArquetipo.getIntances();

    @Autowired
    private DoSolicitudServicio solicitudServicio;

    @Autowired
    private DoGerenciaRepositorio gerenciaRepositorio;

    @Autowired
    private DoDocumentoRepositorio documentoRepositorio;
    
    @Override
    public ResponseDto generarReporteInventarioDocumentos(@RequestBody SolicitudFiltroDto filtro, String cedulaUsuario) throws ServiceException {
        try {

            // validar filtro de gerencia obligatorio
            if (filtro.getIdGerenciaResponsable() == null || filtro.getIdGerenciaResponsable() == 0) {
                String mensaje = "El filtro de gerencia es obligatorio para generar el reporte.";
                LOG.error(mensaje);
                throw new InvalidParameterException(mensaje);
            }

            List<SolicitudDto> solicitudes = solicitudServicio.buscarSolicitudes(filtro, 0, cedulaUsuario);

            byte[] archivoExcel = generarExcelInventario(solicitudes);

            String base64 = Base64.getEncoder().encodeToString(archivoExcel);

            Map<String, Object> data = new HashMap<>();
            data.put("nombreArchivo", "InventarioDocumentos.xlsx");
            data.put("extension", "xlsx");
            data.put("base64", base64);
            data.put("tamanoBytes", archivoExcel.length);

            ResponseDto response = Utils.devolverRespuesta(Constantes.CODIGOS_RESPUESTA.OK_00);
            response.setExito(true);
            response.setData(data);

            return response;

        } catch (Exception ex) {
            LOG.error("Error en generarReporteInventarioDocumentos", ex);
            throw new ServiceException(ex);
        }
    }

    private byte[] generarExcelInventario(List<SolicitudDto> solicitudes) throws IOException {

        // obtener parametros del archivo properties
        String serverPath = propFile.obtenerParametro(Constantes.RUTA_DOCUMENTOS);
        String templatesPath = propFile.obtenerParametro(Constantes.RUTA_PLANTILLAS_EXCEL);
        String templateFileName = propFile.obtenerParametro(Constantes.PLANTILLA_EXCEL_INV_DOCUMENTOS);
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

            CellStyle estiloDetalle = crearEstiloParaDetalles(workbook);

            insertarCabecera(solicitudes, sheet);

            // registros - desde fila 7 en Excel (índice 0-based)
            int filaDetalle = 6;

            // pie de pagina - desde fila 10 en Excel (índice 0-based)
            int filaPie = 9;

            List<RowBackupDto> pieBackup = respaldarPie(sheet, filaPie);

            List<CellRangeAddress> mergeBackup = respaldarMergesPie(sheet, filaPie);
            
            eliminarPieYMerges(sheet, filaPie);

            insertarDetalles(solicitudes, dateFormatter, sheet, estiloDetalle, filaDetalle);

            int newPieStart = reinsertarMergesPie(solicitudes, sheet, filaDetalle, filaPie, pieBackup);

            restaurarMergesPie(sheet, filaPie, mergeBackup, newPieStart);

            workbook.write(baos);
            return baos.toByteArray();
        }
    }

    private CellStyle crearEstiloParaDetalles(XSSFWorkbook workbook) {
        // crear estilo con bordes
        CellStyle estiloBorde = workbook.createCellStyle();
        estiloBorde.setBorderTop(BorderStyle.THIN);
        estiloBorde.setBorderBottom(BorderStyle.THIN);
        estiloBorde.setBorderLeft(BorderStyle.THIN);
        estiloBorde.setBorderRight(BorderStyle.THIN);
        estiloBorde.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloBorde.setWrapText(true);

        // fuente tamaño 8
        XSSFFont font8 = workbook.createFont();
        font8.setFontHeightInPoints((short) 8);
        font8.setFontName("Calibri");
        estiloBorde.setFont(font8);
        return estiloBorde;
    }

    private void insertarCabecera(List<SolicitudDto> solicitudes, XSSFSheet sheet) {
        // Cabecera
        if (!solicitudes.isEmpty()) {
            XSSFCell cellD2GerenciaPadre = sheet.getRow(1).getCell(3); // D2
            XSSFCell cellD3GerenciaHija = sheet.getRow(2).getCell(3); // D3

            // obtener gerencia asignada
            int idGerenciaAsignada = solicitudes.get(0).getIdGerenciaResponsable();
            DoGerencia gerenciaAsignada = gerenciaRepositorio.findById(idGerenciaAsignada);
            DoGerencia gerenciaPadre = gerenciaAsignada.getPadre();

            // si hay gerencia padre
            if (gerenciaPadre != null) {

                cellD2GerenciaPadre.setCellValue(gerenciaPadre.getNombre());
                cellD3GerenciaHija.setCellValue(gerenciaAsignada.getNombre());
            } else {
                cellD2GerenciaPadre.setCellValue(gerenciaAsignada.getNombre());                    
                cellD3GerenciaHija.setCellValue("-");
            }                
        }
    }

    private List<RowBackupDto> respaldarPie(XSSFSheet sheet, int filaPie) {
        // backup del pie
        List<RowBackupDto> pieBackup = new ArrayList<>();

        for (int i = filaPie; i <= sheet.getLastRowNum(); i++) {

            XSSFRow row = sheet.getRow(i);
            if (row == null) {
                continue;
            }

            pieBackup.add(crearRowBackup(row, i));
        }

        return pieBackup;
    }

    private RowBackupDto crearRowBackup(XSSFRow row, int rowIndex) {
        RowBackupDto rowBackup = new RowBackupDto();
        rowBackup.setRowIndex(rowIndex);
        rowBackup.setHeight(row.getHeightInPoints());

        List<CellBackupDto> cells = new ArrayList<>();

        for (int j = 0; j < row.getLastCellNum(); j++) {
            cells.add(crearCellBackup(row.getCell(j), j));
        }

        rowBackup.setCells(cells);
        return rowBackup;
    }

    private CellBackupDto crearCellBackup(XSSFCell cell, int columnIndex) {

        CellBackupDto cb = new CellBackupDto();
        cb.setCol(columnIndex);

        if (cell != null) {
            cb.setStyle(cell.getCellStyle());
            
            switch (cell.getCellType()) {
                case STRING:
                    cb.setValue(cell.getStringCellValue());
                    break;
                case NUMERIC:
                    cb.setValue(String.valueOf(cell.getNumericCellValue()));
                    break;
                case BOOLEAN:
                    cb.setValue(String.valueOf(cell.getBooleanCellValue()));
                    break;
                default:
                    cb.setValue("");
            }
            
        } else {
            cb.setValue("");
        }
        return cb;
    }

    private List<CellRangeAddress> respaldarMergesPie(XSSFSheet sheet, int filaPie) {
        // BACKUP MERGES DEL PIE
        List<CellRangeAddress> mergeBackup = new ArrayList<>();

        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress region = sheet.getMergedRegion(i);

            if (region.getFirstRow() >= filaPie) {
                mergeBackup.add(region);
            }
        }
        return mergeBackup;
    }

    private void eliminarPieYMerges(XSSFSheet sheet, int filaPie) {
        // ELIMINAR PIE + MERGES
        for (int i = sheet.getLastRowNum(); i >= filaPie; i--) {
            XSSFRow row = sheet.getRow(i);
            
            if (row != null) {
                sheet.removeRow(row);
            }
        }

        for (int i = sheet.getNumMergedRegions() - 1; i >= 0; i--) {
            CellRangeAddress region = sheet.getMergedRegion(i);
            if (region.getFirstRow() >= filaPie) {
                sheet.removeMergedRegion(i);
            }
        }
    }

    private void insertarDetalles(List<SolicitudDto> solicitudes, DateTimeFormatter dateFormatter, XSSFSheet sheet,
            CellStyle estiloBorde, int filaDetalle) {
                
        // insertar detalles
        for (int i = 0; i < solicitudes.size(); i++) {

            SolicitudDto s = solicitudes.get(i);
            XSSFRow row = sheet.createRow(filaDetalle + i);

            // crear 13 celdas con estilo en la fila
            for (int j = 0; j < 13; j++) {
                XSSFCell cell = row.createCell(j);
                cell.setCellStyle(estiloBorde);
            }

            row.getCell(0).setCellValue("GESTIÓN EXTERNA");
            row.getCell(1).setCellValue(s.getNombreInstitucion() != null ? s.getNombreInstitucion() : "");
            row.getCell(2).setCellValue(s.getFuncionarioReceptor() != null ? s.getFuncionarioReceptor() : "");
            row.getCell(3).setCellValue(s.getId());
            row.getCell(4).setCellValue(s.getTituloDocumento() != null ? s.getTituloDocumento() : "");

            StringBuilder documentosRespuesta = obtenerCadenaDocumentosRespuesta(s);

            row.getCell(5).setCellValue(documentosRespuesta.toString());
            row.getCell(6).setCellValue(s.getFechaIngreso() != null ? s.getFechaIngreso().format(dateFormatter) : "");
            row.getCell(7).setCellValue(s.getFechaMaximaAtencion() != null ? s.getFechaMaximaAtencion().format(dateFormatter) : "");
                        
            StringBuilder tamanos = obtenerCadenaTamanoDocumentos(s);

            row.getCell(8).setCellValue(tamanos.toString());
            row.getCell(9).setCellValue(s.getDestinoFinal() != null ? s.getDestinoFinal() : "");
            row.getCell(10).setCellValue("DIGITAL");

            StringBuilder rutas = obtenerRutasDocumentos(s);

            row.getCell(11).setCellValue(rutas.toString());

        }
    }

    private StringBuilder obtenerRutasDocumentos(SolicitudDto s) {
        // rutas de los documentos
        StringBuilder rutas = new StringBuilder();
        for (DocumentoDto d : s.getDocumentos()) {
            rutas.append(d.getRuta()).append(d.getNombreOriginal()).append("\n");
        }
        return rutas;
    }

    private StringBuilder obtenerCadenaDocumentosRespuesta(SolicitudDto s) {
        // obtener avances (documentos de respuesta)
        StringBuilder documentosRespuesta = new StringBuilder();
        List<DoDocumento> avances = documentoRepositorio.findBySolicitudIdAndTipoAndEstado(s.getId(), Constantes.TIPO_DOCUMENTO_AVANCE, Constantes.ESTADO_ACTIVO);

        for (DoDocumento avance : avances) {
            documentosRespuesta.append(avance.getNombreOriginal()).append("\n");
        }
        return documentosRespuesta;
    }

    private StringBuilder obtenerCadenaTamanoDocumentos(SolicitudDto s) {
        // tamaño de los documentos
        StringBuilder tamanos = new StringBuilder();
        List<DocumentoDto> documentos = s.getDocumentos();

        for (DocumentoDto d : documentos) {
            String tamanoMB = String.format("%.2f", d.getTamanoBytes() / 1024.0 / 1024.0);
            tamanos.append(tamanoMB).append(" MB\n");               
        }

        return tamanos;
    }

    private int reinsertarMergesPie(List<SolicitudDto> solicitudes, XSSFSheet sheet, int filaDetalle, int filaPie,
            List<RowBackupDto> pieBackup) {

        // REINSERTAR PIE
        int newPieStart = filaDetalle + solicitudes.size();
        sheet.createRow(newPieStart);
        newPieStart++;

        for (RowBackupDto rb : pieBackup) {

            int newRowIdx = newPieStart + (rb.getRowIndex() - filaPie);

            XSSFRow row = sheet.createRow(newRowIdx);
            row.setHeightInPoints(rb.getHeight());

            for (CellBackupDto cb : rb.getCells()) {

                XSSFCell cell = row.createCell(cb.getCol());

                if (cb.getValue() != null) {
                    cell.setCellValue(cb.getValue());
                }

                if (cb.getStyle() != null) {
                    cell.setCellStyle(cb.getStyle());
                }
            }
        }

        return newPieStart;
    }

    private void restaurarMergesPie(XSSFSheet sheet, int filaPie, List<CellRangeAddress> mergeBackup, int newPieStart) {
        // RESTAURAR MERGES DEL PIE
        int offset = newPieStart - filaPie;

        for (CellRangeAddress region : mergeBackup) {
            CellRangeAddress newRegion = new CellRangeAddress(
                region.getFirstRow() + offset,
                region.getLastRow() + offset,
                region.getFirstColumn(),
                region.getLastColumn()
            );

            sheet.addMergedRegion(newRegion);
        }
    }

}
