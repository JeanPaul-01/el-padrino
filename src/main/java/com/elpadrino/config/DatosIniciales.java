package com.elpadrino.config;

import com.elpadrino.model.Rol;
import com.elpadrino.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Si la tabla de usuarios está vacía, crea el administrador inicial para poder ingresar al sistema.
 * Usuario: admin / Contraseña: admin123 (se recomienda cambiarla).
 */
@Component
public class DatosIniciales implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatosIniciales.class);

    private final UsuarioService usuarioService;

    public DatosIniciales(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public void run(String... args) {
        if (usuarioService.contar() == 0) {
            usuarioService.crear("admin", "admin123", Rol.ADMINISTRADOR);
            log.warn("Se creó el usuario inicial 'admin' con contraseña 'admin123'. Cámbiela después de ingresar.");
        }
    }
}
