package com.grupo6.sistema.servlet;

import com.grupo6.sistema.listener.AplicacionListener;
import com.grupo6.sistema.model.Departamento;
import com.grupo6.sistema.model.Provincia;
import com.grupo6.sistema.model.TipoUbicacion;
import com.grupo6.sistema.repository.DepartamentoRepository;
import com.grupo6.sistema.repository.DistritoRepository;
import com.grupo6.sistema.repository.ProvinciaRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

/**
 * HU-09: consulta del catálogo de ubicaciones, nivel por nivel.
 * GET /ubicaciones                     -> departamentos
 * GET /ubicaciones?departamentoId=X    -> provincias del departamento
 * GET /ubicaciones?provinciaId=Y       -> distritos de la provincia
 */
@WebServlet(name = "UbicacionListarServlet", urlPatterns = "/ubicaciones")
public class UbicacionListarServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long provinciaId = leerId(request.getParameter("provinciaId"));
        Long departamentoId = leerId(request.getParameter("departamentoId"));

        if (provinciaId != null) {
            Optional<Provincia> provincia = provincias().buscarPorId(provinciaId);
            if (provincia.isEmpty()) {
                noEncontrado(request, response, "No existe la provincia solicitada.");
                return;
            }
            request.setAttribute("nivel", TipoUbicacion.DISTRITO);
            request.setAttribute("padre", provincia.get());
            request.setAttribute("departamento", provincia.get().getDepartamento());
            request.setAttribute("provincia", provincia.get());
            request.setAttribute("ubicaciones", distritos().listarPorProvincia(provinciaId));
            request.setAttribute("conteos", distritos().contarSismosPorDistrito(provinciaId));
            request.setAttribute("etiquetaConteo", "Sismos");
        } else if (departamentoId != null) {
            Optional<Departamento> departamento = departamentos().buscarPorId(departamentoId);
            if (departamento.isEmpty()) {
                noEncontrado(request, response, "No existe el departamento solicitado.");
                return;
            }
            request.setAttribute("nivel", TipoUbicacion.PROVINCIA);
            request.setAttribute("padre", departamento.get());
            request.setAttribute("departamento", departamento.get());
            request.setAttribute("ubicaciones", provincias().listarPorDepartamento(departamentoId));
            request.setAttribute("conteos", provincias().contarDistritosPorProvincia(departamentoId));
            request.setAttribute("etiquetaConteo", "Distritos");
        } else {
            request.setAttribute("nivel", TipoUbicacion.DEPARTAMENTO);
            request.setAttribute("ubicaciones", departamentos().listar());
            request.setAttribute("conteos", departamentos().contarProvinciasPorDepartamento());
            request.setAttribute("etiquetaConteo", "Provincias");
        }

        request.getRequestDispatcher("/WEB-INF/views/ubicaciones/lista.jsp").forward(request, response);
    }

    private void noEncontrado(HttpServletRequest request, HttpServletResponse response, String mensaje)
            throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        request.setAttribute("mensajeError", mensaje);
        request.getRequestDispatcher("/WEB-INF/views/error/404.jsp").forward(request, response);
    }

    private static Long leerId(String texto) {
        try {
            return texto == null || texto.isBlank() ? null : Long.valueOf(texto.trim());
        } catch (NumberFormatException ex) {
            return -1L;
        }
    }

    private DepartamentoRepository departamentos() {
        return (DepartamentoRepository) getServletContext().getAttribute(AplicacionListener.REPOSITORIO_DEPARTAMENTOS);
    }

    private ProvinciaRepository provincias() {
        return (ProvinciaRepository) getServletContext().getAttribute(AplicacionListener.REPOSITORIO_PROVINCIAS);
    }

    private DistritoRepository distritos() {
        return (DistritoRepository) getServletContext().getAttribute(AplicacionListener.REPOSITORIO_DISTRITOS);
    }
}
