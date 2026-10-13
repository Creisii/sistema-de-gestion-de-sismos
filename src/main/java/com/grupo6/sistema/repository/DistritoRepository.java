package com.grupo6.sistema.repository;

import com.grupo6.sistema.model.Distrito;
import com.grupo6.sistema.model.Provincia;
import jakarta.persistence.EntityManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Repositorio JPA de distritos. */
public class DistritoRepository extends RepositorioJpa<Distrito> {

    public DistritoRepository() {
        super(Distrito.class);
    }

    /** Ordenados por departamento, provincia y distrito (útil para las listas desplegables). */
    @Override
    protected String ordenListado() {
        return "e.provincia.departamento.nombre, e.provincia.nombre, e.nombre";
    }

    @Override
    protected void prepararRelaciones(EntityManager em, Distrito distrito) {
        distrito.setProvincia(em.getReference(Provincia.class, distrito.getProvincia().getId()));
    }

    public List<Distrito> listarPorProvincia(long provinciaId) {
        return consultar(em -> em.createQuery(
                        "SELECT d FROM Distrito d WHERE d.provincia.id = :provinciaId ORDER BY d.nombre",
                        Distrito.class)
                .setParameter("provinciaId", provinciaId)
                .getResultList(), "No se pudo listar los distritos.");
    }

    public Optional<Distrito> buscarPorCodigo(String codigo) {
        return buscarUno("SELECT d FROM Distrito d WHERE d.codigo = :valor", codigo);
    }

    /** El nombre de un distrito no se repite dentro de la misma provincia. */
    public boolean existeNombreEnProvincia(String nombre, long provinciaId, Long idExcluido) {
        String jpql = "SELECT COUNT(d) FROM Distrito d WHERE LOWER(d.nombre) = LOWER(:nombre) "
                + "AND d.provincia.id = :provinciaId" + (idExcluido == null ? "" : " AND d.id <> :id");
        return consultar(em -> {
            var consulta = em.createQuery(jpql, Long.class)
                    .setParameter("nombre", nombre)
                    .setParameter("provinciaId", provinciaId);
            if (idExcluido != null) {
                consulta.setParameter("id", idExcluido);
            }
            return consulta.getSingleResult() > 0;
        }, "No se pudo validar el nombre.");
    }

    public long contarSismos(long distritoId) {
        return contar("SELECT COUNT(s) FROM Sismo s WHERE s.distrito.id = :valor", distritoId);
    }

    public long contarEstaciones(long distritoId) {
        return contar("SELECT COUNT(e) FROM EstacionMonitoreo e WHERE e.distrito.id = :valor", distritoId);
    }

    /** Cantidad de sismos de cada distrito de una provincia: id del distrito -> cantidad. */
    public Map<Long, Long> contarSismosPorDistrito(long provinciaId) {
        return consultar(em -> {
            List<Object[]> filas = em.createQuery(
                            "SELECT s.distrito.id, COUNT(s) FROM Sismo s "
                                    + "WHERE s.distrito.provincia.id = :provinciaId GROUP BY s.distrito.id",
                            Object[].class)
                    .setParameter("provinciaId", provinciaId)
                    .getResultList();
            Map<Long, Long> conteos = new HashMap<>();
            for (Object[] fila : filas) {
                conteos.put((Long) fila[0], (Long) fila[1]);
            }
            return conteos;
        }, "No se pudo contar los sismos.");
    }
}
