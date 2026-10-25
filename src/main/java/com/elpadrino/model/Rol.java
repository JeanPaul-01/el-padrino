package com.elpadrino.model;

/** Roles del personal que accede al sistema (sección 8.4.5 del informe). */
public enum Rol {
    ADMINISTRADOR("Administrador"),
    RECEPCIONISTA("Recepcionista");

    private final String etiqueta;

    Rol(String etiqueta) { this.etiqueta = etiqueta; }

    public String getEtiqueta() { return etiqueta; }
}
