package com.elpadrino.util;

import com.elpadrino.exception.ReglaNegocioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Pruebas unitarias (escritas primero, estilo TDD) de las reglas de validación. */
class ValidacionUtilsTest {

    @Test
    @DisplayName("Quita los espacios sobrantes del nombre")
    void normalizaEspacios() {
        assertEquals("Cera mate", ValidacionUtils.normalizarObligatorio("   Cera    mate ", "nombre"));
    }

    @Test
    @DisplayName("Rechaza un nombre con solo espacios")
    void rechazaNombreVacio() {
        assertThrows(ReglaNegocioException.class, () -> ValidacionUtils.normalizarObligatorio("    ", "nombre"));
    }

    @Test
    @DisplayName("Rechaza un precio igual a 0")
    void rechazaPrecioCero() {
        assertThrows(ReglaNegocioException.class, () -> ValidacionUtils.validarPrecio(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Redondea el precio a dos decimales")
    void redondeaPrecio() {
        assertEquals(new BigDecimal("25.50"), ValidacionUtils.validarPrecio(new BigDecimal("25.5")));
    }

    @Test
    @DisplayName("Rechaza una cantidad menor que 1")
    void rechazaCantidadCero() {
        assertThrows(ReglaNegocioException.class, () -> ValidacionUtils.validarCantidad(0));
    }

    @Test
    @DisplayName("Rechaza una salida mayor al stock disponible")
    void rechazaSalidaMayorAlStock() {
        assertThrows(ReglaNegocioException.class, () -> ValidacionUtils.validarSalida(3, 5));
    }

    @Test
    @DisplayName("Permite retirar exactamente todo el stock")
    void permiteSalidaIgualAlStock() {
        assertDoesNotThrow(() -> ValidacionUtils.validarSalida(3, 3));
    }
}
