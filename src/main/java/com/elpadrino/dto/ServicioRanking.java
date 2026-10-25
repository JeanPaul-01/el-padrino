package com.elpadrino.dto;

import java.math.BigDecimal;

/** Fila del reporte R03 (servicios más solicitados). */
public class ServicioRanking {
    private final String servicio;
    private long cantidad;
    private BigDecimal ingresos = BigDecimal.ZERO;

    public ServicioRanking(String servicio) { this.servicio = servicio; }

    public void sumar(int cantidad, BigDecimal subtotal) {
        this.cantidad += cantidad;
        this.ingresos = this.ingresos.add(subtotal);
    }

    public String getServicio() { return servicio; }
    public long getCantidad() { return cantidad; }
    public BigDecimal getIngresos() { return ingresos; }
}
