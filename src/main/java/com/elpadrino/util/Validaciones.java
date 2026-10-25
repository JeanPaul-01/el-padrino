package com.elpadrino.util;

import com.elpadrino.exception.ReglaNegocioException;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/** Validaciones reutilizadas por la capa de servicios. */
public final class Validaciones {

    /** Celular peruano: 9 dígitos que empiezan con 9. */
    private static final Pattern TELEFONO = Pattern.compile("^9\\d{8}$");
    private static final Pattern CORREO = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    private Validaciones() {
    }

    public static String obligatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new ReglaNegocioException("El campo " + campo + " es obligatorio");
        }
        return valor.trim();
    }

    public static <T> T obligatorio(T valor, String campo) {
        if (valor == null) {
            throw new ReglaNegocioException("El campo " + campo + " es obligatorio");
        }
        return valor;
    }

    public static String telefono(String valor) {
        String telefono = obligatorio(valor, "teléfono").replaceAll("\\s", "");
        if (!TELEFONO.matcher(telefono).matches()) {
            throw new ReglaNegocioException("El teléfono debe tener 9 dígitos y empezar con 9");
        }
        return telefono;
    }

    public static String correoOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        String correo = valor.trim();
        if (!CORREO.matcher(correo).matches()) {
            throw new ReglaNegocioException("El correo no tiene un formato válido");
        }
        return correo;
    }

    public static BigDecimal montoNoNegativo(BigDecimal valor, String campo) {
        obligatorio(valor, campo);
        if (valor.signum() < 0) {
            throw new ReglaNegocioException("El " + campo + " debe ser mayor o igual a 0");
        }
        return valor;
    }

    public static int enteroNoNegativo(Integer valor, String campo) {
        obligatorio(valor, campo);
        if (valor < 0) {
            throw new ReglaNegocioException("El " + campo + " debe ser mayor o igual a 0");
        }
        return valor;
    }

    public static int enteroPositivo(Integer valor, String campo) {
        obligatorio(valor, campo);
        if (valor < 1) {
            throw new ReglaNegocioException("La " + campo + " debe ser al menos 1");
        }
        return valor;
    }
}
