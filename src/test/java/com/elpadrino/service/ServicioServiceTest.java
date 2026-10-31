
package com.elpadrino.service;

import com.elpadrino.exception.ReglaNegocioException;
import com.elpadrino.model.Servicio;
import com.elpadrino.repository.ServicioRepository;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ServicioServiceTest {

    @Test
    void rechazaNombreMayorA100Caracteres() {
        ServicioRepository repositorio = mock(ServicioRepository.class);
        ServicioService servicioService = new ServicioService(repositorio);

        Servicio servicio = new Servicio();
        servicio.setNombre("A".repeat(101));
        servicio.setPrecio(new BigDecimal("25.00"));

        assertThrows(ReglaNegocioException.class,
                () -> servicioService.guardar(servicio));

        verifyNoInteractions(repositorio);
    }

    @Test
    void rechazaDescripcionMayorA255Caracteres() {
        ServicioRepository repositorio = mock(ServicioRepository.class);
        ServicioService servicioService = new ServicioService(repositorio);

        Servicio servicio = new Servicio();
        servicio.setNombre("Corte clasico");
        servicio.setDescripcion("A".repeat(256));
        servicio.setPrecio(new BigDecimal("25.00"));

        assertThrows(ReglaNegocioException.class,
                () -> servicioService.guardar(servicio));

        verifyNoInteractions(repositorio);
    }
}
