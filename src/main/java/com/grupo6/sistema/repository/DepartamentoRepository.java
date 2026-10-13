package com.grupo6.sistema.repository;

import com.grupo6.sistema.model.Departamento;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Repositorio JPA de departamentos. */
public class DepartamentoRepository extends RepositorioJpa<Departamento> {

    public DepartamentoRepository() {
        super(Departamento.class);
    }

    @Override
    protected String ordenListado() {
        return "e.nombre";
    }

    public Optional<Departamento> buscarPorCodigo(String codigo) {
        return buscarUno("SELECT d FROM Departamento d WHERE d.codigo = :valor", codigo);
    }

    public boolean existeNombre(String nombre, Long idExcluido) {
        String jpql = "SELECT COUNT(d) FROM Departamento d WHERE LOWER(d.nombre) = LOWER(:nombre)"
                + (idExcluido == null ? "" : " AND d.id <> :id");
        return consultar(em -> {
            var consulta = em.createQuery(jpql, Long.class).setParameter("nombre", nombre);
            if (idExcluido != null) {
                consulta.setParameter("id", idExcluido);
            }
            return consulta.getSingleResult() > 0;
        }, "No se pudo validar el nombre.");
    }

    public long contarProvincias(long departamentoId) {
        return contar("SELECT COUNT(p) FROM Provincia p WHERE p.departamento.id = :valor", departamentoId);
    }

    /** Cantidad de provincias de cada departamento: id del departamento -> cantidad. */
    public Map<Long, Long> contarProvinciasPorDepartamento() {
        return consultar(em -> {
            List<Object[]> filas = em.createQuery(
                    "SELECT p.departamento.id, COUNT(p) FROM Provincia p GROUP BY p.departamento.id",
                    Object[].class).getResultList();
            Map<Long, Long> conteos = new HashMap<>();
            for (Object[] fila : filas) {
                conteos.put((Long) fila[0], (Long) fila[1]);
            }
            return conteos;
        }, "No se pudo contar las provincias.");
    }
}
