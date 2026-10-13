package com.grupo6.sistema.repository;

import com.grupo6.sistema.config.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Base de todos los repositorios: CRUD con EntityManager.
 *
 * - Cada operación abre su propio EntityManager y lo cierra al terminar.
 * - Las operaciones que modifican datos (agregar, actualizar, eliminar) se ejecutan dentro de
 *   una transacción: si algo falla se hace rollback y no queda nada a medias.
 * - Los errores se convierten en PersistenciaException con un mensaje para el usuario.
 *
 * @param <T> entidad JPA que administra el repositorio
 */
public abstract class RepositorioJpa<T> {

    /** Códigos de error de MySQL para restricciones de la base de datos. */
    private static final int MYSQL_DUPLICADO = 1062;
    private static final int MYSQL_REGISTRO_EN_USO = 1451;
    private static final int MYSQL_REFERENCIA_INEXISTENTE = 1452;

    private final Class<T> clase;

    protected RepositorioJpa(Class<T> clase) {
        this.clase = clase;
    }

    /** Orden del listado general, en JPQL sobre el alias "e". */
    protected String ordenListado() {
        return "e.id";
    }

    /**
     * Antes de guardar, reemplaza las entidades relacionadas (que llegan solo con su id desde el
     * formulario) por referencias administradas por este EntityManager.
     */
    protected void prepararRelaciones(EntityManager em, T entidad) {
    }

    public List<T> listar() {
        String jpql = "SELECT e FROM " + clase.getSimpleName() + " e ORDER BY " + ordenListado();
        return consultar(em -> em.createQuery(jpql, clase).getResultList(), "No se pudo obtener el listado.");
    }

    public Optional<T> buscarPorId(long id) {
        return consultar(em -> Optional.ofNullable(em.find(clase, id)), "No se pudo buscar el registro.");
    }

    public T agregar(T entidad) {
        return enTransaccion(em -> {
            prepararRelaciones(em, entidad);
            em.persist(entidad);
            return entidad;
        }, "No se pudo registrar.");
    }

    public T actualizar(T entidad) {
        return enTransaccion(em -> {
            prepararRelaciones(em, entidad);
            return em.merge(entidad);
        }, "No se pudo actualizar.");
    }

    public boolean eliminar(long id) {
        return enTransaccion(em -> {
            T entidad = em.find(clase, id);
            if (entidad == null) {
                return false;
            }
            em.remove(entidad);
            return true;
        }, "No se pudo eliminar.");
    }

    /** Ejecuta una consulta de solo lectura. */
    protected <R> R consultar(Function<EntityManager, R> operacion, String mensajeError) {
        EntityManager em = JpaUtil.crearEntityManager();
        try {
            return operacion.apply(em);
        } catch (PersistenciaException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw traducir(ex, mensajeError);
        } finally {
            em.close();
        }
    }

    /** Ejecuta una operación que modifica datos dentro de una transacción (begin / commit / rollback). */
    protected <R> R enTransaccion(Function<EntityManager, R> operacion, String mensajeError) {
        EntityManager em = JpaUtil.crearEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            R resultado = operacion.apply(em);
            tx.commit();
            return resultado;
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            if (ex instanceof PersistenciaException persistencia) {
                throw persistencia;
            }
            throw traducir(ex, mensajeError);
        } finally {
            em.close();
        }
    }

    /** Busca un único resultado con una consulta JPQL de un parámetro llamado :valor. */
    protected Optional<T> buscarUno(String jpql, Object valor) {
        return consultar(em -> em.createQuery(jpql, clase)
                .setParameter("valor", valor)
                .setMaxResults(1)
                .getResultList()
                .stream()
                .findFirst(), "No se pudo realizar la búsqueda.");
    }

    /** Ejecuta una consulta JPQL COUNT de un parámetro llamado :valor. */
    protected long contar(String jpql, Object valor) {
        return consultar(em -> em.createQuery(jpql, Long.class)
                .setParameter("valor", valor)
                .getSingleResult(), "No se pudo realizar el conteo.");
    }

    /** Traduce los errores de restricciones de MySQL a mensajes claros. */
    private static PersistenciaException traducir(RuntimeException ex, String mensajePorDefecto) {
        Throwable causa = ex;
        while (causa != null) {
            if (causa instanceof SQLException sql) {
                switch (sql.getErrorCode()) {
                    case MYSQL_DUPLICADO:
                        return new PersistenciaException("Ya existe un registro con el mismo código o nombre.", ex);
                    case MYSQL_REGISTRO_EN_USO:
                        return new PersistenciaException("No se puede eliminar porque tiene registros asociados.", ex);
                    case MYSQL_REFERENCIA_INEXISTENTE:
                        return new PersistenciaException("El registro relacionado no existe.", ex);
                    default:
                        break;
                }
            }
            causa = causa.getCause();
        }
        return new PersistenciaException(mensajePorDefecto + " Verifique la conexión con la base de datos.", ex);
    }
}
