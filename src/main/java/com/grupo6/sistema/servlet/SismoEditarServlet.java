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
import java.util.Optional;

/**
 * HU-06: editar un sismo. HU-08: el estado solo se cambia desde aquí.
 * GET carga el formulario con los datos actuales; POST valida con las mismas reglas del
 * registro (SismoValidador), actualiza en MySQL y redirige al detalle (Post/Redirect/Get).
 */
@WebServlet(name = "SismoEditarServlet", urlPatterns = "/sismos/editar")
public class SismoEditarServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Optional<Sismo> encontrado = buscar(request.getParameter("id"));
        if (encontrado.isEmpty()) {
            mostrarNoEncontrado(request, response);
            return;
        }

        Sismo sismo = encontrado.get();
        mostrarFormulario(request, response, sismo.getId(), SismoFormulario.desdeSismo(sismo), Map.of());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding(StandardCharsets.UTF_8.name());

        Optional<Sismo> encontrado = buscar(request.getParameter("id"));
        if (encontrado.isEmpty()) {
            mostrarNoEncontrado(request, response);
            return;
        }
        Sismo sismo = encontrado.get();

        SismoFormulario formulario = SismoFormulario.desdeRequest(request);

        // Mismas validaciones que en el registro. Al pasar el sismo actual:
        // - el código único no se compara consigo mismo,
        // - se valida que el estado sea uno de los 4 permitidos (HU-08).
        SismoValidador validador = new SismoValidador(repositorioSismos(), repositorioDistritos(),
                repositorioEstaciones());
        Map<String, String> errores = validador.validar(formulario, sismo);

        if (!errores.isEmpty()) {
            mostrarFormulario(request, response, sismo.getId(), formulario, errores);
            return;
        }

        formulario.copiarEn(sismo);
        try {
            repositorioSismos().actualizar(sismo);
            response.sendRedirect(request.getContextPath()
                    + "/sismos/detalle?id=" + sismo.getId()
                    + "&actualizado=1");
        } catch (PersistenciaException ex) {
            mostrarFormulario(request, response, sismo.getId(), formulario, Map.of("general", ex.getMessage()));
        }
    }

    private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response,
                                   long sismoId, SismoFormulario formulario, Map<String, String> errores)
            throws ServletException, IOException {
        request.setAttribute("modo", "editar");
        request.setAttribute("sismoId", sismoId);
        request.setAttribute("formulario", formulario);
        request.setAttribute("errores", errores);
        request.setAttribute("distritos", repositorioDistritos().listar());
        request.setAttribute("estaciones", repositorioEstaciones().listar());
        request.setAttribute("intensidades", Sismo.INTENSIDADES);
        request.setAttribute("estados", Sismo.ESTADOS_VALIDOS);
        request.getRequestDispatcher("/WEB-INF/views/sismos/formulario.jsp")
                .forward(request, response);
    }

    private void mostrarNoEncontrado(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        request.setAttribute("mensajeError", "No existe un sismo con el identificador solicitado.");
        request.getRequestDispatcher("/WEB-INF/views/error/404.jsp").forward(request, response);
    }

    private Optional<Sismo> buscar(String idTexto) {
        try {
            long id = Long.parseLong(idTexto);
            return repositorioSismos().buscarPorId(id);
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
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
