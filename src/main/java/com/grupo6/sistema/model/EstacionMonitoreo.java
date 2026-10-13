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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** Estación de monitoreo que registra eventos sísmicos. Una estación puede registrar muchos sismos. */
@Entity
@Table(name = "estacion_monitoreo")
public class EstacionMonitoreo {

    public static final String ESTADO_ACTIVA = "Activa";
    public static final String ESTADO_INACTIVA = "Inactiva";
    public static final String[] ESTADOS_VALIDOS = {ESTADO_ACTIVA, ESTADO_INACTIVA};

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", nullable = false, unique = true, length = 10)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "distrito_id", nullable = false)
    private Distrito distrito;

    @Column(name = "latitud", nullable = false)
    private double latitud;

    @Column(name = "longitud", nullable = false)
    private double longitud;

    @Column(name = "fecha_instalacion", nullable = false)
    private LocalDate fechaInstalacion;

    @Column(name = "estado", nullable = false, length = 10)
    private String estado;

    public EstacionMonitoreo() {
    }

    public EstacionMonitoreo(Long id, String codigo, String nombre, Distrito distrito, double latitud,
                             double longitud, LocalDate fechaInstalacion, String estado) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.distrito = distrito;
        this.latitud = latitud;
        this.longitud = longitud;
        this.fechaInstalacion = fechaInstalacion;
        this.estado = estado;
    }

    /** Referencia solo con el id; el repositorio la reemplaza por la entidad administrada. */
    public static EstacionMonitoreo conId(Long id) {
        EstacionMonitoreo estacion = new EstacionMonitoreo();
        estacion.setId(id);
        return estacion;
    }

    /** Una estación inactiva no puede recibir nuevos registros de sismos. */
    public boolean isActiva() {
        return ESTADO_ACTIVA.equals(estado);
    }

    public String getFechaInstalacionTexto() {
        return fechaInstalacion == null ? "" : fechaInstalacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Distrito getDistrito() {
        return distrito;
    }

    public void setDistrito(Distrito distrito) {
        this.distrito = distrito;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public LocalDate getFechaInstalacion() {
        return fechaInstalacion;
    }

    public void setFechaInstalacion(LocalDate fechaInstalacion) {
        this.fechaInstalacion = fechaInstalacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
