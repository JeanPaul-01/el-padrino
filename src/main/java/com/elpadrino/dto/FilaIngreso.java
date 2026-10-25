package com.elpadrino.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Fila del reporte R01 (ingresos por día o por mes). */
public class FilaIngreso {
    private final LocalDate periodo;
    private final String etiqueta;
    private long ventas;
    private long servicios;
    private long productos;
    private BigDecimal ingresos = BigDecimal.ZERO;
    private int porcentajeBarra;

    public FilaIngreso(LocalDate periodo, String etiqueta) {
        this.periodo = periodo;
        this.etiqueta = etiqueta;
    }

    public void sumar(long servicios, long productos, BigDecimal total) {
        this.ventas++;
        this.servicios += servicios;
        this.productos += productos;
        this.ingresos = this.ingresos.add(total);
    }

    public LocalDate getPeriodo() { return periodo; }
    public String getEtiqueta() { return etiqueta; }
    public long getVentas() { return ventas; }
    public long getServicios() { return servicios; }
    public long getProductos() { return productos; }
    public BigDecimal getIngresos() { return ingresos; }
    public int getPorcentajeBarra() { return porcentajeBarra; }
    public void setPorcentajeBarra(int porcentajeBarra) { this.porcentajeBarra = porcentajeBarra; }
}
