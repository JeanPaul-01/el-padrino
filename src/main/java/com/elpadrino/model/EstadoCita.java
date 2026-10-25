package com.elpadrino.model;

/**
 * Estados posibles de una cita. El color corresponde a la retroalimentación visual
 * definida en el prototipo: pendientes en amarillo, confirmadas en verde.
 */
public enum EstadoCita {
    PENDIENTE("Pendiente", "warning"),
    CONFIRMADA("Confirmada", "success"),
    ATENDIDA("Atendida", "primary"),
    CANCELADA("Cancelada", "secondary");

    private final String etiqueta;
    private final String color;

    EstadoCita(String etiqueta, String color) {
        this.etiqueta = etiqueta;
        this.color = color;
    }

    public String getEtiqueta() { return etiqueta; }
    public String getColor() { return color; }

    /** Una cita activa ocupa el horario del barbero. */
    public boolean ocupaHorario() { return this != CANCELADA; }

    /** Solo las citas pendientes o confirmadas pueden cambiar de estado. */
    public boolean isModificable() { return this == PENDIENTE || this == CONFIRMADA; }
}
