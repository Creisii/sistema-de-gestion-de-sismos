package com.grupo6.sistema.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/** Distrito: pertenece a una provincia. Los sismos y las estaciones se ubican en un distrito. */
@Entity
@Table(name = "distrito",
        uniqueConstraints = @UniqueConstraint(name = "uq_distrito_nombre", columnNames = {"provincia_id", "nombre"}))
public class Distrito implements Ubicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Código de ubigeo de 6 dígitos; empieza con el código de la provincia. */
    @Column(name = "codigo", nullable = false, unique = true, length = 6)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 80)
    private String nombre;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "provincia_id", nullable = false)
    private Provincia provincia;

    public Distrito() {
    }

    public Distrito(Long id, String codigo, String nombre, Provincia provincia) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.provincia = provincia;
    }

    /** Referencia solo con el id; el repositorio la reemplaza por la entidad administrada. */
    public static Distrito conId(Long id) {
        Distrito distrito = new Distrito();
        distrito.setId(id);
        return distrito;
    }

    /** Texto completo para mostrar: "Chivay, Caylloma, Arequipa". */
    public String getUbicacionCompleta() {
        return nombre + ", " + provincia.getNombre() + ", " + provincia.getDepartamento().getNombre();
    }

    @Override
    public TipoUbicacion getTipo() {
        return TipoUbicacion.DISTRITO;
    }

    @Override
    public Ubicacion getPadre() {
        return provincia;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Provincia getProvincia() {
        return provincia;
    }

    public void setProvincia(Provincia provincia) {
        this.provincia = provincia;
    }
}
