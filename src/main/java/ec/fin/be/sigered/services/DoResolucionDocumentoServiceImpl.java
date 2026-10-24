/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.fin.be.sigered.services;

import ec.fin.be.comunes.excepciones.ServiceException;
import ec.fin.be.sigered.UtilPropertiesArquetipo;
import ec.fin.be.sigered.dto.ResolucionDocumentoDto;
import ec.fin.be.sigered.dto.ResponseDto;
import ec.fin.be.sigered.primary.entity.DoResolucion;
import ec.fin.be.sigered.primary.entity.DoResolucionDocumento;
import ec.fin.be.sigered.logger.LoggerSigered;
import ec.fin.be.sigered.primary.repository.DoResolucionesDocumentoRepositorio;
import ec.fin.be.sigered.primary.repository.DoResolucionesRepositorio;
import ec.fin.be.sigered.utils.Constantes;
import ec.fin.be.sigered.utils.Utils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author rmoran
 */
@Service
public class DoResolucionDocumentoServiceImpl implements DoResolucionDocumentoService {
    private static final Logger LOG = LoggerSigered.getLogger(DoResolucionDocumentoServiceImpl.class);
    @Autowired
    DoResolucionesDocumentoRepositorio resolucionDocumentoRepositorio;
    
    @Autowired
    DoResolucionesRepositorio resolucionRespositorio;
    @Override
    public ResolucionDocumentoDto grabarDocumentoResolucion(ResolucionDocumentoDto resolucionDocumento, MultipartFile multipartFile) throws ServiceException {
        String rutaServidor = UtilPropertiesArquetipo.getIntances().obtenerParametro(Constantes.RUTA_DOCUMENTOS);
        String directorio = Constantes.SUBDIRECTORIO;
       
        
        DoResolucion doResolucion= resolucionRespositorio.getById(resolucionDocumento.getIdResolucion());
        DoResolucionDocumento doResolucionDocumento = new DoResolucionDocumento();
        
         String ruta = rutaServidor +
                                 UtilPropertiesArquetipo.getIntances().obtenerParametro(Constantes.SEPARADOR_ARCHIVOS)+ directorio+
                                 UtilPropertiesArquetipo.getIntances().obtenerParametro(Constantes.SEPARADOR_ARCHIVOS)+ doResolucion.getReId()+
                                 UtilPropertiesArquetipo.getIntances().obtenerParametro(Constantes.SEPARADOR_ARCHIVOS);
                 
        
        doResolucionDocumento.setRdId(resolucionDocumento.getIdDocumento());
        doResolucionDocumento.setRdResolucion(doResolucion);
        doResolucionDocumento.setRdNombreDocumento(resolucionDocumento.getNombreDocumento());
        doResolucionDocumento.setRdRuta(ruta);
        doResolucionDocumento.setRdTamanoBytes(Integer.valueOf(String.valueOf(multipartFile.getSize())));
        doResolucionDocumento.setRdExtension(multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf('.')));
        doResolucionDocumento.setRdEstado(Constantes.ESTADO_ACTIVO);
        doResolucionDocumento.setRdFechaRegistro(new Date());
        doResolucionDocumento.setRdUsuarioRegistro(resolucionDocumento.getUsuarioRegistraDocumento());
        
        doResolucionDocumento = resolucionDocumentoRepositorio.save(doResolucionDocumento);
        try{
            File filePath = new File(ruta);
                               if (!filePath.exists()) {
                                   if (filePath.mkdirs()) {
                                       LOG.info("Directorio " + ruta + " creado");
                                   } else {
                                       LOG.info("Error al crear directorio" + ruta);
                                   }
                               }
                            File file = new File(ruta +  multipartFile.getOriginalFilename());
                             multipartFile.transferTo(file);
        }catch(Exception ex){
            LOG.error("[ERROR AL GUARDAR DOCUMENTO RESOLUCION]"+ex);
        }
       
        return entityToDto(doResolucionDocumento);
    }

    @Override
    public List<ResolucionDocumentoDto> listarDocumento(int resolucion) throws ServiceException {
        
        DoResolucion doResolucion= resolucionRespositorio.getById(resolucion);
        List<DoResolucionDocumento> listadoDocumentos= resolucionDocumentoRepositorio.findByRdResolucion(doResolucion);
        return listadoDocumentos.stream().map(this::entityToDto).collect(Collectors.toList());
    }

   
    @Transactional
    @Override
    public boolean eliminarDocumentoResolucion(int id) throws ServiceException {
        try {
            DoResolucionDocumento documento = resolucionDocumentoRepositorio.findByRdId(id);
            resolucionDocumentoRepositorio.deleteByRdId(id);
            eliminarArchivo(documento.getRdRuta(), documento.getRdNombreDocumento());
            return true;
        } catch (IOException ex) {
            LOG.error("[error] "+ ex);
            return false;
        }
    }

    @Override
    public ResponseDto obtenerDocumentoString(int id) throws ServiceException {
        ResponseDto respuesta = Utils.devolverRespuesta(Constantes.CODIGOS_RESPUESTA.OK_00);
        DoResolucionDocumento documento = resolucionDocumentoRepositorio.findByRdId(id);
        try {
            Path path = Paths.get(documento.getRdRuta(), documento.getRdNombreDocumento());
            byte[] bytes = Files.readAllBytes(path);
            String strBase64 = Base64.getEncoder().encodeToString(bytes);
            Map<String, Object> data = new HashMap<>();
            data.put("fileName", documento.getRdNombreDocumento());
            data.put("ext", documento.getRdExtension());
            data.put("strBase64", strBase64);
            data.put("size", documento.getRdTamanoBytes());
            respuesta.setData(data);
        } catch (IOException ex) {
            LOG.error("[obtenerDocumentoString] "+ex);
            respuesta= Utils.devolverRespuesta(Constantes.CODIGOS_RESPUESTA.ER_ERROR);
        }
        return respuesta;        
    }
    
    
    
    private void eliminarArchivo(String ruta, String nombreArchivo) throws IOException {
        Path path = Paths.get(ruta, nombreArchivo);

        try {

            if (!Files.exists(path)) {
                LOG.info("No existe documento: " + path);
                return;
            }

            Files.delete(path);
            LOG.info("Documento eliminado" + path );
            
        } catch (IOException ex) {
            LOG.error("Error al eliminar el documento" + path, ex);
            throw ex;
        }
        
    }
    
    private ResolucionDocumentoDto entityToDto(DoResolucionDocumento entity){
        ResolucionDocumentoDto resolucionDocumentoDto= new ResolucionDocumentoDto();
        
        resolucionDocumentoDto.setIdDocumento(entity.getRdId());
        resolucionDocumentoDto.setIdResolucion(entity.getRdResolucion().getReId());
        resolucionDocumentoDto.setNombreDocumento(entity.getRdNombreDocumento());
        resolucionDocumentoDto.setRutaDocumento(entity.getRdRuta());
        resolucionDocumentoDto.setTamanoDocumento(entity.getRdTamanoBytes());
        resolucionDocumentoDto.setExtensionDocumento(entity.getRdExtension());
        resolucionDocumentoDto.setEstadoDocumento(entity.getRdEstado());
        resolucionDocumentoDto.setFechaRegistroDocumento(entity.getRdFechaRegistro());
        resolucionDocumentoDto.setUsuarioRegistraDocumento(entity.getRdUsuarioRegistro());
        
        return resolucionDocumentoDto;
    }
    
    
    
}
