package com.grupo6.sistema.servlet;

import com.grupo6.sistema.busqueda.Pagina;
import com.grupo6.sistema.busqueda.SismoBusqueda;
import com.grupo6.sistema.listener.AplicacionListener;
import com.grupo6.sistema.model.Sismo;
import com.grupo6.sistema.repository.DepartamentoRepository;
import com.grupo6.sistema.repository.SismoRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * HU-01 y HU-15: lista de sismos con búsqueda, filtros, orden y paginación.
 * Los filtros llegan como parámetros GET, por ejemplo:
 * /sismos?departamentoId=2&magnitudMin=4&magnitudMax=6&desde=2026-01-01&hasta=2026-12-31&orden=magnitud&pagina=2
 */
@WebServlet(name = "SismoListarServlet", urlPatterns = {"/sismos"})
public class SismoListarServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        SismoBusqueda busqueda = SismoBusqueda.desdeRequest(request);
        Pagina<Sismo> pagina = repositorioSismos().buscar(busqueda);

        request.setAttribute("busqueda", busqueda);
        request.setAttribute("pagina", pagina);
        request.setAttribute("sismos", pagina.getRegistros());
        request.setAttribute("departamentos", repositorioDepartamentos().listar());

        request.getRequestDispatcher("/WEB-INF/views/sismos/lista.jsp")
                .forward(request, response);
    }

    private SismoRepository repositorioSismos() {
        return (SismoRepository) getServletContext()
                .getAttribute(AplicacionListener.REPOSITORIO_SISMOS);
    }

    private DepartamentoRepository repositorioDepartamentos() {
        return (DepartamentoRepository) getServletContext()
                .getAttribute(AplicacionListener.REPOSITORIO_DEPARTAMENTOS);
    }
}
