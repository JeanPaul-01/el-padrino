package com.elpadrino.controller;

import com.elpadrino.exception.ReglaNegocioException;
import com.elpadrino.model.Producto;
import com.elpadrino.service.InventarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Pantalla 9: registro y edición de productos. */
@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final InventarioService inventarioService;

    public ProductoController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("producto", new Producto());
        return "inventario/producto-form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Integer id, Model model) {
        model.addAttribute("producto", inventarioService.obtenerProducto(id));
        return "inventario/producto-form";
    }

    @PostMapping
    public String guardar(@ModelAttribute Producto producto, @RequestParam(required = false) Integer stockInicial,
                          Model model, RedirectAttributes flash) {
        try {
            Producto guardado = inventarioService.guardarProducto(producto, stockInicial);
            flash.addFlashAttribute("ok", "Producto " + guardado.getNombre() + " guardado correctamente");
            return "redirect:/inventario";
        } catch (ReglaNegocioException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("producto", producto);
            model.addAttribute("stockInicial", stockInicial);
            return "inventario/producto-form";
        }
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Integer id, RedirectAttributes flash) {
        inventarioService.cambiarEstadoProducto(id);
        flash.addFlashAttribute("ok", "Estado del producto actualizado");
        return "redirect:/inventario";
    }
}
