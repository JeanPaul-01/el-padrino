package com.elpadrino.model;

import jakarta.persistence.*;

/** Barbero que atiende las citas (tabla BARBERO). */
@Entity
@Table(name = "barbero")
public class Barbero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_barbero")
    private Integer idBarbero;

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "telefono", length = 15)
    private String telefono;

    @Column(name = "especialidad", length = 100)
    private String especialidad;

    @Column(name = "estado", nullable = false)
    private Boolean estado = Boolean.TRUE;

    public Integer getIdBarbero() { return idBarbero; }
    public void setIdBarbero(Integer idBarbero) { this.idBarbero = idBarbero; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }

    public boolean isActivo() { return Boolean.TRUE.equals(estado); }
}
