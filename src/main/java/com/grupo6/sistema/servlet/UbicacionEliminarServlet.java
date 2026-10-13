package com.grupo6.sistema.servlet;

import com.grupo6.sistema.model.TipoUbicacion;
import com.grupo6.sistema.model.Ubicacion;
import com.grupo6.sistema.repository.PersistenciaException;
import com.grupo6.sistema.servicio.UbicacionServicio;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * HU-09: eliminar una ubicación con confirmación.
 * GET muestra la confirmación (nunca elimina); POST elimina y vuelve al listado.
 * No se elimina una ubicación con niveles inferiores, sismos o estaciones asociados.
 */
@WebServlet(name = "UbicacionEliminarServlet", urlPatterns = "/ubicaciones/eliminar")
public class UbicacionEliminarServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UbicacionServicio servicio = new UbicacionServicio(getServletContext());
        Ubicacion ubicacion = buscar(servicio, request);
        if (ubicacion == null) {
            noEncontrado(request, response);
            return;
        }
        mostrarConfirmacion(request, response, ubicacion, servicio.motivoParaNoEliminar(ubicacion));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UbicacionServicio servicio = new UbicacionServicio(getServletContext());
        Ubicacion ubicacion = buscar(servicio, request);
        if (ubicacion == null) {
            noEncontrado(request, response);
            return;
        }

        // La regla se vuelve a validar en el servidor antes de eliminar.
        String motivo = servicio.motivoParaNoEliminar(ubicacion);
        if (motivo != null) {
            mostrarConfirmacion(request, response, ubicacion, motivo);
            return;
        }

        try {
            servicio.eliminar(ubicacion);
        } catch (PersistenciaException ex) {
            mostrarConfirmacion(request, response, ubicacion, ex.getMessage());
            return;
        }

        TipoUbicacion tipo = ubicacion.getTipo();
        response.sendRedirect(request.getContextPath() + UbicacionServicio.urlListado(tipo, ubicacion.getPadre())
                + (tipo == TipoUbicacion.DEPARTAMENTO ? "?" : "&")
                + "eliminado=" + URLEncoder.encode(ubicacion.getNombre(), StandardCharsets.UTF_8));
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

    private void mostrarConfirmacion(HttpServletRequest request, HttpServletResponse response,
                                     Ubicacion ubicacion, String motivoBloqueo)
            throws ServletException, IOException {
        request.setAttribute("ubicacion", ubicacion);
        request.setAttribute("tipo", ubicacion.getTipo());
        request.setAttribute("motivoBloqueo", motivoBloqueo);
        request.setAttribute("urlVolver", UbicacionServicio.urlListado(ubicacion.getTipo(), ubicacion.getPadre()));
        request.getRequestDispatcher("/WEB-INF/views/ubicaciones/eliminar.jsp").forward(request, response);
    }

    private void noEncontrado(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        request.setAttribute("mensajeError", "No existe la ubicación solicitada.");
        request.getRequestDispatcher("/WEB-INF/views/error/404.jsp").forward(request, response);
    }
}
