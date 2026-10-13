package com.grupo6.sistema.repository;

import com.grupo6.sistema.model.Distrito;
import com.grupo6.sistema.model.EstacionMonitoreo;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

/** Repositorio JPA de estaciones de monitoreo. */
public class EstacionMonitoreoRepository extends RepositorioJpa<EstacionMonitoreo> {

    public EstacionMonitoreoRepository() {
        super(EstacionMonitoreo.class);
    }

    @Override
    protected String ordenListado() {
        return "e.codigo";
    }

    @Override
    protected void prepararRelaciones(EntityManager em, EstacionMonitoreo estacion) {
        estacion.setDistrito(em.getReference(Distrito.class, estacion.getDistrito().getId()));
    }

    /** Solo las estaciones activas pueden recibir nuevos registros de sismos. */
    public List<EstacionMonitoreo> listarActivas() {
        return consultar(em -> em.createQuery(
                        "SELECT e FROM EstacionMonitoreo e WHERE e.estado = :estado ORDER BY e.codigo",
                        EstacionMonitoreo.class)
                .setParameter("estado", EstacionMonitoreo.ESTADO_ACTIVA)
                .getResultList(), "No se pudo listar las estaciones activas.");
    }

    public Optional<EstacionMonitoreo> buscarPorCodigo(String codigo) {
        return buscarUno("SELECT e FROM EstacionMonitoreo e WHERE e.codigo = :valor", codigo);
    }

    public long contarSismos(long estacionId) {
        return contar("SELECT COUNT(s) FROM Sismo s WHERE s.estacion.id = :valor", estacionId);
    }
}
