package com.grupo6.sistema.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/** Departamento del Perú. Un departamento tiene muchas provincias. */
@Entity
@Table(name = "departamento")
public class Departamento implements Ubicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Código de ubigeo de 2 dígitos (por ejemplo, 04 = Arequipa). */
    @Column(name = "codigo", nullable = false, unique = true, length = 2)
    private String codigo;

    @Column(name = "nombre", nullable = false, unique = true, length = 60)
    private String nombre;

    @OneToMany(mappedBy = "departamento")
    private List<Provincia> provincias = new ArrayList<>();

    public Departamento() {
    }

    public Departamento(Long id, String codigo, String nombre) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
    }

    @Override
    public TipoUbicacion getTipo() {
        return TipoUbicacion.DEPARTAMENTO;
    }

    @Override
    public Ubicacion getPadre() {
        return null;
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

    public List<Provincia> getProvincias() {
        return provincias;
    }

    public void setProvincias(List<Provincia> provincias) {
        this.provincias = provincias;
    }
}
