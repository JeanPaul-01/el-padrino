package com.elpadrino.dto;

/** Ajuste de stock desde la pantalla de inventario (entrada o salida de unidades). */
public class InventarioRequest {

    public enum TipoMovimiento { ENTRADA, SALIDA }

    private Integer productoId;
    private TipoMovimiento tipo = TipoMovimiento.ENTRADA;
    private Integer cantidad;
    private String motivo;

    public Integer getProductoId() { return productoId; }
    public void setProductoId(Integer productoId) { this.productoId = productoId; }
    public TipoMovimiento getTipo() { return tipo; }
    public void setTipo(TipoMovimiento tipo) { this.tipo = tipo; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
