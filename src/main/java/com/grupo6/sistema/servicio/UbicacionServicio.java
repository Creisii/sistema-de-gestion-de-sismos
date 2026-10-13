package com.grupo6.sistema.servicio;

import com.grupo6.sistema.listener.AplicacionListener;
import com.grupo6.sistema.model.Departamento;
import com.grupo6.sistema.model.Distrito;
import com.grupo6.sistema.model.Provincia;
import com.grupo6.sistema.model.TipoUbicacion;
import com.grupo6.sistema.model.Ubicacion;
import com.grupo6.sistema.repository.DepartamentoRepository;
import com.grupo6.sistema.repository.DistritoRepository;
import com.grupo6.sistema.repository.ProvinciaRepository;
import com.grupo6.sistema.validacion.UbicacionFormulario;
import jakarta.servlet.ServletContext;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * HU-09: reglas del catálogo de ubicaciones (departamentos, provincias y distritos).
 * Reúne los tres repositorios para que los servlets trabajen igual con los tres niveles.
 */
public class UbicacionServicio {

    private final DepartamentoRepository departamentos;
    private final ProvinciaRepository provincias;
    private final DistritoRepository distritos;

    public UbicacionServicio(ServletContext contexto) {
        this.departamentos = (DepartamentoRepository) contexto.getAttribute(AplicacionListener.REPOSITORIO_DEPARTAMENTOS);
        this.provincias = (ProvinciaRepository) contexto.getAttribute(AplicacionListener.REPOSITORIO_PROVINCIAS);
        this.distritos = (DistritoRepository) contexto.getAttribute(AplicacionListener.REPOSITORIO_DISTRITOS);
    }

    public Optional<? extends Ubicacion> buscar(TipoUbicacion tipo, long id) {
        return switch (tipo) {
            case DEPARTAMENTO -> departamentos.buscarPorId(id);
            case PROVINCIA -> provincias.buscarPorId(id);
            case DISTRITO -> distritos.buscarPorId(id);
        };
    }

    /**
     * Valida el formulario.
     *
     * @param padre  departamento (para una provincia) o provincia (para un distrito); null para un departamento
     * @param actual null al registrar; la ubicación que se edita al editar (su código no cambia)
     * @return mapa campo -> mensaje; vacío si todo es válido
     */
    public Map<String, String> validar(UbicacionFormulario f, Ubicacion padre, Ubicacion actual) {
        Map<String, String> errores = new LinkedHashMap<>();
        TipoUbicacion tipo = f.getTipo();

        if (actual == null) {
            String codigo = f.getCodigo();
            int longitud = tipo.getLongitudCodigo();
            if (codigo.isEmpty()) {
                errores.put("codigo", "El código de ubigeo es obligatorio.");
            } else if (!codigo.matches("\\d{" + longitud + "}")) {
                errores.put("codigo", "El código debe tener exactamente " + longitud + " dígitos.");
            } else if (padre != null && !codigo.startsWith(padre.getCodigo())) {
                errores.put("codigo", "El código debe empezar con " + padre.getCodigo()
                        + ", el código de " + padre.getNombre() + ".");
            } else if (existeCodigo(tipo, codigo)) {
                errores.put("codigo", "El código " + codigo + " ya está registrado.");
            }
        }

        String nombre = f.getNombre();
        int maximo = tipo == TipoUbicacion.DEPARTAMENTO ? 60 : 80;
        Long idExcluido = actual == null ? null : actual.getId();
        if (nombre.isEmpty()) {
            errores.put("nombre", "El nombre es obligatorio.");
        } else if (nombre.length() > maximo) {
            errores.put("nombre", "El nombre no puede superar los " + maximo + " caracteres.");
        } else if (existeNombre(tipo, nombre, padre, idExcluido)) {
            errores.put("nombre", padre == null
                    ? "Ya existe un departamento con ese nombre."
                    : "Ya existe " + (tipo == TipoUbicacion.PROVINCIA ? "una provincia" : "un distrito")
                    + " con ese nombre en " + padre.getNombre() + ".");
        }
        return errores;
    }

