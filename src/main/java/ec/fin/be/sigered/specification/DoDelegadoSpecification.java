package ec.fin.be.sigered.specification;

import ec.fin.be.sigered.dto.DelegadoFiltroDto;
import ec.fin.be.sigered.primary.entity.DoDelegado;
import ec.fin.be.sigered.utils.Constantes;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

public class DoDelegadoSpecification {

    private DoDelegadoSpecification() {
    }

    public static Specification<DoDelegado> buscar(DelegadoFiltroDto filtro) {
        return (Root<DoDelegado> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {

            // excluir registros con estado eliminado
            Predicate predicate = predicadoBase(root, cb);
            Join<Object, Object> gerenciaJoin = root.join("gerencia", JoinType.LEFT);

            if (filtro.getIdGerencia() != null) {
                predicate = cb.and(predicate,
                        cb.equal(gerenciaJoin.get("id"), filtro.getIdGerencia()));
            }

            if (filtro.getIdentificacionFuncionario() != null && !filtro.getIdentificacionFuncionario().isEmpty()) {
                predicate = cb.and(predicate,
                        cb.like(root.get("cedula"),
                                "%" + filtro.getIdentificacionFuncionario() + "%"));
            }

            if (filtro.getNombreFuncionario() != null && !filtro.getNombreFuncionario().isEmpty()) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("nombreFuncionario")),
                                "%" + filtro.getNombreFuncionario().toLowerCase() + "%"));
            }

            return predicate;
        };
    }

    private static Predicate predicadoBase(Root<DoDelegado> root, CriteriaBuilder cb) {
        return cb.and(
                cb.conjunction(),
                cb.notEqual(root.get("estado"), Constantes.ESTADO_ELIMINADO));
    }
}