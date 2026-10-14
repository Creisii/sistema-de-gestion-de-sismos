package com.grupo6.sistema.servlet;

import com.grupo6.sistema.listener.AplicacionListener;
import com.grupo6.sistema.model.EstacionMonitoreo;
import com.grupo6.sistema.repository.DistritoRepository;
import com.grupo6.sistema.repository.EstacionMonitoreoRepository;
import com.grupo6.sistema.validacion.EstacionFormulario;
import com.grupo6.sistema.validacion.EstacionValidador;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

/**
 * HU-10: editar una estación, incluido el cambio de estado Activa/Inactiva.
 * GET carga el formulario con los datos actuales; POST valida, actualiza y redirige
 * al listado (Post/Redirect/Get).
 */
@WebServlet(name = "EstacionEditarServlet", urlPatterns = "/estaciones/editar")
public class EstacionEditarServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Optional<EstacionMonitoreo> encontrada = buscar(request.getParameter("id"));
        if (encontrada.isEmpty()) {
            mostrarNoEncontrado(request, response);
            return;
        }
        EstacionMonitoreo estacion = encontrada.get();
        mostrarFormulario(request, response, estacion.getId(),
                EstacionFormulario.desdeEstacion(estacion), Map.of());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding(StandardCharsets.UTF_8.name());

        Optional<EstacionMonitoreo> encontrada = buscar(request.getParameter("id"));
        if (encontrada.isEmpty()) {
            mostrarNoEncontrado(request, response);
            return;
        }
        EstacionMonitoreo estacion = encontrada.get();

        EstacionFormulario formulario = EstacionFormulario.desdeRequest(request);

        EstacionValidador validador = new EstacionValidador(repositorioEstaciones(), repositorioDistritos());
        Map<String, String> errores = validador.validar(formulario, estacion);

        if (!errores.isEmpty()) {
            mostrarFormulario(request, response, estacion.getId(), formulario, errores);
            return;
        }

        formulario.copiarEn(estacion);
        repositorioEstaciones().actualizar(estacion);

        response.sendRedirect(request.getContextPath()
                + "/estaciones?actualizado=" + estacion.getCodigo());
    }

    private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response,
                                   long estacionId, EstacionFormulario formulario, Map<String, String> errores)
            throws ServletException, IOException {
        request.setAttribute("modo", "editar");
        request.setAttribute("estacionId", estacionId);
        request.setAttribute("formulario", formulario);
        request.setAttribute("errores", errores);
        request.setAttribute("distritos", repositorioDistritos().listar());
        request.setAttribute("estados", EstacionMonitoreo.ESTADOS_VALIDOS);
        request.getRequestDispatcher("/WEB-INF/views/estaciones/formulario.jsp")
                .forward(request, response);
    }

    private void mostrarNoEncontrado(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        request.setAttribute("mensajeError", "No existe una estación con el identificador solicitado.");
        request.getRequestDispatcher("/WEB-INF/views/error/404.jsp").forward(request, response);
    }

    private Optional<EstacionMonitoreo> buscar(String idTexto) {
        try {
            return repositorioEstaciones().buscarPorId(Long.parseLong(idTexto));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    private EstacionMonitoreoRepository repositorioEstaciones() {
        return (EstacionMonitoreoRepository) getServletContext()
                .getAttribute(AplicacionListener.REPOSITORIO_ESTACIONES);
    }

    private DistritoRepository repositorioDistritos() {
        return (DistritoRepository) getServletContext()
                .getAttribute(AplicacionListener.REPOSITORIO_DISTRITOS);
    }
}