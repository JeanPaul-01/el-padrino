package com.elpadrino.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Programa de cliente frecuente (tabla FIDELIZACION):
 * cada 5 cortes pagados, el 6.º es gratis.
 */
@Entity
@Table(name = "fidelizacion")
public class Fidelizacion {

    /** Cortes pagados necesarios para obtener un corte gratis. */
    public static final int CORTES_REQUERIDOS = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_fidelizacion")
    private Integer idFidelizacion;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_cliente", nullable = false, unique = true)
    private Cliente cliente;

    /** Cortes pagados acumulados desde el último canje. */
    @Column(name = "cortes_acumulados", nullable = false)
    private Integer cortesAcumulados = 0;

    /** Total histórico de servicios pagados (no se reinicia). */
    @Column(name = "puntos", nullable = false)
    private Integer puntos = 0;

    @Column(name = "ultima_actualizacion", nullable = false)
    private LocalDateTime ultimaActualizacion;

    @PrePersist
    @PreUpdate
    void alActualizar() {
        ultimaActualizacion = LocalDateTime.now();
    }

    public boolean isCorteGratisDisponible() {
        return cortesAcumulados != null && cortesAcumulados >= CORTES_REQUERIDOS;
    }

    /** Cortes mostrados en la tarjeta (máximo 5). */
    public int getCortesTarjeta() {
        return Math.min(cortesAcumulados == null ? 0 : cortesAcumulados, CORTES_REQUERIDOS);
    }

    public int getAvancePorcentaje() {
        return getCortesTarjeta() * 100 / CORTES_REQUERIDOS;
    }

    public int getCortesFaltantes() {
        return CORTES_REQUERIDOS - getCortesTarjeta();
    }

    public int getCortesRequeridos() { return CORTES_REQUERIDOS; }

    public Integer getIdFidelizacion() { return idFidelizacion; }
    public void setIdFidelizacion(Integer idFidelizacion) { this.idFidelizacion = idFidelizacion; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public Integer getCortesAcumulados() { return cortesAcumulados; }
    public void setCortesAcumulados(Integer cortesAcumulados) { this.cortesAcumulados = cortesAcumulados; }
    public Integer getPuntos() { return puntos; }
    public void setPuntos(Integer puntos) { this.puntos = puntos; }
    public LocalDateTime getUltimaActualizacion() { return ultimaActualizacion; }
    public void setUltimaActualizacion(LocalDateTime ultimaActualizacion) { this.ultimaActualizacion = ultimaActualizacion; }
}
