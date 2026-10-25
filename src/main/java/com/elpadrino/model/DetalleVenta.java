package com.elpadrino.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

/** Línea de una venta: un servicio o un producto (tabla DETALLE_VENTA). */
@Entity
@Table(name = "detalle_venta")
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer idDetalle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_venta", nullable = false)
    private Venta venta;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_servicio")
    private Servicio servicio;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_producto")
    private Producto producto;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    public static DetalleVenta deServicio(Servicio servicio, int cantidad) {
        DetalleVenta d = new DetalleVenta();
        d.servicio = servicio;
        d.establecerImporte(servicio.getPrecio(), cantidad);
        return d;
    }

    public static DetalleVenta deProducto(Producto producto, int cantidad) {
        DetalleVenta d = new DetalleVenta();
        d.producto = producto;
        d.establecerImporte(producto.getPrecio(), cantidad);
        return d;
    }

    private void establecerImporte(BigDecimal precio, int cantidad) {
        this.cantidad = cantidad;
        this.precioUnitario = precio;
        this.subtotal = precio.multiply(BigDecimal.valueOf(cantidad));
    }

    public boolean isEsServicio() { return servicio != null; }

    public String getDescripcion() {
        return servicio != null ? servicio.getNombre() : producto.getNombre();
    }

    public String getTipo() { return servicio != null ? "Servicio" : "Producto"; }

    public Integer getIdDetalle() { return idDetalle; }
    public void setIdDetalle(Integer idDetalle) { this.idDetalle = idDetalle; }
    public Venta getVenta() { return venta; }
    public void setVenta(Venta venta) { this.venta = venta; }
    public Servicio getServicio() { return servicio; }
    public void setServicio(Servicio servicio) { this.servicio = servicio; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}
