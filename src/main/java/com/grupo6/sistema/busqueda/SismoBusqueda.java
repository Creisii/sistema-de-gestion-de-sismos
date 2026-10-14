package com.grupo6.sistema.busqueda;

import jakarta.servlet.http.HttpServletRequest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Criterios de búsqueda de sismos (HU-15): filtros, orden y página.
 * Se arma con los parámetros de la URL (GET /sismos?...), así una búsqueda se puede recargar,
 * compartir o paginar sin perder los filtros.
 *
 * Para agregar un filtro nuevo (por ejemplo HU-30 estado o HU-31 texto):
 * 1. Agregar el campo con su getter y leerlo en desdeRequest().
 * 2. Incluirlo en parametrosFiltro() para que se mantenga al paginar y ordenar.
 * 3. Agregar su condición JPQL en SismoRepository.agregarCondiciones().
 */
public class SismoBusqueda {

    public static final int TAMANIO_PAGINA = 10;

    /** Columnas por las que se puede ordenar (clave de la URL). */
    public static final List<String> ORDENES_VALIDOS =
            List.of("fecha", "codigo", "magnitud", "profundidad", "departamento", "estado");

    // Valores tal como se escribieron (para volver a mostrarlos en el formulario)
    private String departamentoIdTexto = "";
    private String terminoTexto = "";
    private String magnitudMinTexto = "";
    private String magnitudMaxTexto = "";
    private String desdeTexto = "";
    private String hastaTexto = "";
    private String estado ="";

    // Valores ya validados (null = filtro no aplicado)
    private Long departamentoId;
    private Double magnitudMin;
    private Double magnitudMax;
    private LocalDate desde;
    private LocalDate hasta;

    private String orden = "fecha";
    private boolean descendente = true;
    private int pagina = 1;

    private final List<String> errores = new ArrayList<>();

    public static SismoBusqueda desdeRequest(HttpServletRequest request) {
        SismoBusqueda b = new SismoBusqueda();

        b.terminoTexto = limpiar(request.getParameter("buscar"));

        b.departamentoIdTexto = limpiar(request.getParameter("departamentoId"));
        if (!b.departamentoIdTexto.isEmpty()) {
            try {
                b.departamentoId = Long.valueOf(b.departamentoIdTexto);
            } catch (NumberFormatException ex) {
                b.errores.add("El departamento seleccionado no es válido.");
            }
        }

        b.magnitudMinTexto = limpiar(request.getParameter("magnitudMin"));
        b.magnitudMaxTexto = limpiar(request.getParameter("magnitudMax"));
        b.magnitudMin = leerMagnitud(b.magnitudMinTexto, "mínima", b.errores);
        b.magnitudMax = leerMagnitud(b.magnitudMaxTexto, "máxima", b.errores);
        if (b.magnitudMin != null && b.magnitudMax != null && b.magnitudMin > b.magnitudMax) {
            b.errores.add("La magnitud mínima no puede ser mayor que la máxima.");
            b.magnitudMin = null;
            b.magnitudMax = null;
        }

        b.desdeTexto = limpiar(request.getParameter("desde"));
        b.hastaTexto = limpiar(request.getParameter("hasta"));
        b.desde = leerFecha(b.desdeTexto, "inicial", b.errores);
        b.hasta = leerFecha(b.hastaTexto, "final", b.errores);
        if (b.desde != null && b.hasta != null && b.desde.isAfter(b.hasta)) {
            b.errores.add("La fecha inicial no puede ser posterior a la fecha final.");
            b.desde = null;
            b.hasta = null;
        }
        
        b.estado = limpiar(request.getParameter("estado"));

        String orden = limpiar(request.getParameter("orden"));
        if (ORDENES_VALIDOS.contains(orden)) {
            b.orden = orden;
        }
        String direccion = limpiar(request.getParameter("dir"));
        if (!direccion.isEmpty()) {
            b.descendente = !"asc".equalsIgnoreCase(direccion);
        } else {
            // Por defecto: fechas y magnitudes de mayor a menor; textos de A a Z
            b.descendente = b.orden.equals("fecha") || b.orden.equals("magnitud") || b.orden.equals("profundidad");
        }

        try {
            b.pagina = Math.max(1, Integer.parseInt(limpiar(request.getParameter("pagina"))));
        } catch (NumberFormatException ex) {
            b.pagina = 1;
        }
        return b;
    }

