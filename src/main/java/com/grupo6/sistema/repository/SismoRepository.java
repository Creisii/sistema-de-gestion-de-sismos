package com.grupo6.sistema.repository;

import com.grupo6.sistema.busqueda.Pagina;
import com.grupo6.sistema.busqueda.SismoBusqueda;
import com.grupo6.sistema.model.Distrito;
import com.grupo6.sistema.model.EstacionMonitoreo;
import com.grupo6.sistema.model.Sismo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/** Repositorio JPA de sismos. */
public class SismoRepository extends RepositorioJpa<Sismo> {

    /** Columnas permitidas para ordenar (evita armar JPQL con texto que viene de la URL). */
    private static final Map<String, String> COLUMNAS_ORDEN = Map.of(
            "fecha", "s.fechaHora",
            "codigo", "s.codigo",
            "magnitud", "s.magnitud",
            "profundidad", "s.profundidad",
            "departamento", "dep.nombre",
            "estado", "s.estado");

    private static final String DESDE_SISMOS = " FROM Sismo s JOIN s.distrito dis JOIN dis.provincia pro"
            + " JOIN pro.departamento dep";

    public SismoRepository() {
        super(Sismo.class);
    }

    @Override
    protected String ordenListado() {
        return "e.fechaHora DESC";
    }

    @Override
    protected void prepararRelaciones(EntityManager em, Sismo sismo) {
        sismo.setDistrito(em.getReference(Distrito.class, sismo.getDistrito().getId()));
        if (sismo.getEstacion() != null) {
            sismo.setEstacion(em.getReference(EstacionMonitoreo.class, sismo.getEstacion().getId()));
        }
    }

    public Optional<Sismo> buscarPorCodigo(String codigo) {
        return buscarUno("SELECT s FROM Sismo s WHERE s.codigo = :valor", codigo);
    }

    public boolean existeCodigo(String codigo) {
        return buscarPorCodigo(codigo).isPresent();
    }

    /** Para Editar: indica si el código ya pertenece a OTRO sismo distinto del que se está editando. */
    public boolean existeCodigoEnOtroSismo(String codigo, long idActual) {
        return buscarPorCodigo(codigo)
                .map(s -> s.getId() != idActual)
                .orElse(false);
    }

    /**
     * HU-15: búsqueda con filtros, orden y paginación.
     * Primero cuenta los resultados (para la navegación) y luego trae solo los de la página pedida
     * con setFirstResult / setMaxResults, sin cargar todos los registros a la vez.
     */
    public Pagina<Sismo> buscar(SismoBusqueda busqueda) {
        List<String> condiciones = new ArrayList<>();
        Map<String, Object> parametros = new HashMap<>();
        agregarCondiciones(busqueda, condiciones, parametros);

        String where = condiciones.isEmpty() ? "" : " WHERE " + String.join(" AND ", condiciones);
        String direccion = busqueda.isDescendente() ? " DESC" : " ASC";
        String orden = " ORDER BY " + COLUMNAS_ORDEN.getOrDefault(busqueda.getOrden(), "s.fechaHora")
                + direccion + ", s.id" + direccion;

        return consultar(em -> {
            TypedQuery<Long> conteo = em.createQuery("SELECT COUNT(s)" + DESDE_SISMOS + where, Long.class);
            parametros.forEach(conteo::setParameter);
            long total = conteo.getSingleResult();

            int tamanio = busqueda.getTamanio();
            int pagina = Math.min(busqueda.getPagina(), Pagina.calcularTotalPaginas(total, tamanio));
            busqueda.setPagina(pagina);

            TypedQuery<Sismo> consulta = em.createQuery("SELECT s" + DESDE_SISMOS + where + orden, Sismo.class);
            parametros.forEach(consulta::setParameter);
            List<Sismo> registros = consulta
                    .setFirstResult((pagina - 1) * tamanio)
                    .setMaxResults(tamanio)
                    .getResultList();
            return new Pagina<>(registros, pagina, tamanio, total);
        }, "No se pudo realizar la búsqueda de sismos.");
    }

    /** Condiciones JPQL de cada filtro. Alias: s = sismo, dis = distrito, pro = provincia, dep = departamento. */
    private void agregarCondiciones(SismoBusqueda b, List<String> condiciones, Map<String, Object> parametros) {
        if (!b.getTerminoTexto().isEmpty()) {
            String referencia = b.getTerminoTexto().toLowerCase(Locale.ROOT).replace("!", "!!").replace("%", "!%").replace("_", "!_");
            condiciones.add("(LOWER(s.codigo) = :codigo OR LOWER(s.referencia) LIKE :referencia ESCAPE '!'"
                    + " OR LOWER(dis.nombre) LIKE :referencia ESCAPE '!'"
                    + " OR LOWER(pro.nombre) LIKE :referencia ESCAPE '!')");
            parametros.put("codigo", b.getTerminoTexto().toLowerCase(Locale.ROOT));
            parametros.put("referencia", "%" + referencia + "%");
        }
        if (b.getDepartamentoId() != null) {
            condiciones.add("dep.id = :departamentoId");
            parametros.put("departamentoId", b.getDepartamentoId());
        }
        if (b.getMagnitudMin() != null) {
            condiciones.add("s.magnitud >= :magnitudMin");
            parametros.put("magnitudMin", b.getMagnitudMin());
        }
        if (b.getMagnitudMax() != null) {
            condiciones.add("s.magnitud <= :magnitudMax");
            parametros.put("magnitudMax", b.getMagnitudMax());
        }
        if (b.getDesde() != null) {
            condiciones.add("s.fechaHora >= :desde");
            parametros.put("desde", b.getDesde().atStartOfDay());
        }
        if (b.getHasta() != null) {
            // Hasta el final del día indicado: menor que el inicio del día siguiente
            condiciones.add("s.fechaHora < :hasta");
            parametros.put("hasta", b.getHasta().plusDays(1).atStartOfDay());
        }
        // HU-30 (filtro por estado) se agrega aquí.
    }
}
