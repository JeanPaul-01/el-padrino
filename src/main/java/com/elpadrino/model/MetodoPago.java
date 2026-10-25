package com.elpadrino.model;

/** Métodos de pago aceptados en la barbería. */
public enum MetodoPago {
    EFECTIVO("Efectivo"),
    YAPE("Yape"),
    PLIN("Plin"),
    TARJETA("Tarjeta");

    private final String etiqueta;

    MetodoPago(String etiqueta) { this.etiqueta = etiqueta; }

    public String getEtiqueta() { return etiqueta; }
}
