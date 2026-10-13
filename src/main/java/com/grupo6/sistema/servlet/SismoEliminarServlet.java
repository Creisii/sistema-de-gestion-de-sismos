package com.grupo6.sistema.servlet;

import com.grupo6.sistema.listener.AplicacionListener;
import com.grupo6.sistema.model.Sismo;
import com.grupo6.sistema.repository.PersistenciaException;
import com.grupo6.sistema.repository.SismoRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * HU-07: eliminar un sismo con confirmación.
 * GET solo muestra la pantalla de confirmación (nunca elimina).
 * POST elimina y redirige a la lista (Post/Redirect/Get).
 * Regla: un sismo En seguimiento no se puede eliminar.
 */
@WebServlet(name = "SismoEliminarServlet", urlPatterns = "/sismos/eliminar")
public class SismoEliminarServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Optional<Sismo> encontrado = buscar(request.getParameter("id"));
        if (encontrado.isEmpty()) {
            mostrarNoEncontrado(request, response);
            return;
        }
        mostrarConfirmacion(request, response, encontrado.get());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Optional<Sismo> encontrado = buscar(request.getParameter("id"));
        if (encontrado.isEmpty()) {
            mostrarNoEncontrado(request, response);
            return;
        }
        Sismo sismo = encontrado.get();

        // La regla se valida también en el servidor, aunque el botón esté deshabilitado en la vista.
        if (estaBloqueado(sismo)) {
            mostrarConfirmacion(request, response, sismo);
            return;
        }

        try {
            repositorio().eliminar(sismo.getId());
        } catch (PersistenciaException ex) {
            request.setAttribute("motivoBloqueo", ex.getMessage());
            mostrarConfirmacion(request, response, sismo);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/sismos?eliminado="
                + URLEncoder.encode(sismo.getCodigo(), StandardCharsets.UTF_8));
    }

    private boolean estaBloqueado(Sismo sismo) {
        return sismo.isEnSeguimiento();
    }

    private void mostrarConfirmacion(HttpServletRequest request, HttpServletResponse response, Sismo sismo)
            throws ServletException, IOException {
        request.setAttribute("sismo", sismo);
        if (estaBloqueado(sismo)) {
            request.setAttribute("motivoBloqueo",
                    "El sismo " + sismo.getCodigo() + " está En seguimiento y no puede eliminarse. "
                            + "Cambie su estado a Cerrado si ya no requiere seguimiento.");
        }
        request.getRequestDispatcher("/WEB-INF/views/sismos/eliminar.jsp").forward(request, response);
    }

    private void mostrarNoEncontrado(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        request.setAttribute("mensajeError", "No existe un sismo con el identificador solicitado.");
        request.getRequestDispatcher("/WEB-INF/views/error/404.jsp").forward(request, response);
    }

    private Optional<Sismo> buscar(String idTexto) {
        try {
            return repositorio().buscarPorId(Long.parseLong(idTexto));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    private SismoRepository repositorio() {
        return (SismoRepository) getServletContext()
                .getAttribute(AplicacionListener.REPOSITORIO_SISMOS);
    }
}
