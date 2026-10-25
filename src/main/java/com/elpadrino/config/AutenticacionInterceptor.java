package com.elpadrino.config;

import com.elpadrino.dto.SesionUsuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Control de acceso: exige sesión iniciada y restringe las funciones de administrador
 * (gestión de usuarios y exportación del reporte de ingresos).
 */
public class AutenticacionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        SesionUsuario usuario = session == null ? null : (SesionUsuario) session.getAttribute(SesionUsuario.ATRIBUTO);

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        String ruta = request.getRequestURI().substring(request.getContextPath().length());
        boolean soloAdministrador = ruta.startsWith("/usuarios") || ruta.startsWith("/reportes/ingresos/exportar");
        if (soloAdministrador && !usuario.isAdministrador()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Solo el administrador puede acceder a esta opción");
            return false;
        }
        return true;
    }
}
