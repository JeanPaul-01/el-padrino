package com.elpadrino.controller;

import com.elpadrino.dto.SesionUsuario;
import com.elpadrino.dto.VentaRequest;
import com.elpadrino.exception.ReglaNegocioException;
import com.elpadrino.model.Cita;
import com.elpadrino.model.MetodoPago;
import com.elpadrino.model.Venta;
import com.elpadrino.service.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Pantalla 10: registro de venta y pago, y listado de ventas. */
@Controller
@RequestMapping("/ventas")
public class VentaController {

    private final VentaService ventaService;
    private final ClienteService clienteService;
    private final BarberoService barberoService;
    private final ServicioService servicioService;
    private final InventarioService inventarioService;
    private final CitaService citaService;

    public VentaController(VentaService ventaService, ClienteService clienteService, BarberoService barberoService,
                           ServicioService servicioService, InventarioService inventarioService,
                           CitaService citaService) {
        this.ventaService = ventaService;
        this.clienteService = clienteService;
        this.barberoService = barberoService;
        this.servicioService = servicioService;
        this.inventarioService = inventarioService;
        this.citaService = citaService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
                         Model model) {
        LocalDate fin = hasta == null ? LocalDate.now() : hasta;
        LocalDate inicio = desde == null ? fin.minusDays(6) : desde;
        if (fin.isBefore(inicio)) {
            model.addAttribute("error", "La fecha final no puede ser anterior a la fecha inicial");
            fin = inicio;
        }
        List<Venta> ventas = ventaService.listarPorPeriodo(inicio, fin);
        model.addAttribute("ventas", ventas);
        model.addAttribute("total", ventas.stream().map(Venta::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add));
        model.addAttribute("desde", inicio);
        model.addAttribute("hasta", fin);
        return "ventas/lista";
    }

    @GetMapping("/nueva")
    public String nueva(@RequestParam(required = false) Integer citaId, Model model) {
        cargarFormulario(model);
        if (citaId != null) {
            Cita cita = citaService.obtener(citaId);
            model.addAttribute("cita", cita);
            model.addAttribute("clienteId", cita.getCliente().getIdCliente());
            model.addAttribute("barberoId", cita.getBarbero().getIdBarbero());
        }
        return "ventas/form";
    }

    @PostMapping
    public String registrar(@ModelAttribute VentaRequest request,
                            @SessionAttribute(SesionUsuario.ATRIBUTO) SesionUsuario sesion,
                            Model model, RedirectAttributes flash) {
        try {
            Venta venta = ventaService.registrar(request, sesion);
            String mensaje = "Venta N.º " + venta.getIdVenta() + " registrada. Total cobrado: S/ " + venta.getTotal();
            if (venta.isConCorteGratis()) {
                mensaje += " (se aplicó el corte gratis del programa de fidelización)";
            }
            flash.addFlashAttribute("ok", mensaje);
            return "redirect:/ventas/" + venta.getIdVenta();
        } catch (ReglaNegocioException e) {
            cargarFormulario(model);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("clienteId", request.getClienteId());
            model.addAttribute("barberoId", request.getBarberoId());
            model.addAttribute("metodoPago", request.getMetodoPago());
            if (request.getCitaId() != null) {
                model.addAttribute("cita", citaService.obtener(request.getCitaId()));
            }
            return "ventas/form";
        }
    }

    private void cargarFormulario(Model model) {
        model.addAttribute("clientes", clienteService.listar());
        model.addAttribute("barberos", barberoService.listarActivos());
        model.addAttribute("servicios", servicioService.listarActivos());
        model.addAttribute("inventario", inventarioService.listarInventario());
        model.addAttribute("metodosPago", MetodoPago.values());
    }
}
