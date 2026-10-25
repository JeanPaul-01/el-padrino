package com.elpadrino.dto;

import com.elpadrino.model.Cita;
import com.elpadrino.model.Inventario;

import java.math.BigDecimal;
import java.util.List;

/** Indicadores del reporte R07 mostrados en el Dashboard. */
public class ResumenDia {
    private final long citasHoy;
    private final long pendientes;
    private final long confirmadas;
    private final BigDecimal ingresosDia;
    private final List<Cita> citas;
    private final List<Inventario> stockCritico;

    public ResumenDia(long citasHoy, long pendientes, long confirmadas, BigDecimal ingresosDia,
                      List<Cita> citas, List<Inventario> stockCritico) {
        this.citasHoy = citasHoy;
        this.pendientes = pendientes;
        this.confirmadas = confirmadas;
        this.ingresosDia = ingresosDia;
        this.citas = citas;
        this.stockCritico = stockCritico;
    }

    public long getCitasHoy() { return citasHoy; }
    public long getPendientes() { return pendientes; }
    public long getConfirmadas() { return confirmadas; }
    public BigDecimal getIngresosDia() { return ingresosDia; }
    public List<Cita> getCitas() { return citas; }
    public List<Inventario> getStockCritico() { return stockCritico; }
    public int getProductosStockCritico() { return stockCritico.size(); }
}
