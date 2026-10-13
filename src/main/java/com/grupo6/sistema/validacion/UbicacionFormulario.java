package com.grupo6.sistema.validacion;

import com.grupo6.sistema.model.TipoUbicacion;
import com.grupo6.sistema.model.Ubicacion;
import jakarta.servlet.http.HttpServletRequest;

/** Valores del formulario de ubicaciones (HU-09), tal como los escribió el usuario. */
public class UbicacionFormulario {

    private TipoUbicacion tipo;
    private String codigo = "";
    private String nombre = "";

    public static UbicacionFormulario desdeRequest(HttpServletRequest request, TipoUbicacion tipo) {
        UbicacionFormulario f = new UbicacionFormulario();
        f.tipo = tipo;
        f.codigo = limpiar(request.getParameter("codigo"));
        f.nombre = limpiar(request.getParameter("nombre")).replaceAll("\\s+", " ");
        return f;
    }

    public static UbicacionFormulario desdeUbicacion(Ubicacion ubicacion) {
        UbicacionFormulario f = new UbicacionFormulario();
        f.tipo = ubicacion.getTipo();
        f.codigo = ubicacion.getCodigo();
        f.nombre = ubicacion.getNombre();
        return f;
    }

    /** Formulario vacío; para provincias y distritos el código empieza con el del padre. */
    public static UbicacionFormulario nuevo(TipoUbicacion tipo, Ubicacion padre) {
        UbicacionFormulario f = new UbicacionFormulario();
        f.tipo = tipo;
        f.codigo = padre == null ? "" : padre.getCodigo();
        return f;
    }

    private static String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    public TipoUbicacion getTipo() { return tipo; }
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
}
