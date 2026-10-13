package com.grupo6.sistema.repository;

import com.grupo6.sistema.model.Departamento;
import com.grupo6.sistema.model.Provincia;
import jakarta.persistence.EntityManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Repositorio JPA de provincias. */
public class ProvinciaRepository extends RepositorioJpa<Provincia> {

    public ProvinciaRepository() {
        super(Provincia.class);
    }

    @Override
    protected String ordenListado() {
        return "e.departamento.nombre, e.nombre";
    }

    @Override
    protected void prepararRelaciones(EntityManager em, Provincia provincia) {
        provincia.setDepartamento(em.getReference(Departamento.class, provincia.getDepartamento().getId()));
    }

    public List<Provincia> listarPorDepartamento(long departamentoId) {
        return consultar(em -> em.createQuery(
                        "SELECT p FROM Provincia p WHERE p.departamento.id = :departamentoId ORDER BY p.nombre",
                        Provincia.class)
                .setParameter("departamentoId", departamentoId)
                .getResultList(), "No se pudo listar las provincias.");
    }

    public Optional<Provincia> buscarPorCodigo(String codigo) {
        return buscarUno("SELECT p FROM Provincia p WHERE p.codigo = :valor", codigo);
    }

    /** El nombre de una provincia no se repite dentro del mismo departamento. */
    public boolean existeNombreEnDepartamento(String nombre, long departamentoId, Long idExcluido) {
        String jpql = "SELECT COUNT(p) FROM Provincia p WHERE LOWER(p.nombre) = LOWER(:nombre) "
                + "AND p.departamento.id = :departamentoId" + (idExcluido == null ? "" : " AND p.id <> :id");
        return consultar(em -> {
            var consulta = em.createQuery(jpql, Long.class)
                    .setParameter("nombre", nombre)
                    .setParameter("departamentoId", departamentoId);
            if (idExcluido != null) {
                consulta.setParameter("id", idExcluido);
            }
            return consulta.getSingleResult() > 0;
        }, "No se pudo validar el nombre.");
    }

    public long contarDistritos(long provinciaId) {
        return contar("SELECT COUNT(d) FROM Distrito d WHERE d.provincia.id = :valor", provinciaId);
    }

    /** Cantidad de distritos de cada provincia de un departamento: id de la provincia -> cantidad. */
    public Map<Long, Long> contarDistritosPorProvincia(long departamentoId) {
        return consultar(em -> {
            List<Object[]> filas = em.createQuery(
                            "SELECT d.provincia.id, COUNT(d) FROM Distrito d "
                                    + "WHERE d.provincia.departamento.id = :departamentoId GROUP BY d.provincia.id",
                            Object[].class)
                    .setParameter("departamentoId", departamentoId)
                    .getResultList();
            Map<Long, Long> conteos = new HashMap<>();
            for (Object[] fila : filas) {
                conteos.put((Long) fila[0], (Long) fila[1]);
            }
            return conteos;
        }, "No se pudo contar los distritos.");
    }
}
