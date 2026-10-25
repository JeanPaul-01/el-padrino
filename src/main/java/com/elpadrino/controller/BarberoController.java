package com.elpadrino.controller;

import com.elpadrino.exception.ReglaNegocioException;
import com.elpadrino.model.Barbero;
import com.elpadrino.service.BarberoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Mantenimiento de barberos. */
@Controller
@RequestMapping("/barberos")
public class BarberoController {

    private final BarberoService barberoService;

    public BarberoController(BarberoService barberoService) {
        this.barberoService = barberoService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) Integer editar, Model model) {
        model.addAttribute("barberos", barberoService.listar());
        model.addAttribute("barbero", editar == null ? new Barbero() : barberoService.obtener(editar));
        return "barberos/index";
    }

    @PostMapping
    public String guardar(@ModelAttribute Barbero barbero, Model model, RedirectAttributes flash) {
        try {
            barberoService.guardar(barbero);
            flash.addFlashAttribute("ok", "Barbero " + barbero.getNombres() + " guardado correctamente");
            return "redirect:/barberos";
        } catch (ReglaNegocioException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("barberos", barberoService.listar());
            model.addAttribute("barbero", barbero);
            return "barberos/index";
        }
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Integer id, RedirectAttributes flash) {
        barberoService.cambiarEstado(id);
        flash.addFlashAttribute("ok", "Estado del barbero actualizado");
        return "redirect:/barberos";
    }
}
