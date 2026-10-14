package com.grupo6.sistema.validacion;

import com.grupo6.sistema.model.Distrito;
import com.grupo6.sistema.model.EstacionMonitoreo;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;

/**
 * Guarda los valores del formulario de estación tal como los escribió el usuario (como texto).
 * Se usa igual en Nuevo y en Editar (HU-10).
 */
public class EstacionFormulario {

    private String codigo = "";
    private String nombre = "";
    private String distritoId = "";
    private String latitud = "";
    private String longitud = "";
    private String fechaInstalacion = "";
    private String estado = "";

    public static EstacionFormulario desdeRequest(HttpServletRequest request) {
        EstacionFormulario f = new EstacionFormulario();
        f.codigo = limpiar(request.getParameter("codigo")).toUpperCase();
        f.nombre = limpiar(request.getParameter("nombre"));
        f.distritoId = limpiar(request.getParameter("distritoId"));
        f.latitud = limpiar(request.getParameter("latitud"));
        f.longitud = limpiar(request.getParameter("longitud"));
        f.fechaInstalacion = limpiar(request.getParameter("fechaInstalacion"));
        f.estado = limpiar(request.getParameter("estado"));
        return f;
    }

    /** Copia los datos de una estación existente (para precargar Editar). */
    public static EstacionFormulario desdeEstacion(EstacionMonitoreo estacion) {
        EstacionFormulario f = new EstacionFormulario();
        f.codigo = estacion.getCodigo();
        f.nombre = estacion.getNombre();
        f.distritoId = estacion.getDistrito() == null ? "" : String.valueOf(estacion.getDistrito().getId());
        f.latitud = String.valueOf(estacion.getLatitud());
        f.longitud = String.valueOf(estacion.getLongitud());
        f.fechaInstalacion = estacion.getFechaInstalacion() == null ? "" : estacion.getFechaInstalacion().toString();
        f.estado = texto(estacion.getEstado());
        return f;
    }

    /** Formulario vacío para Nuevo: estado Activa por defecto. */
    public static EstacionFormulario nuevo() {
        EstacionFormulario f = new EstacionFormulario();
        f.estado = EstacionMonitoreo.ESTADO_ACTIVA;
        return f;
    }

    /**
     * Pasa los valores al objeto EstacionMonitoreo. Solo debe llamarse cuando EstacionValidador
     * no encontró errores. El distrito se asigna solo con su id; el repositorio lo reemplaza
     * por la entidad administrada al guardar.
     */
    public void copiarEn(EstacionMonitoreo estacion) {
        estacion.setCodigo(codigo);
        estacion.setNombre(nombre);
        estacion.setDistrito(Distrito.conId(Long.valueOf(distritoId)));
        estacion.setLatitud(EstacionValidador.aNumero(latitud));
        estacion.setLongitud(EstacionValidador.aNumero(longitud));
        estacion.setFechaInstalacion(LocalDate.parse(fechaInstalacion));
        estacion.setEstado(estado);
    }

    private static String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private static String texto(String valor) {
        return valor == null ? "" : valor;
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getDistritoId() { return distritoId; }
    public String getLatitud() { return latitud; }
    public String getLongitud() { return longitud; }
    public String getFechaInstalacion() { return fechaInstalacion; }
    public String getEstado() { return estado; }
}