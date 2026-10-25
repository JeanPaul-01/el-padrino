package com.elpadrino.dto;

import java.math.BigDecimal;
import java.util.List;

/** Resultado del reporte R01 con sus indicadores resumen. */
public class ReporteIngresos {
    private final List<FilaIngreso> filas;
    private final BigDecimal total;
    private final long ventas;
    private final long servicios;
    private final long productos;

    public ReporteIngresos(List<FilaIngreso> filas, BigDecimal total, long ventas, long servicios, long productos) {
        this.filas = filas;
        this.total = total;
        this.ventas = ventas;
        this.servicios = servicios;
        this.productos = productos;
    }

    public boolean isVacio() { return filas.isEmpty(); }
    public List<FilaIngreso> getFilas() { return filas; }
    public BigDecimal getTotal() { return total; }
    public long getVentas() { return ventas; }
    public long getServicios() { return servicios; }
    public long getProductos() { return productos; }
}
