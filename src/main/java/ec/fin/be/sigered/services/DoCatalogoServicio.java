/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.fin.be.sigered.services;

import ec.fin.be.comunes.excepciones.ServiceException;
import ec.fin.be.sigered.dto.CatalogoDto;
import ec.fin.be.sigered.dto.ResponseDto;
import ec.fin.be.sigered.primary.entity.DoTabla;
import java.util.List;

/**
 *
 * @author rmoran
 */
public interface DoCatalogoServicio {
    
    ResponseDto guardaCatalogo(CatalogoDto catalogoDto) throws ServiceException;
    CatalogoDto consultaCatalogo(String catalogo);
    List<CatalogoDto> consultaTabla(DoTabla tabla, String estado);
    
    List<CatalogoDto> listarCatalogos(DoTabla tabla);
    
    
    
}
