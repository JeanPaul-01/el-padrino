package com.elpadrino.dto;

import com.elpadrino.model.Cita;

import java.time.LocalDate;
import java.util.List;

/** Columna del calendario semanal de citas. */
public class DiaAgenda {
    private final LocalDate fecha;
    private final List<Cita> citas;

    public DiaAgenda(LocalDate fecha, List<Cita> citas) {
        this.fecha = fecha;
        this.citas = citas;
    }

    public LocalDate getFecha() { return fecha; }
    public List<Cita> getCitas() { return citas; }
    public boolean isHoy() { return LocalDate.now().equals(fecha); }
}
