/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.fin.be.sigered.controller;

import ec.fin.be.comunes.excepciones.ServiceException;
import ec.fin.be.sigered.dto.CatalogoDto;
import ec.fin.be.sigered.dto.ResponseDto;
import ec.fin.be.sigered.primary.entity.DoCatalogo;
import ec.fin.be.sigered.primary.entity.DoTabla;
import ec.fin.be.sigered.logger.LoggerSigered;
import ec.fin.be.sigered.services.DoCatalogoServicio;
import ec.fin.be.sigered.utils.Constantes;
import ec.fin.be.sigered.utils.Utils;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author rmoran
 */
@RestController
@RequestMapping("/catalogo")
public class CatalogoController {
    private static final Logger LOG = LoggerSigered.getLogger(CatalogoController.class);
    
    @Autowired
    private DoCatalogoServicio servicioCatalogo;
    
    @PostMapping("/grabarCatalogo")
    public ResponseDto grabarCatalogo(@RequestBody CatalogoDto catalogoDto
                                     )throws ServiceException{
        LOG.info("grabarCatalogo "+ catalogoDto.getpCodigo());
        ResponseDto response = Utils.devolverRespuesta(Constantes.CODIGOS_RESPUESTA.ER_ERROR);
        
        response = servicioCatalogo.guardaCatalogo(catalogoDto);
        
        return response;
    }
    
    @GetMapping("/consultaCatalogo/{codigo}")
    public CatalogoDto consultaCatalogo(@PathVariable String codigo )throws ServiceException{
        LOG.info("consultaCatalogo");
        return servicioCatalogo.consultaCatalogo(codigo);
    }
    
    @GetMapping("/listarCatalogosEstado/{estado}")
    public List<CatalogoDto> listarCatalogosEstado(@PathVariable String estado )throws ServiceException{
        LOG.info("consultaCatalogo");
        DoTabla tabla= new DoTabla();
        tabla.setCodigo(Constantes.TABLAS_AREAS);
        tabla.setDescripcion(Constantes.DESCRIPCION_AREAS);
        return servicioCatalogo.consultaTabla(tabla,estado);
    }
    
    @GetMapping("/listarCatalogos")
    public List<CatalogoDto> listarCatalogos( )throws ServiceException{
        LOG.info("consultaCatalogo");
        DoTabla tabla= new DoTabla();
        tabla.setCodigo(Constantes.TABLAS_AREAS);
        tabla.setDescripcion(Constantes.DESCRIPCION_AREAS);
        return servicioCatalogo.listarCatalogos(tabla);
    }
    
}
