package com.elpadrino.dto;

import com.elpadrino.model.Fidelizacion;

import java.util.List;

/** Indicadores y detalle del reporte R06 (estado del programa de fidelización). */
public class ResumenFidelizacion {
    private final long clientesEnPrograma;
    private final long cortesGratisDisponibles;
    private final long canjesDelMes;
    private final List<Fidelizacion> detalle;

    public ResumenFidelizacion(long clientesEnPrograma, long cortesGratisDisponibles,
                               long canjesDelMes, List<Fidelizacion> detalle) {
        this.clientesEnPrograma = clientesEnPrograma;
        this.cortesGratisDisponibles = cortesGratisDisponibles;
        this.canjesDelMes = canjesDelMes;
        this.detalle = detalle;
    }

    public long getClientesEnPrograma() { return clientesEnPrograma; }
    public long getCortesGratisDisponibles() { return cortesGratisDisponibles; }
    public long getCanjesDelMes() { return canjesDelMes; }
    public List<Fidelizacion> getDetalle() { return detalle; }
}
