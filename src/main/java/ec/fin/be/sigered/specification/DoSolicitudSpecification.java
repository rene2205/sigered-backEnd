package ec.fin.be.sigered.specification;

import ec.fin.be.sigered.dto.SolicitudFiltroDto;
import ec.fin.be.sigered.primary.entity.DoSolicitud;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

public class DoSolicitudSpecification {

    private DoSolicitudSpecification() {
    }

    public static Specification<DoSolicitud> buscar(SolicitudFiltroDto filtro) {
        return (root, query, cb) -> construirPredicate(root, cb, filtro);
    }

    private static Predicate construirPredicate(
            Root<DoSolicitud> root,
            CriteriaBuilder cb,
            SolicitudFiltroDto filtro) {

        Predicate predicate = cb.conjunction();

        // JOINS
        Join<Object, Object> institucionJoin = root.join("institucion", JoinType.LEFT);
        Join<Object, Object> gerenciaJoin = root.join("gerenciaResponsable", JoinType.LEFT);

        predicate = agregarFiltrosId(predicate, cb, filtro, institucionJoin, gerenciaJoin);
        predicate = agregarFiltrosTexto(predicate, cb, root, filtro);
        predicate = agregarFiltrosFecha(predicate, cb, root, filtro);
        predicate = agregarFiltroEstado(predicate, cb, root, filtro);

        return predicate;
    }

    private static Predicate agregarFiltrosId(
            Predicate predicate,
            CriteriaBuilder cb,
            SolicitudFiltroDto filtro,
            Join<Object, Object> institucionJoin,
            Join<Object, Object> gerenciaJoin) {

        if (filtro.getIdInstitucion() != null) {
            predicate = cb.and(predicate,
                    cb.equal(institucionJoin.get("id"), filtro.getIdInstitucion()));
        }

        if (filtro.getIdGerenciaResponsable() != null) {
            predicate = cb.and(predicate,
                    cb.equal(gerenciaJoin.get("id"), filtro.getIdGerenciaResponsable()));
        }

        return predicate;
    }

    private static Predicate agregarFiltrosTexto(
            Predicate predicate,
            CriteriaBuilder cb,
            Root<DoSolicitud> root,
            SolicitudFiltroDto filtro) {

        if (tieneValor(filtro.getDocumentoInstitucion())) {
            predicate = cb.and(predicate,
                    cb.like(cb.lower(root.get("documentoInstitucion")),
                            "%" + filtro.getDocumentoInstitucion().toLowerCase() + "%"));
        }

        if (tieneValor(filtro.getDocumentoInterno())) {
            predicate = cb.and(predicate,
                    cb.like(cb.lower(root.get("documentoInterno")),
                            "%" + filtro.getDocumentoInterno().toLowerCase() + "%"));
        }

        if (tieneValor(filtro.getTituloDocumento())) {
            predicate = cb.and(predicate,
                    cb.like(cb.lower(root.get("tituloDocumento")),
                            "%" + filtro.getTituloDocumento().toLowerCase() + "%"));
        }

        return predicate;
    }

    private static Predicate agregarFiltrosFecha(
            Predicate predicate,
            CriteriaBuilder cb,
            Root<DoSolicitud> root,
            SolicitudFiltroDto filtro) {

        if (filtro.getFechaIngresoDesde() != null) {
            predicate = cb.and(predicate,
                    cb.greaterThanOrEqualTo(root.get("fechaIngreso"),
                            filtro.getFechaIngresoDesde()));
        }

        if (filtro.getFechaIngresoHasta() != null) {
            predicate = cb.and(predicate,
                    cb.lessThanOrEqualTo(root.get("fechaIngreso"),
                            filtro.getFechaIngresoHasta()));
        }

        if (filtro.getDiasTermino() != null) {
            predicate = cb.and(predicate,
                    cb.equal(root.get("diasTermino"), filtro.getDiasTermino()));
        }

        return predicate;
    }

    private static Predicate agregarFiltroEstado(
            Predicate predicate,
            CriteriaBuilder cb,
            Root<DoSolicitud> root,
            SolicitudFiltroDto filtro) {

        if (tieneValor(filtro.getEstado())) {
            predicate = cb.and(predicate,
                    cb.equal(root.get("estado"), filtro.getEstado()));
        }

        return predicate;
    }

    private static boolean tieneValor(String valor) {
        return valor != null && !valor.isEmpty();
    }

}