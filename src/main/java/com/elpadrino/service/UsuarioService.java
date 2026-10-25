package com.elpadrino.service;

import com.elpadrino.dto.SesionUsuario;
import com.elpadrino.exception.RecursoNoEncontradoException;
import com.elpadrino.exception.ReglaNegocioException;
import com.elpadrino.model.Rol;
import com.elpadrino.model.Usuario;
import com.elpadrino.repository.UsuarioRepository;
import com.elpadrino.util.PasswordHasher;
import com.elpadrino.util.Validaciones;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Autenticación y administración de usuarios del sistema (proceso TO-BE "Autenticación"). */
@Service
@Transactional(readOnly = true)
public class UsuarioService {

    private static final int LONGITUD_MINIMA_PASSWORD = 6;

    private final UsuarioRepository usuarioRepository;
    private final PasswordHasher passwordHasher;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordHasher passwordHasher) {
        this.usuarioRepository = usuarioRepository;
        this.passwordHasher = passwordHasher;
    }

    /** Valida las credenciales y devuelve los datos que se guardan en la sesión. */
    public SesionUsuario autenticar(String nombreUsuario, String password) {
        String usuarioIngresado = Validaciones.obligatorio(nombreUsuario, "usuario");
        Validaciones.obligatorio(password, "contraseña");

        Usuario usuario = usuarioRepository.findByNombreUsuario(usuarioIngresado)
                .filter(u -> passwordHasher.coincide(password, u.getPassword()))
                .orElseThrow(() -> new ReglaNegocioException("Usuario o contraseña incorrectos"));

        if (!usuario.isActivo()) {
            throw new ReglaNegocioException("El usuario está desactivado. Comuníquese con el administrador");
        }
        return new SesionUsuario(usuario.getIdUsuario(), usuario.getNombreUsuario(), usuario.getRol());
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAllByOrderByNombreUsuarioAsc();
    }

    public Usuario obtener(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", id));
    }

    public long contar() {
        return usuarioRepository.count();
    }

    @Transactional
    public Usuario crear(String nombreUsuario, String password, Rol rol) {
        String nombre = Validaciones.obligatorio(nombreUsuario, "usuario").toLowerCase();
        Validaciones.obligatorio(rol, "rol");
        validarPassword(password);
        if (usuarioRepository.existsByNombreUsuario(nombre)) {
            throw new ReglaNegocioException("Ya existe un usuario con el nombre " + nombre);
        }
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(nombre);
        usuario.setPassword(passwordHasher.cifrar(password));
        usuario.setRol(rol);
        usuario.setEstado(true);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void cambiarPassword(Integer idUsuario, String nuevaPassword) {
        validarPassword(nuevaPassword);
        Usuario usuario = obtener(idUsuario);
        usuario.setPassword(passwordHasher.cifrar(nuevaPassword));
    }

    @Transactional
    public void cambiarEstado(Integer idUsuario, Integer idUsuarioActual) {
        if (idUsuario.equals(idUsuarioActual)) {
            throw new ReglaNegocioException("No puede desactivar su propio usuario");
        }
        Usuario usuario = obtener(idUsuario);
        usuario.setEstado(!usuario.isActivo());
    }

    private void validarPassword(String password) {
        if (password == null || password.length() < LONGITUD_MINIMA_PASSWORD) {
            throw new ReglaNegocioException("La contraseña debe tener al menos " + LONGITUD_MINIMA_PASSWORD + " caracteres");
        }
    }
}
