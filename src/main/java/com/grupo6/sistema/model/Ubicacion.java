package com.grupo6.sistema.model;

/**
 * Lo común a Departamento, Provincia y Distrito, para que el módulo de ubicaciones (HU-09)
 * use las mismas pantallas para los tres niveles.
 */
public interface Ubicacion {

    Long getId();

    String getCodigo();

    String getNombre();

    void setNombre(String nombre);

    TipoUbicacion getTipo();

    /** Ubicación de nivel superior (null para un departamento). */
    Ubicacion getPadre();
}
