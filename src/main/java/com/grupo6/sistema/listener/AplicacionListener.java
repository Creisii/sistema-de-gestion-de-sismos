package com.grupo6.sistema.listener;

import com.grupo6.sistema.config.JpaUtil;
import com.grupo6.sistema.repository.DepartamentoRepository;
import com.grupo6.sistema.repository.DistritoRepository;
import com.grupo6.sistema.repository.EstacionMonitoreoRepository;
import com.grupo6.sistema.repository.ProvinciaRepository;
import com.grupo6.sistema.repository.ReporteAfectacionRepository;
import com.grupo6.sistema.repository.SeguimientoSismoRepository;
import com.grupo6.sistema.repository.SismoRepository;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Al iniciar la aplicación crea un repositorio JPA por entidad y los comparte en el ServletContext.
 * Los datos ya no se cargan aquí: están en MySQL (database/02_datos_iniciales.sql).
 * Al detener la aplicación cierra el EntityManagerFactory.
 */
@WebListener
public class AplicacionListener implements ServletContextListener {

    public static final String REPOSITORIO_SISMOS = "repositorioSismos";
    public static final String REPOSITORIO_DEPARTAMENTOS = "repositorioDepartamentos";
    public static final String REPOSITORIO_PROVINCIAS = "repositorioProvincias";
    public static final String REPOSITORIO_DISTRITOS = "repositorioDistritos";
    public static final String REPOSITORIO_ESTACIONES = "repositorioEstaciones";
    public static final String REPOSITORIO_REPORTES = "repositorioReportes";
    public static final String REPOSITORIO_SEGUIMIENTOS = "repositorioSeguimientos";

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext contexto = event.getServletContext();
        contexto.setAttribute(REPOSITORIO_SISMOS, new SismoRepository());
        contexto.setAttribute(REPOSITORIO_DEPARTAMENTOS, new DepartamentoRepository());
        contexto.setAttribute(REPOSITORIO_PROVINCIAS, new ProvinciaRepository());
        contexto.setAttribute(REPOSITORIO_DISTRITOS, new DistritoRepository());
        contexto.setAttribute(REPOSITORIO_ESTACIONES, new EstacionMonitoreoRepository());
        contexto.setAttribute(REPOSITORIO_REPORTES, new ReporteAfectacionRepository());
        contexto.setAttribute(REPOSITORIO_SEGUIMIENTOS, new SeguimientoSismoRepository());
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        JpaUtil.cerrar();
    }
}
