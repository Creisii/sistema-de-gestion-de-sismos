package com.grupo6.sistema.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.ArrayList;
import java.util.List;

/** Provincia: pertenece a un departamento y tiene muchos distritos. */
@Entity
@Table(name = "provincia",
        uniqueConstraints = @UniqueConstraint(name = "uq_provincia_nombre", columnNames = {"departamento_id", "nombre"}))
public class Provincia implements Ubicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Código de ubigeo de 4 dígitos; empieza con el código del departamento. */
    @Column(name = "codigo", nullable = false, unique = true, length = 4)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 80)
    private String nombre;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "departamento_id", nullable = false)
    private Departamento departamento;

    @OneToMany(mappedBy = "provincia")
    private List<Distrito> distritos = new ArrayList<>();

    public Provincia() {
    }

    public Provincia(Long id, String codigo, String nombre, Departamento departamento) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.departamento = departamento;
    }

    @Override
    public TipoUbicacion getTipo() {
        return TipoUbicacion.PROVINCIA;
    }

    @Override
    public Ubicacion getPadre() {
        return departamento;
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

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    public List<Distrito> getDistritos() {
        return distritos;
    }

    public void setDistritos(List<Distrito> distritos) {
        this.distritos = distritos;
    }
}
