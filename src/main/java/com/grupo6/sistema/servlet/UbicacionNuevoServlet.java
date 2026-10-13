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
 * HU-09: registrar un departamento, una provincia o un distrito.
 * GET  /ubicaciones/nuevo?tipo=provincia&padreId=2  -> formulario
 * POST /ubicaciones/nuevo                           -> valida, guarda y vuelve al listado (PRG)
 */
@WebServlet(name = "UbicacionNuevoServlet", urlPatterns = "/ubicaciones/nuevo")
public class UbicacionNuevoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        TipoUbicacion tipo = TipoUbicacion.desdeClave(request.getParameter("tipo"));
        UbicacionServicio servicio = new UbicacionServicio(getServletContext());
        Ubicacion padre = buscarPadre(servicio, tipo, request.getParameter("padreId"));
        if (tipo == null || (tipo.getPadre() != null && padre == null)) {
            noEncontrado(request, response);
            return;
        }
        mostrarFormulario(request, response, UbicacionFormulario.nuevo(tipo, padre), padre, Map.of());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());

        TipoUbicacion tipo = TipoUbicacion.desdeClave(request.getParameter("tipo"));
        UbicacionServicio servicio = new UbicacionServicio(getServletContext());
        Ubicacion padre = buscarPadre(servicio, tipo, request.getParameter("padreId"));
        if (tipo == null || (tipo.getPadre() != null && padre == null)) {
            noEncontrado(request, response);
            return;
        }

        UbicacionFormulario formulario = UbicacionFormulario.desdeRequest(request, tipo);
        Map<String, String> errores = servicio.validar(formulario, padre, null);
        if (!errores.isEmpty()) {
            mostrarFormulario(request, response, formulario, padre, errores);
            return;
        }

        try {
            Ubicacion creada = servicio.registrar(formulario, padre);
            response.sendRedirect(request.getContextPath() + UbicacionServicio.urlListado(tipo, padre)
                    + (tipo == TipoUbicacion.DEPARTAMENTO ? "?" : "&")
                    + "creado=" + URLEncoder.encode(creada.getNombre(), StandardCharsets.UTF_8));
        } catch (PersistenciaException ex) {
            mostrarFormulario(request, response, formulario, padre, Map.of("general", ex.getMessage()));
        }
    }

    private Ubicacion buscarPadre(UbicacionServicio servicio, TipoUbicacion tipo, String padreId) {
        if (tipo == null || tipo.getPadre() == null) {
            return null;
        }
        try {
            return servicio.buscar(tipo.getPadre(), Long.parseLong(padreId)).orElse(null);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response,
                                   UbicacionFormulario formulario, Ubicacion padre, Map<String, String> errores)
            throws ServletException, IOException {
        request.setAttribute("modo", "nuevo");
        request.setAttribute("tipo", formulario.getTipo());
        request.setAttribute("formulario", formulario);
        request.setAttribute("padre", padre);
        request.setAttribute("errores", errores);
        request.setAttribute("urlVolver", UbicacionServicio.urlListado(formulario.getTipo(), padre));
        request.getRequestDispatcher("/WEB-INF/views/ubicaciones/formulario.jsp").forward(request, response);
    }

    private void noEncontrado(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        request.setAttribute("mensajeError", "La ubicación de nivel superior no existe.");
        request.getRequestDispatcher("/WEB-INF/views/error/404.jsp").forward(request, response);
    }
}
