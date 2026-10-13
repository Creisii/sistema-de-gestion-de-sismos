package com.grupo6.sistema.repository;

import com.grupo6.sistema.model.ReporteAfectacion;
import com.grupo6.sistema.model.Sismo;
import jakarta.persistence.EntityManager;

import java.util.List;

/** Repositorio JPA de reportes de afectación (las pantallas llegan en el sprint 3). */
public class ReporteAfectacionRepository extends RepositorioJpa<ReporteAfectacion> {

    public ReporteAfectacionRepository() {
        super(ReporteAfectacion.class);
    }

    @Override
    protected String ordenListado() {
        return "e.fechaHora DESC";
    }

    @Override
    protected void prepararRelaciones(EntityManager em, ReporteAfectacion reporte) {
        reporte.setSismo(em.getReference(Sismo.class, reporte.getSismo().getId()));
    }

    public List<ReporteAfectacion> listarPorSismo(long sismoId) {
        return consultar(em -> em.createQuery(
                        "SELECT r FROM ReporteAfectacion r WHERE r.sismo.id = :sismoId ORDER BY r.fechaHora DESC",
                        ReporteAfectacion.class)
                .setParameter("sismoId", sismoId)
                .getResultList(), "No se pudo listar los reportes de afectación.");
    }
}
