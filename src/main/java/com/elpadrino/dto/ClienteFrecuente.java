package com.elpadrino.dto;

import java.time.LocalDateTime;

/** Fila del reporte R05 (clientes más frecuentes). */
public class ClienteFrecuente {
    private final Integer idCliente;
    private final String cliente;
    private long visitas;
    private LocalDateTime ultimaVisita;
    private int posicion;
    private int cortes;

    public ClienteFrecuente(Integer idCliente, String cliente) {
        this.idCliente = idCliente;
        this.cliente = cliente;
    }

    public void registrarVisita(LocalDateTime fecha) {
        visitas++;
        if (ultimaVisita == null || fecha.isAfter(ultimaVisita)) {
            ultimaVisita = fecha;
        }
    }

    public Integer getIdCliente() { return idCliente; }
    public String getCliente() { return cliente; }
    public long getVisitas() { return visitas; }
    public LocalDateTime getUltimaVisita() { return ultimaVisita; }
    public int getPosicion() { return posicion; }
    public void setPosicion(int posicion) { this.posicion = posicion; }
    public int getCortes() { return cortes; }
    public void setCortes(int cortes) { this.cortes = cortes; }
}
