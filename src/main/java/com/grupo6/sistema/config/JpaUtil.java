package com.grupo6.sistema.config;

import com.grupo6.sistema.repository.PersistenciaException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Crea una sola vez el EntityManagerFactory de la unidad de persistencia "SismosPU" y entrega
 * un EntityManager nuevo a cada operación de los repositorios.
 *
 * Los datos de conexión se leen en este orden (cada uno reemplaza al anterior):
 * 1. db.properties (valores por defecto, en el repositorio).
 * 2. db-local.properties (configuración de cada integrante, fuera del repositorio).
 * 3. Propiedades del sistema -Dsismos.db.usuario, -Dsismos.db.contrasena, etc.
 */
public final class JpaUtil {

    public static final String UNIDAD_PERSISTENCIA = "SismosPU";

    private static volatile EntityManagerFactory fabrica;

    private JpaUtil() {
    }

    public static EntityManager crearEntityManager() {
        return obtenerFabrica().createEntityManager();
    }

    public static synchronized void cerrar() {
        if (fabrica != null && fabrica.isOpen()) {
            fabrica.close();
        }
        fabrica = null;
    }

    /** Se crea al primer uso; si MySQL no está disponible, se vuelve a intentar en la siguiente petición. */
    private static EntityManagerFactory obtenerFabrica() {
        EntityManagerFactory actual = fabrica;
        if (actual == null) {
            synchronized (JpaUtil.class) {
                if (fabrica == null) {
                    fabrica = crearFabrica();
                }
                actual = fabrica;
            }
        }
        return actual;
    }

    private static EntityManagerFactory crearFabrica() {
        Properties propiedades = cargarPropiedades();
        Map<String, String> configuracion = new HashMap<>();
        configuracion.put("jakarta.persistence.jdbc.driver", valor(propiedades, "driver"));
        configuracion.put("jakarta.persistence.jdbc.url", valor(propiedades, "url"));
        configuracion.put("jakarta.persistence.jdbc.user", valor(propiedades, "usuario"));
        configuracion.put("jakarta.persistence.jdbc.password", valor(propiedades, "contrasena"));
        try {
            return Persistence.createEntityManagerFactory(UNIDAD_PERSISTENCIA, configuracion);
        } catch (RuntimeException ex) {
            throw new PersistenciaException("No se pudo iniciar JPA. Verifique que MySQL esté iniciado "
                    + "y los datos de db.properties / db-local.properties.", ex);
        }
    }

    private static Properties cargarPropiedades() {
        Properties propiedades = new Properties();
        if (!cargar(propiedades, "db.properties")) {
            throw new PersistenciaException("No se encontró db.properties en src/main/resources.", null);
        }
        cargar(propiedades, "db-local.properties");
        return propiedades;
    }

    private static boolean cargar(Properties destino, String archivo) {
        try (InputStream entrada = JpaUtil.class.getClassLoader().getResourceAsStream(archivo)) {
            if (entrada == null) {
                return false;
            }
            destino.load(entrada);
            return true;
        } catch (IOException ex) {
            throw new PersistenciaException("No se pudo leer " + archivo + ".", ex);
        }
    }

    private static String valor(Properties propiedades, String clave) {
        String sistema = System.getProperty("sismos.db." + clave);
        if (sistema != null && !sistema.isBlank()) {
            return sistema;
        }
        return propiedades.getProperty(clave, "").trim();
    }
}
