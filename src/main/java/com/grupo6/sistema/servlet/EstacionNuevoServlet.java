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

/**
 * HU-10: registrar una nueva estación de monitoreo.
 * GET muestra el formulario vacío; POST valida en el servidor (EstacionValidador),
 * registra la estación y redirige al listado (Post/Redirect/Get).
 */
@WebServlet(name = "EstacionNuevoServlet", urlPatterns = "/estaciones/nuevo")
public class EstacionNuevoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        mostrarFormulario(request, response, EstacionFormulario.nuevo(), Map.of());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding(StandardCharsets.UTF_8.name());

        EstacionFormulario formulario = EstacionFormulario.desdeRequest(request);

        EstacionValidador validador = new EstacionValidador(repositorioEstaciones(), repositorioDistritos());
        Map<String, String> errores = validador.validar(formulario, null);

        if (!errores.isEmpty()) {
            mostrarFormulario(request, response, formulario, errores);
            return;
        }

        EstacionMonitoreo estacion = new EstacionMonitoreo();
        formulario.copiarEn(estacion);
        EstacionMonitoreo creada = repositorioEstaciones().agregar(estacion);

        response.sendRedirect(request.getContextPath()
                + "/estaciones?creado=" + creada.getCodigo());
    }

    private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response,
                                   EstacionFormulario formulario, Map<String, String> errores)
            throws ServletException, IOException {
        request.setAttribute("modo", "nuevo");
        request.setAttribute("formulario", formulario);
        request.setAttribute("errores", errores);
        request.setAttribute("distritos", repositorioDistritos().listar());
        request.setAttribute("estados", EstacionMonitoreo.ESTADOS_VALIDOS);
        request.getRequestDispatcher("/WEB-INF/views/estaciones/formulario.jsp")
                .forward(request, response);
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