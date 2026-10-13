package com.grupo6.sistema.repository;

import com.grupo6.sistema.model.SeguimientoSismo;
import com.grupo6.sistema.model.Sismo;
import jakarta.persistence.EntityManager;

import java.util.List;

/** Repositorio JPA de actualizaciones de seguimiento (las pantallas llegan en el sprint 3). */
public class SeguimientoSismoRepository extends RepositorioJpa<SeguimientoSismo> {

    public SeguimientoSismoRepository() {
        super(SeguimientoSismo.class);
    }

    @Override
    protected String ordenListado() {
        return "e.fechaActualizacion DESC";
    }

    @Override
    protected void prepararRelaciones(EntityManager em, SeguimientoSismo seguimiento) {
        seguimiento.setSismo(em.getReference(Sismo.class, seguimiento.getSismo().getId()));
    }

    public List<SeguimientoSismo> listarPorSismo(long sismoId) {
        return consultar(em -> em.createQuery(
                        "SELECT s FROM SeguimientoSismo s WHERE s.sismo.id = :sismoId ORDER BY s.fechaActualizacion DESC",
                        SeguimientoSismo.class)
                .setParameter("sismoId", sismoId)
                .getResultList(), "No se pudo listar los seguimientos.");
    }
}
