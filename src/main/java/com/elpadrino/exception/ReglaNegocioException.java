package com.elpadrino.exception;

/**
 * Error de validación o de regla de negocio. Su mensaje se muestra tal cual al usuario,
 * por eso debe ser claro y comprensible (principio "Mensajes claros" del prototipo).
 */
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
