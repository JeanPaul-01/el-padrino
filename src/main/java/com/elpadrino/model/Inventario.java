package com.elpadrino.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Stock actual de cada producto (tabla INVENTARIO). */
@Entity
@Table(name = "inventario")
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inventario")
    private Integer idInventario;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_producto", nullable = false, unique = true)
    private Producto producto;

    @Column(name = "stock_actual", nullable = false)
    private Integer stockActual = 0;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    @PrePersist
    @PreUpdate
    void alActualizar() {
        fechaActualizacion = LocalDateTime.now();
    }

    /** Estado del stock según el reporte R04: Agotado, Stock bajo o Normal. */
    public String getEstadoStock() {
        if (stockActual == null || stockActual == 0) {
            return "Agotado";
        }
        return stockActual <= producto.getStockMinimo() ? "Stock bajo" : "Normal";
    }

    public boolean isStockCritico() {
        return stockActual != null && stockActual <= producto.getStockMinimo();
    }

    public Integer getIdInventario() { return idInventario; }
    public void setIdInventario(Integer idInventario) { this.idInventario = idInventario; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    public Integer getStockActual() { return stockActual; }
    public void setStockActual(Integer stockActual) { this.stockActual = stockActual; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
}
