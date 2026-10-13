package com.grupo6.sistema.servlet;

import com.grupo6.sistema.listener.AplicacionListener;
import com.grupo6.sistema.model.Sismo;
import com.grupo6.sistema.repository.DistritoRepository;
import com.grupo6.sistema.repository.EstacionMonitoreoRepository;
import com.grupo6.sistema.repository.PersistenciaException;
import com.grupo6.sistema.repository.SismoRepository;
import com.grupo6.sistema.validacion.SismoFormulario;
import com.grupo6.sistema.validacion.SismoValidador;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * HU-02: registrar un nuevo sismo.
 * GET muestra el formulario vacío; POST valida en el servidor (SismoValidador), guarda el sismo
 * en MySQL con estado Registrado y redirige al detalle (Post/Redirect/Get).
 */
@WebServlet(name = "SismoNuevoServlet", urlPatterns = "/sismos/nuevo")
public class SismoNuevoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SismoFormulario formulario = new SismoFormulario();
        formulario.setEstado(Sismo.ESTADO_REGISTRADO);
        mostrarFormulario(request, response, formulario, Map.of());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding(StandardCharsets.UTF_8.name());

        SismoFormulario formulario = SismoFormulario.desdeRequest(request);
        formulario.setEstado(Sismo.ESTADO_REGISTRADO); // HU-08: todo sismo nuevo nace como Registrado

        SismoValidador validador = new SismoValidador(repositorioSismos(), repositorioDistritos(),
                repositorioEstaciones());
        Map<String, String> errores = validador.validar(formulario, null);

        if (!errores.isEmpty()) {
            mostrarFormulario(request, response, formulario, errores);
            return;
        }

        Sismo sismo = new Sismo();
        formulario.copiarEn(sismo);
        sismo.setEstado(Sismo.ESTADO_REGISTRADO);
        try {
            Sismo creado = repositorioSismos().agregar(sismo);
            response.sendRedirect(request.getContextPath()
                    + "/sismos/detalle?id=" + creado.getId()
                    + "&creado=1");
        } catch (PersistenciaException ex) {
            mostrarFormulario(request, response, formulario, Map.of("general", ex.getMessage()));
        }
    }

    private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response,
                                   SismoFormulario formulario, Map<String, String> errores)
            throws ServletException, IOException {
        request.setAttribute("modo", "nuevo");
        request.setAttribute("formulario", formulario);
        request.setAttribute("errores", errores);
        request.setAttribute("distritos", repositorioDistritos().listar());
        request.setAttribute("estaciones", repositorioEstaciones().listar());
        request.setAttribute("intensidades", Sismo.INTENSIDADES);
        request.setAttribute("estados", Sismo.ESTADOS_VALIDOS);
        request.getRequestDispatcher("/WEB-INF/views/sismos/formulario.jsp")
                .forward(request, response);
    }

    private SismoRepository repositorioSismos() {
        return (SismoRepository) getServletContext()
                .getAttribute(AplicacionListener.REPOSITORIO_SISMOS);
    }

    private DistritoRepository repositorioDistritos() {
        return (DistritoRepository) getServletContext()
                .getAttribute(AplicacionListener.REPOSITORIO_DISTRITOS);
    }

    private EstacionMonitoreoRepository repositorioEstaciones() {
        return (EstacionMonitoreoRepository) getServletContext()
                .getAttribute(AplicacionListener.REPOSITORIO_ESTACIONES);
    }
}
