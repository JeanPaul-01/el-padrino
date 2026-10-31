package com.elpadrino.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Pruebas unitarias de la entidad Producto. */
class ProductoTest {

    @Test
    @DisplayName("Un producto sin registro de inventario tiene stock 0")
    void stockActualSinInventario() {
        Producto producto = new Producto();
        assertEquals(0, producto.getStockActual());
    }

    @Test
    @DisplayName("El stock actual se lee del inventario asociado")
    void stockActualDesdeInventario() {
        Inventario inventario = new Inventario();
        inventario.setStockActual(7);
        Producto producto = new Producto();
        producto.setInventario(inventario);

        assertEquals(7, producto.getStockActual());
    }

    @Test
    @DisplayName("Un producto nuevo está activo por defecto")
    void productoNuevoActivo() {
        assertTrue(new Producto().isActivo());
    }

    @Test
    @DisplayName("Un producto con estado falso no está activo")
    void productoInactivo() {
        Producto producto = new Producto();
        producto.setEstado(false);
        assertFalse(producto.isActivo());
    }
}
