package com.grupo6.sistema.servlet;

import com.grupo6.sistema.listener.AplicacionListener;
import com.grupo6.sistema.model.EstacionMonitoreo;
import com.grupo6.sistema.repository.EstacionMonitoreoRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/** HU-10: listar estaciones de monitoreo. */
@WebServlet(name = "EstacionListarServlet", urlPatterns = "/estaciones")
public class EstacionListarServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<EstacionMonitoreo> estaciones = repositorio().listar();
        request.setAttribute("estaciones", estaciones);
        request.setAttribute("totalEstaciones", estaciones.size());

        request.getRequestDispatcher("/WEB-INF/views/estaciones/lista.jsp")
                .forward(request, response);
    }

    private EstacionMonitoreoRepository repositorio() {
        return (EstacionMonitoreoRepository) getServletContext()
                .getAttribute(AplicacionListener.REPOSITORIO_ESTACIONES);
    }
}