package com.elpadrino.dto;

import com.elpadrino.model.Rol;

import java.io.Serializable;

/** Datos del usuario autenticado que se guardan en la sesión HTTP. */
public class SesionUsuario implements Serializable {

    public static final String ATRIBUTO = "usuarioSesion";

    private final Integer idUsuario;
    private final String nombreUsuario;
    private final Rol rol;

    public SesionUsuario(Integer idUsuario, String nombreUsuario, Rol rol) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.rol = rol;
    }

    public boolean isAdministrador() { return rol == Rol.ADMINISTRADOR; }

    public Integer getIdUsuario() { return idUsuario; }
    public String getNombreUsuario() { return nombreUsuario; }
    public Rol getRol() { return rol; }
}
