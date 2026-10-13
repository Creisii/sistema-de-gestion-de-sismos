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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Evento sísmico. Se ubica en un distrito del catálogo (HU-09) y, opcionalmente,
 * indica la estación de monitoreo que lo registró (HU-11).
 */
@Entity
@Table(name = "sismo")
public class Sismo {

    public static final String ESTADO_REGISTRADO = "Registrado";
    public static final String ESTADO_EN_EVALUACION = "En evaluación";
    public static final String ESTADO_EN_SEGUIMIENTO = "En seguimiento";
    public static final String ESTADO_CERRADO = "Cerrado";

    public static final String[] ESTADOS_VALIDOS = {
            ESTADO_REGISTRADO, ESTADO_EN_EVALUACION, ESTADO_EN_SEGUIMIENTO, ESTADO_CERRADO
    };

    /** Escala de Mercalli Modificada (MM). */
    public static final String[] INTENSIDADES = {
            "I (MM)", "II (MM)", "III (MM)", "IV (MM)", "V (MM)", "VI (MM)",
            "VII (MM)", "VIII (MM)", "IX (MM)", "X (MM)", "XI (MM)", "XII (MM)"
    };

    private static final DateTimeFormatter FORMATO_TEXTO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FORMATO_INPUT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "magnitud", nullable = false)
    private double magnitud;

    @Column(name = "profundidad", nullable = false)
    private double profundidad;

    @Column(name = "latitud", nullable = false)
    private double latitud;

    @Column(name = "longitud", nullable = false)
    private double longitud;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "distrito_id", nullable = false)
    private Distrito distrito;

    /** Punto de referencia opcional, por ejemplo "12 km al SO de Chivay". */
    @Column(name = "referencia", length = 150)
    private String referencia;

    @Column(name = "intensidad", length = 10)
    private String intensidad;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "estacion_id")
    private EstacionMonitoreo estacion;

    public Sismo() {
    }

    public static boolean esEstadoValido(String estado) {
        if (estado == null) {
            return false;
        }
        for (String valido : ESTADOS_VALIDOS) {
            if (valido.equals(estado)) {
                return true;
            }
        }
        return false;
    }

    public boolean isEnSeguimiento() {
        return ESTADO_EN_SEGUIMIENTO.equals(estado);
    }

    public String getClaseEstado() {
        if (estado == null) {
            return "";
        }
        return switch (estado) {
            case ESTADO_EN_SEGUIMIENTO -> "badge badge-seguimiento";
            case ESTADO_EN_EVALUACION -> "badge badge-evaluacion";
            case ESTADO_CERRADO -> "badge badge-cerrado";
            default -> "badge badge-registrado";
        };
    }

    public String getFechaHoraTexto() {
        return fechaHora == null ? "" : fechaHora.format(FORMATO_TEXTO);
    }

    /** Fecha en el formato que espera un input datetime-local (por ejemplo, 2026-08-18T14:32). */
    public String getFechaHoraInput() {
        return fechaHora == null ? "" : fechaHora.format(FORMATO_INPUT);
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

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public double getMagnitud() {
        return magnitud;
    }

    public void setMagnitud(double magnitud) {
        this.magnitud = magnitud;
    }

    public double getProfundidad() {
        return profundidad;
    }

    public void setProfundidad(double profundidad) {
        this.profundidad = profundidad;
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

    public Distrito getDistrito() {
        return distrito;
    }

    public void setDistrito(Distrito distrito) {
        this.distrito = distrito;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getIntensidad() {
        return intensidad;
    }

    public void setIntensidad(String intensidad) {
        this.intensidad = intensidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public EstacionMonitoreo getEstacion() {
        return estacion;
    }

    public void setEstacion(EstacionMonitoreo estacion) {
        this.estacion = estacion;
    }
}
