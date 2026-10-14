package com.grupo6.sistema.validacion;

import com.grupo6.sistema.model.EstacionMonitoreo;
import com.grupo6.sistema.repository.DistritoRepository;
import com.grupo6.sistema.repository.EstacionMonitoreoRepository;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Validaciones de la estación de monitoreo en el servidor (HU-10).
 * - Campos obligatorios y código único.
 * - El distrito debe existir en el catálogo (HU-09).
 * - Coordenadas dentro de rango y fecha de instalación no futura.
 * - Estado: solo Activa o Inactiva.
 */
public class EstacionValidador {

    private final EstacionMonitoreoRepository estaciones;
    private final DistritoRepository distritos;

    public EstacionValidador(EstacionMonitoreoRepository estaciones, DistritoRepository distritos) {
        this.estaciones = estaciones;
        this.distritos = distritos;
    }

    /**
     * @param f              datos escritos en el formulario
     * @param estacionActual null al registrar; la estación que se está editando al editar
     */
    public Map<String, String> validar(EstacionFormulario f, EstacionMonitoreo estacionActual) {
        Map<String, String> errores = new LinkedHashMap<>();
        boolean esNueva = estacionActual == null;

        obligatorio(errores, "codigo", f.getCodigo(), "El código es obligatorio.");
        obligatorio(errores, "nombre", f.getNombre(), "El nombre es obligatorio.");
        obligatorio(errores, "distritoId", f.getDistritoId(), "El distrito es obligatorio.");
        obligatorio(errores, "latitud", f.getLatitud(), "La latitud es obligatoria.");
        obligatorio(errores, "longitud", f.getLongitud(), "La longitud es obligatoria.");
        obligatorio(errores, "fechaInstalacion", f.getFechaInstalacion(), "La fecha de instalación es obligatoria.");

        if (!errores.containsKey("codigo") && f.getCodigo().length() > 10) {
            errores.put("codigo", "El código no puede superar los 10 caracteres.");
        }

        // Código único
        if (!errores.containsKey("codigo")) {
            boolean repetido = estaciones.buscarPorCodigo(f.getCodigo())
                    .map(e -> esNueva || !e.getId().equals(estacionActual.getId()))
                    .orElse(false);
            if (repetido) {
                errores.put("codigo", "El código " + f.getCodigo() + " ya está registrado.");
            }
        }

        // Distrito existente en el catálogo (HU-09)
        if (!errores.containsKey("distritoId")) {
            Long distritoId = aId(f.getDistritoId());
            if (distritoId == null || distritos.buscarPorId(distritoId).isEmpty()) {
                errores.put("distritoId", "El distrito seleccionado no existe en el catálogo.");
            }
        }

        rango(errores, "latitud", f.getLatitud(), "La latitud", -90, true, 90.0, "La latitud debe estar entre -90 y 90.");
        rango(errores, "longitud", f.getLongitud(), "La longitud", -180, true, 180.0, "La longitud debe estar entre -180 y 180.");

        // Fecha de instalación no futura
        if (!errores.containsKey("fechaInstalacion")) {
            try {
                LocalDate fecha = LocalDate.parse(f.getFechaInstalacion());
                if (fecha.isAfter(LocalDate.now())) {
                    errores.put("fechaInstalacion", "La fecha de instalación no puede ser futura.");
                }
            } catch (DateTimeParseException ex) {
                errores.put("fechaInstalacion", "El formato de fecha no es válido.");
            }
        }

        // Estado: solo los dos valores permitidos
        if (f.getEstado().isBlank()) {
            errores.put("estado", "El estado es obligatorio.");
        } else if (!esEstadoValido(f.getEstado())) {
            errores.put("estado", "El estado seleccionado no es válido.");
        }

        return errores;
    }

    private boolean esEstadoValido(String estado) {
        for (String valido : EstacionMonitoreo.ESTADOS_VALIDOS) {
            if (valido.equals(estado)) {
                return true;
            }
        }
        return false;
    }

    private void obligatorio(Map<String, String> errores, String campo, String valor, String mensaje) {
        if (valor == null || valor.isBlank()) {
            errores.put(campo, mensaje);
        }
    }

    private void rango(Map<String, String> errores, String campo, String valor, String nombre,
                       double minimo, boolean incluyeMinimo, Double maximo, String mensajeRango) {
        if (errores.containsKey(campo)) {
            return;
        }
        Double numero;
        try {
            numero = aNumero(valor);
        } catch (NumberFormatException ex) {
            errores.put(campo, nombre + " debe ser un número válido.");
            return;
        }
        boolean bajoMinimo = incluyeMinimo ? numero < minimo : numero <= minimo;
        boolean sobreMaximo = maximo != null && numero > maximo;
        if (bajoMinimo || sobreMaximo || numero.isNaN() || numero.isInfinite()) {
            errores.put(campo, mensajeRango);
        }
    }

    private static Long aId(String texto) {
        try {
            return Long.valueOf(texto.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public static double aNumero(String texto) {
        return Double.parseDouble(texto.trim().replace(',', '.'));
    }
}