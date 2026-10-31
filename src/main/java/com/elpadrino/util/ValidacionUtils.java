package com.elpadrino.util;

import com.elpadrino.exception.ReglaNegocioException;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Reglas de validación compartidas por los módulos de inventario, ventas y servicios.
 * <p>
 * Repite en el servidor las validaciones de los formularios JSP, porque las
 * validaciones del navegador se pueden saltar enviando la petición directamente.
 * Usa Apache Commons Lang para el tratamiento de textos y Google Guava para
 * verificar los argumentos internos.
 */
public final class ValidacionUtils {

    private ValidacionUtils() {
        // Clase de utilidades: no se instancia.
    }

    /**
     * Quita los espacios sobrantes de un texto obligatorio.
     *
     * @param valor texto ingresado por el usuario
     * @param campo nombre del campo, para el mensaje de error
     * @return el texto sin espacios al inicio, al final ni repetidos
     * @throws ReglaNegocioException si el texto está vacío o solo tiene espacios
     */
    public static String normalizarObligatorio(String valor, String campo) {
        Preconditions.checkArgument(StringUtils.isNotBlank(campo), "Debe indicarse el nombre del campo");
        String limpio = StringUtils.normalizeSpace(valor);
        if (StringUtils.isBlank(limpio)) {
            throw new ReglaNegocioException("El campo " + campo + " es obligatorio");
        }
        return limpio;
    }

    /**
     * Verifica que un precio sea mayor que cero y lo redondea a dos decimales.
     *
     * @param precio precio ingresado
     * @return el precio con dos decimales
     * @throws ReglaNegocioException si el precio es nulo, cero o negativo
     */
    public static BigDecimal validarPrecio(BigDecimal precio) {
        if (precio == null || precio.signum() <= 0) {
            throw new ReglaNegocioException("El precio debe ser mayor que 0");
        }
        return precio.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Verifica que la cantidad de un movimiento sea al menos 1.
     *
     * @param cantidad cantidad ingresada
     * @return la misma cantidad
     * @throws ReglaNegocioException si la cantidad es nula o menor que 1
     */
    public static int validarCantidad(Integer cantidad) {
        if (cantidad == null || cantidad < 1) {
            throw new ReglaNegocioException("La cantidad debe ser al menos 1");
        }
        return cantidad;
    }

    /**
     * Verifica que una salida de inventario no deje el stock en negativo.
     *
     * @param stockActual unidades disponibles (nunca negativas)
     * @param cantidad    unidades que se quieren retirar
     * @throws ReglaNegocioException si se intenta retirar más de lo disponible
     */
    public static void validarSalida(int stockActual, int cantidad) {
        Preconditions.checkArgument(stockActual >= 0, "El stock actual no puede ser negativo");
        if (cantidad > stockActual) {
            throw new ReglaNegocioException(
                    "Stock insuficiente: hay " + stockActual + " unidades y se intentó retirar " + cantidad);
        }
    }
}
