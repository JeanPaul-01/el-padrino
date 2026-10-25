package com.elpadrino.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/** Cliente de la barbería (tabla CLIENTE). */
@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer idCliente;

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "telefono", nullable = false, unique = true, length = 15)
    private String telefono;

    @Column(name = "correo", length = 100)
    private String correo;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDate fechaRegistro;

    @PrePersist
    void alRegistrar() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDate.now();
        }
    }

    public Integer getIdCliente() { return idCliente; }
    public void setIdCliente(Integer idCliente) { this.idCliente = idCliente; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}
