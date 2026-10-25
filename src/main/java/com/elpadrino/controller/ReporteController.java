package com.elpadrino.controller;

import com.elpadrino.dto.SesionUsuario;
import com.elpadrino.exception.ReglaNegocioException;
import com.elpadrino.service.BarberoService;
import com.elpadrino.service.ReporteService;
import com.elpadrino.service.ReporteService.Agrupacion;
import com.elpadrino.service.ServicioService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

/** Pantallas 12 (Reporte de ingresos) y 13 (Stock bajo y clientes frecuentes). */
@Controller
@RequestMapping("/reportes")
public class ReporteController {

    private static final int CLIENTES_EN_RANKING = 10;

    private final ReporteService reporteService;
    private final BarberoService barberoService;
    private final ServicioService servicioService;

    public ReporteController(ReporteService reporteService, BarberoService barberoService,
                             ServicioService servicioService) {
        this.reporteService = reporteService;
        this.barberoService = barberoService;
        this.servicioService = servicioService;
    }

    @GetMapping("/ingresos")
    public String ingresos(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
                           @RequestParam(required = false) Integer barbero,
                           @RequestParam(required = false) Integer servicio,
                           @RequestParam(defaultValue = "DIA") Agrupacion agrupacion,
                           Model model) {
        LocalDate fin = hasta == null ? LocalDate.now() : hasta;
        LocalDate inicio = desde == null ? fin.withDayOfMonth(1) : desde;
        model.addAttribute("desde", inicio);
        model.addAttribute("hasta", fin);
        model.addAttribute("barbero", barbero);
        model.addAttribute("servicio", servicio);
        model.addAttribute("agrupacion", agrupacion);
        model.addAttribute("barberos", barberoService.listar());
        model.addAttribute("servicios", servicioService.listar());
        try {
            model.addAttribute("reporte", reporteService.ingresosPorPeriodo(inicio, fin, barbero, servicio, agrupacion));
            model.addAttribute("porBarbero", reporteService.ingresosPorBarbero(inicio, fin));
            model.addAttribute("ranking", reporteService.serviciosMasSolicitados(inicio, fin, barbero));
        } catch (ReglaNegocioException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "reportes/ingresos";
    }

    /** Exportación a Excel (CSV). Solo el administrador puede exportar el reporte de ingresos. */
    @GetMapping("/ingresos/exportar")
    public ResponseEntity<byte[]> exportar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) Integer barbero,
            @RequestParam(required = false) Integer servicio,
            @RequestParam(defaultValue = "DIA") Agrupacion agrupacion,
            @SessionAttribute(SesionUsuario.ATRIBUTO) SesionUsuario sesion) {
        String csv = reporteService.exportarIngresosCsv(desde, hasta, barbero, servicio, agrupacion,
                sesion.getNombreUsuario());
        String archivo = "ingresos_" + desde + "_" + hasta + ".csv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + archivo + "\"")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(csv.getBytes(StandardCharsets.UTF_8));
    }

    @GetMapping("/stock-clientes")
    public String stockYClientes(Model model) {
        model.addAttribute("stockBajo", reporteService.productosStockBajo());
        model.addAttribute("clientesFrecuentes", reporteService.clientesFrecuentes(CLIENTES_EN_RANKING));
        model.addAttribute("ahora", java.time.LocalDateTime.now());
        return "reportes/stock-clientes";
    }
}
