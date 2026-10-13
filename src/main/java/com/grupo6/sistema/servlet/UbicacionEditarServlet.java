package com.grupo6.sistema.servlet;

import com.grupo6.sistema.model.TipoUbicacion;
import com.grupo6.sistema.model.Ubicacion;
import com.grupo6.sistema.repository.PersistenciaException;
import com.grupo6.sistema.servicio.UbicacionServicio;
import com.grupo6.sistema.validacion.UbicacionFormulario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * HU-09: editar el nombre de una ubicación. El código de ubigeo no se modifica.
 * GET  /ubicaciones/editar?tipo=distrito&id=5  -> formulario precargado
 * POST /ubicaciones/editar                      -> valida, actualiza y vuelve al listado (PRG)
 */
@WebServlet(name = "UbicacionEditarServlet", urlPatterns = "/ubicaciones/editar")
public class UbicacionEditarServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Ubicacion ubicacion = buscar(new UbicacionServicio(getServletContext()), request);
        if (ubicacion == null) {
            noEncontrado(request, response);
            return;
        }
        mostrarFormulario(request, response, ubicacion, UbicacionFormulario.desdeUbicacion(ubicacion), Map.of());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());

        UbicacionServicio servicio = new UbicacionServicio(getServletContext());
        Ubicacion ubicacion = buscar(servicio, request);
        if (ubicacion == null) {
            noEncontrado(request, response);
            return;
        }

        UbicacionFormulario formulario = UbicacionFormulario.desdeRequest(request, ubicacion.getTipo());
        Map<String, String> errores = servicio.validar(formulario, ubicacion.getPadre(), ubicacion);
        if (!errores.isEmpty()) {
            mostrarFormulario(request, response, ubicacion, formulario, errores);
            return;
        }

        try {
            servicio.actualizarNombre(ubicacion, formulario.getNombre());
            TipoUbicacion tipo = ubicacion.getTipo();
            response.sendRedirect(request.getContextPath() + UbicacionServicio.urlListado(tipo, ubicacion.getPadre())
                    + (tipo == TipoUbicacion.DEPARTAMENTO ? "?" : "&")
                    + "actualizado=" + URLEncoder.encode(formulario.getNombre(), StandardCharsets.UTF_8));
        } catch (PersistenciaException ex) {
            mostrarFormulario(request, response, ubicacion, formulario, Map.of("general", ex.getMessage()));
        }
    }

    private Ubicacion buscar(UbicacionServicio servicio, HttpServletRequest request) {
        TipoUbicacion tipo = TipoUbicacion.desdeClave(request.getParameter("tipo"));
        if (tipo == null) {
            return null;
        }
        try {
            return servicio.buscar(tipo, Long.parseLong(request.getParameter("id"))).orElse(null);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response, Ubicacion ubicacion,
                                   UbicacionFormulario formulario, Map<String, String> errores)
            throws ServletException, IOException {
        request.setAttribute("modo", "editar");
        request.setAttribute("tipo", ubicacion.getTipo());
        request.setAttribute("ubicacion", ubicacion);
        request.setAttribute("formulario", formulario);
        request.setAttribute("padre", ubicacion.getPadre());
        request.setAttribute("errores", errores);
        request.setAttribute("urlVolver", UbicacionServicio.urlListado(ubicacion.getTipo(), ubicacion.getPadre()));
        request.getRequestDispatcher("/WEB-INF/views/ubicaciones/formulario.jsp").forward(request, response);
    }

    private void noEncontrado(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        request.setAttribute("mensajeError", "No existe la ubicación solicitada.");
        request.getRequestDispatcher("/WEB-INF/views/error/404.jsp").forward(request, response);
    }
}