    public boolean isTieneFiltros() {
        return !terminoTexto.isEmpty() || departamentoId != null || magnitudMin != null || magnitudMax != null
                || desde != null || hasta != null || !estado.isEmpty();
    }

    /** Filtros actuales como parámetros de URL (sin orden ni página). */
    private Map<String, String> parametrosFiltro() {
        Map<String, String> p = new LinkedHashMap<>();
        p.put("buscar", terminoTexto);
        p.put("departamentoId", departamentoIdTexto);
        p.put("magnitudMin", magnitudMinTexto);
        p.put("magnitudMax", magnitudMaxTexto);
        p.put("desde", desdeTexto);
        p.put("hasta", hastaTexto);
        p.put("estado",estado);
        return p;
    }

    /** Query string para ir a otra página manteniendo filtros y orden. */
    public String urlPagina(int numero) {
        Map<String, String> p = parametrosFiltro();
        p.put("orden", orden);
        p.put("dir", descendente ? "desc" : "asc");
        p.put("pagina", String.valueOf(numero));
        return construir(p);
    }

    /** Query string para ordenar por una columna; si ya está ordenada por ella, invierte el sentido. */
    public String urlOrden(String columna) {
        Map<String, String> p = parametrosFiltro();
        boolean desc = columna.equals(orden) ? !descendente
                : columna.equals("fecha") || columna.equals("magnitud") || columna.equals("profundidad");
        p.put("orden", columna);
        p.put("dir", desc ? "desc" : "asc");
        return construir(p);
    }

    /** Flecha para la cabecera de la columna ordenada. */
    public String indicadorOrden(String columna) {
        if (!columna.equals(orden)) {
            return "";
        }
        return descendente ? " ▼" : " ▲";
    }

    private static String construir(Map<String, String> parametros) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> e : parametros.entrySet()) {
            if (e.getValue() == null || e.getValue().isEmpty()) {
                continue;
            }
            sb.append(sb.length() == 0 ? "?" : "&")
                    .append(e.getKey()).append('=')
                    .append(URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8));
        }
        return sb.length() == 0 ? "?" : sb.toString();
    }

    private static Double leerMagnitud(String texto, String nombre, List<String> errores) {
        if (texto.isEmpty()) {
            return null;
        }
        try {
            double valor = Double.parseDouble(texto.replace(',', '.'));
            if (valor < 0 || valor > 10 || Double.isNaN(valor)) {
                errores.add("La magnitud " + nombre + " debe estar entre 0 y 10.");
                return null;
            }
            return valor;
        } catch (NumberFormatException ex) {
            errores.add("La magnitud " + nombre + " debe ser un número.");
            return null;
        }
    }

    private static LocalDate leerFecha(String texto, String nombre, List<String> errores) {
        if (texto.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(texto);
        } catch (DateTimeParseException ex) {
            errores.add("La fecha " + nombre + " no es válida.");
            return null;
        }
    }

    private static String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    public String getDepartamentoIdTexto() { return departamentoIdTexto; }
    public String getTerminoTexto() { return terminoTexto; }
    public String getMagnitudMinTexto() { return magnitudMinTexto; }
    public String getMagnitudMaxTexto() { return magnitudMaxTexto; }
    public String getDesdeTexto() { return desdeTexto; }
    public String getHastaTexto() { return hastaTexto; }
    public String getEstado() { return estado; }
    public Long getDepartamentoId() { return departamentoId; }
    public Double getMagnitudMin() { return magnitudMin; }
    public Double getMagnitudMax() { return magnitudMax; }
    public LocalDate getDesde() { return desde; }
    public LocalDate getHasta() { return hasta; }
    public String getOrden() { return orden; }
    public boolean isDescendente() { return descendente; }
    public int getPagina() { return pagina; }
    public void setPagina(int pagina) { this.pagina = pagina; }
    public int getTamanio() { return TAMANIO_PAGINA; }
    public List<String> getErrores() { return errores; }
}
