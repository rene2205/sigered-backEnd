package ec.fin.be.sigered.specification;

import ec.fin.be.sigered.dto.InstitucionFiltroDto;
import ec.fin.be.sigered.primary.entity.DoInstitucion;
import ec.fin.be.sigered.utils.Constantes;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

public class DoInstitucionSpecification {

    private DoInstitucionSpecification() {
    }

    public static Specification<DoInstitucion> buscar(InstitucionFiltroDto filtro) {
        return (Root<DoInstitucion> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {

            // excluir registros con estado eliminado
            Predicate predicate = cb.and(
                cb.conjunction(),
                cb.notEqual(root.get("estado"), Constantes.ESTADO_ELIMINADO));

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