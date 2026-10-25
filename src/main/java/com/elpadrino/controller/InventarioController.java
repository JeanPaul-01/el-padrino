package com.elpadrino.controller;

import com.elpadrino.dto.InventarioRequest;
import com.elpadrino.exception.ReglaNegocioException;
import com.elpadrino.service.InventarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Pantalla 8 (Inventario) y ajuste de stock de la pantalla 9. */
@Controller
@RequestMapping("/inventario")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("inventario", inventarioService.listarInventario());
        model.addAttribute("criticos", inventarioService.listarStockBajo().size());
        return "inventario/lista";
    }

    @GetMapping("/{idProducto}/ajuste")
    public String ajuste(@PathVariable Integer idProducto, Model model) {
        model.addAttribute("item", inventarioService.obtenerInventario(idProducto));
        model.addAttribute("tipos", InventarioRequest.TipoMovimiento.values());
        return "inventario/ajuste";
    }

    @PostMapping("/ajuste")
    public String ajustar(@ModelAttribute InventarioRequest request, RedirectAttributes flash) {
        try {
            var inventario = inventarioService.ajustarStock(request);
            flash.addFlashAttribute("ok", "Stock de " + inventario.getProducto().getNombre()
                    + " actualizado: " + inventario.getStockActual() + " unidades");
            return "redirect:/inventario";
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/inventario/" + request.getProductoId() + "/ajuste";
        }
    }
}
