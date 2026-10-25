package com.elpadrino.service;

import com.elpadrino.dto.ResumenFidelizacion;
import com.elpadrino.model.Cliente;
import com.elpadrino.model.Fidelizacion;
import com.elpadrino.repository.FidelizacionRepository;
import com.elpadrino.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Programa de cliente frecuente: cada 5 cortes pagados, el 6.º es gratis.
 * Se actualiza automáticamente al registrar una venta con servicios.
 */
@Service
@Transactional(readOnly = true)
public class FidelizacionService {

    private final FidelizacionRepository fidelizacionRepository;
    private final VentaRepository ventaRepository;

    public FidelizacionService(FidelizacionRepository fidelizacionRepository, VentaRepository ventaRepository) {
        this.fidelizacionRepository = fidelizacionRepository;
        this.ventaRepository = ventaRepository;
    }

    public Optional<Fidelizacion> buscarPorCliente(Integer idCliente) {
        return fidelizacionRepository.findByClienteIdCliente(idCliente);
    }

    public boolean tieneCorteGratis(Integer idCliente) {
        return buscarPorCliente(idCliente).map(Fidelizacion::isCorteGratisDisponible).orElse(false);
    }

    /**
     * Registra los servicios pagados de una venta.
     *
     * @param serviciosPagados servicios cobrados (sin contar el corte gratis)
     * @param canjeoCorteGratis true si en esta venta se usó el corte gratis
     */
    @Transactional
    public Fidelizacion registrarServicios(Cliente cliente, int serviciosPagados, boolean canjeoCorteGratis) {
        Fidelizacion tarjeta = fidelizacionRepository.findByClienteIdCliente(cliente.getIdCliente())
                .orElseGet(() -> {
                    Fidelizacion nueva = new Fidelizacion();
                    nueva.setCliente(cliente);
                    return nueva;
                });

        int cortes = tarjeta.getCortesAcumulados();
        if (canjeoCorteGratis) {
            // El canje consume los 5 cortes acumulados; el sobrante (si lo hubiera) se conserva.
            cortes -= Fidelizacion.CORTES_REQUERIDOS;
        }
        tarjeta.setCortesAcumulados(Math.max(cortes, 0) + serviciosPagados);
        tarjeta.setPuntos(tarjeta.getPuntos() + serviciosPagados);
        return fidelizacionRepository.save(tarjeta);
    }

    /** Reporte R06: estado del programa de fidelización. */
    public ResumenFidelizacion resumen() {
        List<Fidelizacion> detalle = fidelizacionRepository.findAllByOrderByCortesAcumuladosDescPuntosDesc();
        long disponibles = detalle.stream().filter(Fidelizacion::isCorteGratisDisponible).count();
        long canjesMes = ventaRepository.countByDescuentoGreaterThanAndFechaGreaterThanEqual(
                BigDecimal.ZERO, LocalDate.now().withDayOfMonth(1).atStartOfDay());
        return new ResumenFidelizacion(detalle.size(), disponibles, canjesMes, detalle);
    }
}
