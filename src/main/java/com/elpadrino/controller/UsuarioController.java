package com.elpadrino.controller;

import com.elpadrino.dto.SesionUsuario;
import com.elpadrino.exception.ReglaNegocioException;
import com.elpadrino.model.Rol;
import com.elpadrino.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Pantalla 1 (Login) y administración de usuarios. */
@Controller
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String login(HttpSession session) {
        if (session.getAttribute(SesionUsuario.ATRIBUTO) != null) {
            return "redirect:/dashboard";
        }
        return "login";
    }

    @PostMapping("/login")
    public String iniciarSesion(@RequestParam(required = false) String usuario,
                                @RequestParam(required = false) String password,
                                HttpServletRequest request, Model model) {
        try {
            SesionUsuario sesion = usuarioService.autenticar(usuario, password);
            // Nueva sesión para evitar fijación de sesión.
            HttpSession anterior = request.getSession(false);
            if (anterior != null) {
                anterior.invalidate();
            }
            request.getSession(true).setAttribute(SesionUsuario.ATRIBUTO, sesion);
            return "redirect:/dashboard";
        } catch (ReglaNegocioException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("usuario", usuario);
            return "login";
        }
    }

    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/usuarios")
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listar());
        model.addAttribute("roles", Rol.values());
        return "usuarios/index";
    }

    @PostMapping("/usuarios")
    public String crear(@RequestParam String nombreUsuario, @RequestParam String password,
                        @RequestParam Rol rol, RedirectAttributes flash) {
        try {
            usuarioService.crear(nombreUsuario, password, rol);
            flash.addFlashAttribute("ok", "Usuario registrado correctamente");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/usuarios";
    }

    @PostMapping("/usuarios/{id}/password")
    public String cambiarPassword(@PathVariable Integer id, @RequestParam String password, RedirectAttributes flash) {
        try {
            usuarioService.cambiarPassword(id, password);
            flash.addFlashAttribute("ok", "Contraseña actualizada");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/usuarios";
    }

    @PostMapping("/usuarios/{id}/estado")
    public String cambiarEstado(@PathVariable Integer id, @SessionAttribute(SesionUsuario.ATRIBUTO) SesionUsuario sesion,
                                RedirectAttributes flash) {
        try {
            usuarioService.cambiarEstado(id, sesion.getIdUsuario());
            flash.addFlashAttribute("ok", "Estado del usuario actualizado");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/usuarios";
    }
}
