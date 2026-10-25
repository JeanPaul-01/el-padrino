package com.elpadrino.service;

import com.elpadrino.dto.*;
import com.elpadrino.exception.ReglaNegocioException;
import com.elpadrino.model.*;
import com.elpadrino.repository.FidelizacionRepository;
import com.elpadrino.repository.VentaRepository;
import com.elpadrino.util.Formato;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Reportes del sistema (sección 8.6 del informe): R01 a R07.
 * Las agregaciones se calculan en Java a partir de las ventas del periodo.
 */
@Service
@Transactional(readOnly = true)
public class ReporteService {

    public enum Agrupacion { DIA, MES }

    private final VentaRepository ventaRepository;
    private final FidelizacionRepository fidelizacionRepository;
    private final InventarioService inventarioService;
    private final CitaService citaService;
    private final Formato formato = new Formato();

    public ReporteService(VentaRepository ventaRepository, FidelizacionRepository fidelizacionRepository,
                          InventarioService inventarioService, CitaService citaService) {
        this.ventaRepository = ventaRepository;
        this.fidelizacionRepository = fidelizacionRepository;
        this.inventarioService = inventarioService;
        this.citaService = citaService;
    }

    /** Valida los filtros comunes: fechas obligatorias y la final no anterior a la inicial. */
    public void validarPeriodo(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) {
            throw new ReglaNegocioException("Indique la fecha inicial y la fecha final");
        }
        if (hasta.isBefore(desde)) {
            throw new ReglaNegocioException("La fecha final no puede ser anterior a la fecha inicial");
        }
    }

    private List<Venta> ventasFiltradas(LocalDate desde, LocalDate hasta, Integer idBarbero, Integer idServicio) {
        validarPeriodo(desde, hasta);
        return ventaRepository.buscarPorPeriodo(desde.atStartOfDay(), hasta.plusDays(1).atStartOfDay())
                .stream()
                .filter(v -> idBarbero == null || v.getBarbero().getIdBarbero().equals(idBarbero))
                .filter(v -> idServicio == null || v.getDetalles().stream()
                        .anyMatch(d -> d.getServicio() != null && d.getServicio().getIdServicio().equals(idServicio)))
                .toList();
    }

    /** R01 – Ingresos por periodo, agrupados por día o por mes. */
    public ReporteIngresos ingresosPorPeriodo(LocalDate desde, LocalDate hasta, Integer idBarbero,
                                              Integer idServicio, Agrupacion agrupacion) {
        List<Venta> ventas = ventasFiltradas(desde, hasta, idBarbero, idServicio);
        Map<LocalDate, FilaIngreso> filas = new TreeMap<>();
        long servicios = 0;
        long productos = 0;
        BigDecimal total = BigDecimal.ZERO;

        for (Venta v : ventas) {
            LocalDate dia = v.getFecha().toLocalDate();
            LocalDate clave = agrupacion == Agrupacion.MES ? dia.withDayOfMonth(1) : dia;
            String etiqueta = agrupacion == Agrupacion.MES ? formato.mes(clave) : formato.dia(clave);
            long s = contar(v, true);
            long p = contar(v, false);
            filas.computeIfAbsent(clave, k -> new FilaIngreso(k, etiqueta)).sumar(s, p, v.getTotal());
            servicios += s;
            productos += p;
            total = total.add(v.getTotal());
        }

        BigDecimal maximo = filas.values().stream().map(FilaIngreso::getIngresos)
                .max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
        for (FilaIngreso fila : filas.values()) {
            fila.setPorcentajeBarra(porcentaje(fila.getIngresos(), maximo).intValue());
        }
        return new ReporteIngresos(new ArrayList<>(filas.values()), total, ventas.size(), servicios, productos);
    }

    private long contar(Venta venta, boolean servicios) {
        return venta.getDetalles().stream()
                .filter(d -> d.isEsServicio() == servicios)
                .mapToLong(DetalleVenta::getCantidad)
                .sum();
    }

    /** R02 – Ingresos por barbero con su porcentaje sobre el total. */
    public List<IngresoBarbero> ingresosPorBarbero(LocalDate desde, LocalDate hasta) {
        List<Venta> ventas = ventasFiltradas(desde, hasta, null, null);
        Map<Integer, IngresoBarbero> porBarbero = new LinkedHashMap<>();
        BigDecimal total = BigDecimal.ZERO;
        for (Venta v : ventas) {
            porBarbero.computeIfAbsent(v.getBarbero().getIdBarbero(), k -> new IngresoBarbero(v.getBarbero().getNombres()))
                    .sumar(v.getTotal());
            total = total.add(v.getTotal());
        }
        for (IngresoBarbero fila : porBarbero.values()) {
            fila.setPorcentaje(porcentaje(fila.getIngresos(), total).setScale(1, RoundingMode.HALF_UP));
        }
        return porBarbero.values().stream()
                .sorted(Comparator.comparing(IngresoBarbero::getIngresos).reversed())
                .toList();
    }

    /** R03 – Servicios más solicitados. */
    public List<ServicioRanking> serviciosMasSolicitados(LocalDate desde, LocalDate hasta, Integer idBarbero) {
        Map<Integer, ServicioRanking> ranking = new HashMap<>();
        for (Venta v : ventasFiltradas(desde, hasta, idBarbero, null)) {
            for (DetalleVenta d : v.getDetalles()) {
                if (d.isEsServicio()) {
                    ranking.computeIfAbsent(d.getServicio().getIdServicio(), k -> new ServicioRanking(d.getServicio().getNombre()))
                            .sumar(d.getCantidad(), d.getSubtotal());
                }
            }
        }
        return ranking.values().stream()
                .sorted(Comparator.comparingLong(ServicioRanking::getCantidad).reversed())
                .toList();
    }

    /** R04 – Productos con stock bajo. */
    public List<Inventario> productosStockBajo() {
        return inventarioService.listarStockBajo();
    }

    /** R05 – Clientes más frecuentes: una visita es una venta que incluye al menos un servicio. */
    public List<ClienteFrecuente> clientesFrecuentes(int limite) {
        LocalDateTime desde = LocalDate.of(2000, 1, 1).atStartOfDay();
        LocalDateTime hasta = LocalDate.now().plusDays(1).atStartOfDay();
        Map<Integer, ClienteFrecuente> mapa = new HashMap<>();
        for (Venta v : ventaRepository.buscarPorPeriodo(desde, hasta)) {
            boolean incluyeServicio = v.getDetalles().stream().anyMatch(DetalleVenta::isEsServicio);
            if (incluyeServicio) {
                Cliente c = v.getCliente();
                mapa.computeIfAbsent(c.getIdCliente(), k -> new ClienteFrecuente(c.getIdCliente(), c.getNombres()))
                        .registrarVisita(v.getFecha());
            }
        }
        Map<Integer, Integer> cortes = fidelizacionRepository.findAll().stream()
                .collect(Collectors.toMap(f -> f.getCliente().getIdCliente(), Fidelizacion::getCortesTarjeta));

        List<ClienteFrecuente> lista = mapa.values().stream()
                .sorted(Comparator.comparingLong(ClienteFrecuente::getVisitas).reversed()
                        .thenComparing(ClienteFrecuente::getUltimaVisita, Comparator.reverseOrder()))
                .limit(limite)
                .toList();
        for (int i = 0; i < lista.size(); i++) {
            ClienteFrecuente fila = lista.get(i);
            fila.setPosicion(i + 1);
            fila.setCortes(cortes.getOrDefault(fila.getIdCliente(), 0));
        }
        return lista;
    }

    /** R07 – Resumen del día para el Dashboard. */
    public ResumenDia resumenDelDia() {
        List<Cita> citas = citaService.citasDeHoy();
        long pendientes = citas.stream().filter(c -> c.getEstado() == EstadoCita.PENDIENTE).count();
        long confirmadas = citas.stream().filter(c -> c.getEstado() == EstadoCita.CONFIRMADA).count();
        LocalDate hoy = LocalDate.now();
        BigDecimal ingresos = ventaRepository.sumarTotal(hoy.atStartOfDay(), hoy.plusDays(1).atStartOfDay());
        return new ResumenDia(citas.size(), pendientes, confirmadas, ingresos, citas,
                inventarioService.listarStockBajo());
    }

    /** Exportación del reporte R01 en CSV (se abre directamente en Excel). */
    public String exportarIngresosCsv(LocalDate desde, LocalDate hasta, Integer idBarbero, Integer idServicio,
                                      Agrupacion agrupacion, String generadoPor) {
        ReporteIngresos reporte = ingresosPorPeriodo(desde, hasta, idBarbero, idServicio, agrupacion);
        StringBuilder csv = new StringBuilder("﻿");
        csv.append("Reporte de ingresos - Barberia El Padrino\n");
        csv.append("Periodo;").append(formato.fecha(desde)).append(" al ").append(formato.fecha(hasta)).append('\n');
        csv.append("Generado;").append(formato.fechaHora(LocalDateTime.now())).append(" por ").append(generadoPor).append("\n\n");
        csv.append("Periodo;Ventas;Servicios;Productos;Ingresos (S/)\n");
        if (reporte.isVacio()) {
            csv.append("No hay registros para el periodo seleccionado\n");
        }
        for (FilaIngreso f : reporte.getFilas()) {
            csv.append(f.getEtiqueta()).append(';').append(f.getVentas()).append(';')
                    .append(f.getServicios()).append(';').append(f.getProductos()).append(';')
                    .append(Formato.numero(f.getIngresos())).append('\n');
        }
        csv.append("Total;").append(reporte.getVentas()).append(';').append(reporte.getServicios()).append(';')
                .append(reporte.getProductos()).append(';').append(Formato.numero(reporte.getTotal())).append('\n');
        return csv.toString();
    }

    private static BigDecimal porcentaje(BigDecimal parte, BigDecimal total) {
        if (total == null || total.signum() == 0) {
            return BigDecimal.ZERO;
        }
        return parte.multiply(BigDecimal.valueOf(100)).divide(total, 2, RoundingMode.HALF_UP);
    }
}
