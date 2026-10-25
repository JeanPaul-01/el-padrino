package com.elpadrino.config;

import com.elpadrino.dto.SesionUsuario;
import com.elpadrino.exception.RecursoNoEncontradoException;
import com.elpadrino.util.Formato;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

/** Datos comunes a todas las vistas JSP y manejo de errores generales. */
@ControllerAdvice
public class VistaGlobalAdvice {

    private static final Formato FORMATO = new Formato();

    /** Objeto de formato disponible en las vistas como ${f}. */
    @ModelAttribute("f")
    public Formato formato() {
        return FORMATO;
    }

    @ModelAttribute("usuarioActual")
    public SesionUsuario usuarioActual(HttpSession session) {
        return (SesionUsuario) session.getAttribute(SesionUsuario.ATRIBUTO);
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public String noEncontrado(RecursoNoEncontradoException ex, Model model, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        model.addAttribute("f", FORMATO);
        model.addAttribute("mensaje", ex.getMessage());
        return "error";
    }
}
