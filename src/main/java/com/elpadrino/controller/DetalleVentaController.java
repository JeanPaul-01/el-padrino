package com.elpadrino.controller;

import com.elpadrino.service.VentaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/** Comprobante de una venta con sus detalles (servicios y productos). */
@Controller
public class DetalleVentaController {

    private final VentaService ventaService;

    public DetalleVentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping("/ventas/{id}")
    public String detalle(@PathVariable Integer id, Model model) {
        model.addAttribute("venta", ventaService.obtener(id));
        return "ventas/detalle";
    }
}
