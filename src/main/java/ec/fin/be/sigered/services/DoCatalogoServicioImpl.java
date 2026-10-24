/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.fin.be.sigered.services;

import ec.fin.be.comunes.excepciones.ServiceException;
import ec.fin.be.sigered.dto.CatalogoDto;
import ec.fin.be.sigered.dto.ResponseDto;
import ec.fin.be.sigered.primary.entity.DoCatalogo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ec.fin.be.sigered.primary.entity.DoCatalogoId;
import ec.fin.be.sigered.primary.entity.DoTabla;
import ec.fin.be.sigered.logger.LoggerSigered;
import ec.fin.be.sigered.utils.Constantes;
import java.util.List;
import java.util.stream.Collectors;
import ec.fin.be.sigered.primary.repository.DoCatalogoRepositorio;
import ec.fin.be.sigered.utils.Utils;
import org.apache.log4j.Logger;
/**
 *
 * @author rmoran
 */
@Service
public class DoCatalogoServicioImpl implements DoCatalogoServicio {
     private static final Logger LOG = LoggerSigered.getLogger(DoCatalogoServicioImpl.class);
    @Autowired
    DoCatalogoRepositorio repositorioCatalogo;
    @Override
    public ResponseDto guardaCatalogo(CatalogoDto catalogoDto) throws ServiceException {
        ResponseDto response = Utils.devolverRespuesta(Constantes.CODIGOS_RESPUESTA.ER_ERROR);
        try{
            DoCatalogo catalogo;
            DoCatalogoId id = new DoCatalogoId();
            id.setTabla(Constantes.TABLAS_AREAS);
            id.setCodigo(catalogoDto.getpCodigo());
            DoCatalogo doCatalogo= new DoCatalogo();
            doCatalogo.setValor(catalogoDto.getpValor());
            doCatalogo.setEstado(catalogoDto.getpEstado());
            doCatalogo.setId(id);
            catalogo = repositorioCatalogo.save(doCatalogo);
             response = Utils.devolverRespuesta(Constantes.CODIGOS_RESPUESTA.OK_00);
            
        }catch(Exception ex){
             LOG.error("[ERROR AL GUARDAR CATALOGO]"+ex);
           response.setMensajeError(ex.getMessage());   
                }
        return response;
    }

    @Override
    public CatalogoDto consultaCatalogo(String codigo) {
        DoCatalogoId id= new DoCatalogoId();
        id.setTabla(Constantes.TABLAS_AREAS);
        id.setCodigo(codigo);
         DoCatalogo catalogo= repositorioCatalogo.findById(id);
        return entityToDto(catalogo);
    }

    @Override
    public List<CatalogoDto> consultaTabla(DoTabla tabla, String estado) {
        List<DoCatalogo> listadoCatalogo=  repositorioCatalogo.findByTablaAndEstado(tabla,estado);
        
        return listadoCatalogo.stream().map(this::entityToDto).collect(Collectors.toList());
    }
    
    private CatalogoDto entityToDto (DoCatalogo entity){
       
        if (entity == null) {
            return null;
        }
        
        CatalogoDto dto = new CatalogoDto();
        dto.setpCodigo(entity.getId().getCodigo());
        dto.setpCodigoPadre(entity.getCodigoPadre());
        dto.setpEstado(entity.getEstado());
        dto.setpTabla(entity.getId().getTabla());
        dto.setpValor(entity.getValor());
        
        return dto;
    }

    @Override
    public List<CatalogoDto> listarCatalogos(DoTabla tabla) {
        List<DoCatalogo> listadoCatalogo=  repositorioCatalogo.findByTabla(tabla);
        return listadoCatalogo.stream().map(this::entityToDto).collect(Collectors.toList());
    }

   
    
    
}
