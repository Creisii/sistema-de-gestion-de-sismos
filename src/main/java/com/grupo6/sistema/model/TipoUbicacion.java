package com.grupo6.sistema.model;

/**
 * Niveles del catálogo de ubicaciones (HU-09). Cada nivel tiene un código de ubigeo
 * de longitud fija y, salvo el departamento, un nivel padre.
 */
public enum TipoUbicacion {

    DEPARTAMENTO("departamento", "Departamento", "Departamentos", 2, true),
    PROVINCIA("provincia", "Provincia", "Provincias", 4, false),
    DISTRITO("distrito", "Distrito", "Distritos", 6, true);

    private final String clave;
    private final String nombre;
    private final String nombrePlural;
    private final int longitudCodigo;
    private final boolean masculino;

    TipoUbicacion(String clave, String nombre, String nombrePlural, int longitudCodigo, boolean masculino) {
        this.clave = clave;
        this.nombre = nombre;
        this.nombrePlural = nombrePlural;
        this.longitudCodigo = longitudCodigo;
        this.masculino = masculino;
    }

    /** Textos con el género correcto para las vistas: "Nuevo distrito" / "Nueva provincia". */
    public String getNuevo() {
        return masculino ? "Nuevo" : "Nueva";
    }

    public String getEste() {
        return masculino ? "este" : "esta";
    }

    public String getRegistrados() {
        return masculino ? "registrados" : "registradas";
    }

    /** Convierte el parámetro "tipo" de la URL; devuelve null si no es válido. */
    public static TipoUbicacion desdeClave(String clave) {
        for (TipoUbicacion tipo : values()) {
            if (tipo.clave.equalsIgnoreCase(clave == null ? "" : clave.trim())) {
                return tipo;
            }
        }
        return null;
    }

    public TipoUbicacion getPadre() {
        return switch (this) {
            case DEPARTAMENTO -> null;
            case PROVINCIA -> DEPARTAMENTO;
            case DISTRITO -> PROVINCIA;
        };
    }

    public String getClave() {
        return clave;
    }

    public String getNombre() {
        return nombre;
    }

    public String getNombrePlural() {
        return nombrePlural;
    }

    public int getLongitudCodigo() {
        return longitudCodigo;
    }
}
