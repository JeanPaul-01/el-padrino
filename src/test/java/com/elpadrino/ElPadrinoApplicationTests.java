package com.elpadrino;

import com.elpadrino.exception.ReglaNegocioException;
import com.elpadrino.model.*;
import com.elpadrino.service.CitaService;
import com.elpadrino.util.Formato;
import com.elpadrino.util.PasswordHasher;
import com.elpadrino.util.Validaciones;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias de las reglas de negocio (no requieren base de datos).
 * Ejecutar con: mvnw.cmd test
 */
class ElPadrinoApplicationTests {

    @Test
    void laContrasenaSeCifraYSeVerifica() {
        PasswordHasher hasher = new PasswordHasher();
        String hash = hasher.cifrar("admin123");
        assertTrue(hash.startsWith("pbkdf2$"));
        assertNotEquals("admin123", hash);
        assertTrue(hasher.coincide("admin123", hash));
        assertFalse(hasher.coincide("otra", hash));
    }

    @Test
    void laFidelizacionOtorgaCorteGratisAlQuintoCorte() {
        Fidelizacion tarjeta = new Fidelizacion();
        tarjeta.setCortesAcumulados(4);
        assertFalse(tarjeta.isCorteGratisDisponible());
        assertEquals(1, tarjeta.getCortesFaltantes());
        tarjeta.setCortesAcumulados(5);
        assertTrue(tarjeta.isCorteGratisDisponible());
        assertEquals(100, tarjeta.getAvancePorcentaje());
    }

    @Test
    void laVentaCalculaSubtotalDeServiciosYProductos() {
        Servicio corte = new Servicio();
        corte.setNombre("Corte clásico");
        corte.setPrecio(new BigDecimal("25.00"));
        Producto cera = new Producto();
        cera.setNombre("Cera mate");
        cera.setPrecio(new BigDecimal("30.00"));

        Venta venta = new Venta();
        venta.agregarDetalle(DetalleVenta.deServicio(corte, 2));
        venta.agregarDetalle(DetalleVenta.deProducto(cera, 1));

        assertEquals(new BigDecimal("80.00"), venta.getSubtotal());
        assertEquals(2, venta.getDetalles().size());
        assertSame(venta, venta.getDetalles().get(0).getVenta());
    }

    @Test
    void elTelefonoDebeTenerNueveDigitosYEmpezarConNueve() {
        assertEquals("987654321", Validaciones.telefono("987 654 321"));
        assertThrows(ReglaNegocioException.class, () -> Validaciones.telefono("12345"));
        assertThrows(ReglaNegocioException.class, () -> Validaciones.telefono("887654321"));
    }

    @Test
    void soloSePuedenModificarCitasPendientesOConfirmadas() {
        assertTrue(EstadoCita.PENDIENTE.isModificable());
        assertTrue(EstadoCita.CONFIRMADA.isModificable());
        assertFalse(EstadoCita.ATENDIDA.isModificable());
        assertFalse(EstadoCita.CANCELADA.isModificable());
    }

    @Test
    void elHorarioDeAtencionEsDeLunesASabadoEnTurnosDeTreintaMinutos() {
        CitaService citaService = new CitaService(null, null, null, null);
        var turnos = citaService.turnosDelDia();
        assertEquals(LocalTime.of(9, 0), turnos.get(0));
        assertEquals(LocalTime.of(19, 30), turnos.get(turnos.size() - 1));
        assertEquals(22, turnos.size());
        assertFalse(CitaService.esDiaLaborable(LocalDate.of(2026, 10, 18))); // domingo
        assertTrue(CitaService.esDiaLaborable(LocalDate.of(2026, 10, 17)));  // sábado
    }

    @Test
    void losMontosSeMuestranEnSolesConDosDecimales() {
        Formato f = new Formato();
        assertEquals("S/ 1,234.50", f.soles(new BigDecimal("1234.5")));
        assertEquals("05/10/2026", f.fecha(LocalDate.of(2026, 10, 5)));
    }
}
