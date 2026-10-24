package ec.fin.be.sigered.specification;

import ec.fin.be.sigered.dto.GerenciaFiltroDto;
import ec.fin.be.sigered.primary.entity.DoGerencia;
import ec.fin.be.sigered.utils.Constantes;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

public class DoGerenciaSpecification {

    private DoGerenciaSpecification() {        
    }

    public static Specification<DoGerencia> buscar(GerenciaFiltroDto filtro) {
        return (Root<DoGerencia> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {

            // excluir registros con estado eliminado
            Predicate predicate = cb.and(
                cb.conjunction(),
                cb.notEqual(root.get("estado"), Constantes.ESTADO_ELIMINADO));

            // filtrar por gerencia padre
            if (filtro.getIdPadre() != null && filtro.getIdPadre() != 0) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("padre").get("id"), filtro.getIdPadre()));
            }

            if (filtro.getCodigo() != null && !filtro.getCodigo().isEmpty()) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("codigo")),
                                "%" + filtro.getCodigo().toLowerCase() + "%"));
            }

            if (filtro.getNombre() != null && !filtro.getNombre().isEmpty()) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("nombre")),
                                "%" + filtro.getNombre().toLowerCase() + "%"));
            }

            return predicate;
        };
    }
}