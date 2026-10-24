/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.fin.be.sigered.specification;
import ec.fin.be.sigered.dto.ResolucionFiltroDto;
import ec.fin.be.sigered.primary.entity.DoResolucion;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
/**
 *
 * @author rmoran
 */
public class DoResolucionSpecifications {
    public static Specification<DoResolucion> buscarFiltro(ResolucionFiltroDto filtro){
       return (root, query, cb) -> construirPredicate(root, cb, filtro);
    }
    
     private static Predicate construirPredicate(
            Root<DoResolucion> root,
            CriteriaBuilder cb,
            ResolucionFiltroDto filtro) {
         
         Predicate predicate = cb.conjunction();
         predicate = agregarFiltroArea(predicate, cb, root, filtro);
         predicate = agregarFiltroFecha(predicate, cb, root, filtro);
         predicate = agregarFiltroArea(predicate, cb, root, filtro);
         predicate = agregarFiltroEstado(predicate, cb, root, filtro);
         predicate = agregarFiltroTipo(predicate, cb, root, filtro);
         predicate = agregarFiltroResolucion(predicate, cb, root, filtro);
         
         
         return predicate;
     
     }
     
     
     private static Predicate agregarFiltroFecha(
            Predicate predicate,
            CriteriaBuilder cb,
            Root<DoResolucion> root,
            ResolucionFiltroDto filtro) {

        if (filtro.getFechaInicial()!= null) {
            predicate = cb.and(predicate,
                    cb.greaterThanOrEqualTo(root.get("reFechaResolucion"),
                            filtro.getFechaInicial()));
        }

        if (filtro.getFechaFinal()!= null) {
            predicate = cb.and(predicate,
                    cb.lessThanOrEqualTo(root.get("reFechaResolucion"),
                            filtro.getFechaFinal()));
        }

        

        return predicate;
    }
      
      private static Predicate agregarFiltroArea(
            Predicate predicate,
            CriteriaBuilder cb,
            Root<DoResolucion> root,
            ResolucionFiltroDto filtro) {
           
           if (filtro.getArea()!=null && !filtro.getArea().isEmpty()){
                predicate=cb.and(predicate,cb.equal(root.get("reArea"), filtro.getArea())
                );
            }
           
        return predicate;
    }
      
       private static Predicate agregarFiltroTipo(
            Predicate predicate,
            CriteriaBuilder cb,
            Root<DoResolucion> root,
            ResolucionFiltroDto filtro) {
           
           if (filtro.getTipo()!=null && !filtro.getTipo().isEmpty()){
                predicate=cb.and(predicate,cb.equal(root.get("reTipoVigencia"), filtro.getTipo())
                );
            }
           
        return predicate;
    }
       
        private static Predicate agregarFiltroEstado(
            Predicate predicate,
            CriteriaBuilder cb,
            Root<DoResolucion> root,
            ResolucionFiltroDto filtro) {
           
           if (filtro.getEstado()!=null && !filtro.getEstado().isEmpty()){
                predicate=cb.and(predicate,cb.equal(root.get("reEstadoActual"), filtro.getEstado())
                );
            }
           
        return predicate;
    }
        
         private static Predicate agregarFiltroResolucion(
            Predicate predicate,
            CriteriaBuilder cb,
            Root<DoResolucion> root,
            ResolucionFiltroDto filtro) {
           
           if (filtro.getResolucion()!=null){
                predicate=cb.and(predicate,cb.like(root.get("reResolucion"), "%"+filtro.getResolucion()+"%")
                );
            }
           
        return predicate;
    }
        
    }