    /** Registra una ubicación nueva dentro de su padre. */
    public Ubicacion registrar(UbicacionFormulario f, Ubicacion padre) {
        return switch (f.getTipo()) {
            case DEPARTAMENTO -> departamentos.agregar(new Departamento(null, f.getCodigo(), f.getNombre()));
            case PROVINCIA -> provincias.agregar(new Provincia(null, f.getCodigo(), f.getNombre(), (Departamento) padre));
            case DISTRITO -> distritos.agregar(new Distrito(null, f.getCodigo(), f.getNombre(), (Provincia) padre));
        };
    }

    /** Al editar solo cambia el nombre: el código de ubigeo identifica la ubicación y no se modifica. */
    public void actualizarNombre(Ubicacion ubicacion, String nombre) {
        ubicacion.setNombre(nombre);
        switch (ubicacion.getTipo()) {
            case DEPARTAMENTO -> departamentos.actualizar((Departamento) ubicacion);
            case PROVINCIA -> provincias.actualizar((Provincia) ubicacion);
            case DISTRITO -> distritos.actualizar((Distrito) ubicacion);
        }
    }

    /**
     * Motivo por el que una ubicación no se puede eliminar, o null si se puede.
     * No se elimina una ubicación con niveles inferiores o con sismos o estaciones asociados.
     */
    public String motivoParaNoEliminar(Ubicacion ubicacion) {
        long id = ubicacion.getId();
        switch (ubicacion.getTipo()) {
            case DEPARTAMENTO -> {
                long cantidad = departamentos.contarProvincias(id);
                if (cantidad > 0) {
                    return ubicacion.getNombre() + " tiene " + cantidad + " provincia(s) registrada(s). "
                            + "Elimine primero sus provincias.";
                }
            }
            case PROVINCIA -> {
                long cantidad = provincias.contarDistritos(id);
                if (cantidad > 0) {
                    return ubicacion.getNombre() + " tiene " + cantidad + " distrito(s) registrado(s). "
                            + "Elimine primero sus distritos.";
                }
            }
            case DISTRITO -> {
                long sismos = distritos.contarSismos(id);
                long estaciones = distritos.contarEstaciones(id);
                if (sismos > 0 || estaciones > 0) {
                    return ubicacion.getNombre() + " está en uso por " + sismos + " sismo(s) y "
                            + estaciones + " estación(es) de monitoreo.";
                }
            }
        }
        return null;
    }

    public boolean eliminar(Ubicacion ubicacion) {
        long id = ubicacion.getId();
        return switch (ubicacion.getTipo()) {
            case DEPARTAMENTO -> departamentos.eliminar(id);
            case PROVINCIA -> provincias.eliminar(id);
            case DISTRITO -> distritos.eliminar(id);
        };
    }

    /** URL (sin contexto) del listado donde se muestra una ubicación: el de su padre. */
    public static String urlListado(TipoUbicacion tipo, Ubicacion padre) {
        return switch (tipo) {
            case DEPARTAMENTO -> "/ubicaciones";
            case PROVINCIA -> "/ubicaciones?departamentoId=" + padre.getId();
            case DISTRITO -> "/ubicaciones?provinciaId=" + padre.getId();
        };
    }

    private boolean existeCodigo(TipoUbicacion tipo, String codigo) {
        return switch (tipo) {
            case DEPARTAMENTO -> departamentos.buscarPorCodigo(codigo).isPresent();
            case PROVINCIA -> provincias.buscarPorCodigo(codigo).isPresent();
            case DISTRITO -> distritos.buscarPorCodigo(codigo).isPresent();
        };
    }

    private boolean existeNombre(TipoUbicacion tipo, String nombre, Ubicacion padre, Long idExcluido) {
        return switch (tipo) {
            case DEPARTAMENTO -> departamentos.existeNombre(nombre, idExcluido);
            case PROVINCIA -> provincias.existeNombreEnDepartamento(nombre, padre.getId(), idExcluido);
            case DISTRITO -> distritos.existeNombreEnProvincia(nombre, padre.getId(), idExcluido);
        };
    }
}
