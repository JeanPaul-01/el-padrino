package com.elpadrino.controller;

import com.elpadrino.exception.ReglaNegocioException;
import com.elpadrino.model.Servicio;
import com.elpadrino.service.ServicioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Catálogo de servicios y precios. */
@Controller
@RequestMapping("/servicios")
public class ServicioController {

    private final ServicioService servicioService;

    public ServicioController(ServicioService servicioService) {
        this.servicioService = servicioService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) Integer editar, Model model) {
        model.addAttribute("servicios", servicioService.listar());
        model.addAttribute("servicio", editar == null ? new Servicio() : servicioService.obtener(editar));
        return "servicios/index";
    }

    @PostMapping
    public String guardar(@ModelAttribute Servicio servicio, Model model, RedirectAttributes flash) {
        try {
            servicioService.guardar(servicio);
            flash.addFlashAttribute("ok", "Servicio " + servicio.getNombre() + " guardado correctamente");
            return "redirect:/servicios";
        } catch (ReglaNegocioException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("servicios", servicioService.listar());
            model.addAttribute("servicio", servicio);
            return "servicios/index";
        }
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Integer id, RedirectAttributes flash) {
        servicioService.cambiarEstado(id);
        flash.addFlashAttribute("ok", "Estado del servicio actualizado");
        return "redirect:/servicios";
    }
}
