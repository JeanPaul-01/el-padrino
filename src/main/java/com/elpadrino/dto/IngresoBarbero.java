package com.elpadrino.dto;

import java.math.BigDecimal;

/** Fila del reporte R02 (ingresos por barbero). */
public class IngresoBarbero {
    private final String barbero;
    private long atenciones;
    private BigDecimal ingresos = BigDecimal.ZERO;
    private BigDecimal porcentaje = BigDecimal.ZERO;

    public IngresoBarbero(String barbero) { this.barbero = barbero; }

    public void sumar(BigDecimal total) {
        atenciones++;
        ingresos = ingresos.add(total);
    }

    public String getBarbero() { return barbero; }
    public long getAtenciones() { return atenciones; }
    public BigDecimal getIngresos() { return ingresos; }
    public BigDecimal getPorcentaje() { return porcentaje; }
    public void setPorcentaje(BigDecimal porcentaje) { this.porcentaje = porcentaje; }
}
