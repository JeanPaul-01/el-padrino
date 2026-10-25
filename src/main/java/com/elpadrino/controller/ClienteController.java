package com.elpadrino.controller;

import com.elpadrino.exception.ReglaNegocioException;
import com.elpadrino.model.Cliente;
import com.elpadrino.service.CitaService;
import com.elpadrino.service.ClienteService;
import com.elpadrino.service.FidelizacionService;
import com.elpadrino.service.VentaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Pantallas 6 (Clientes) y 7 (Perfil e historial del cliente). */
@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final CitaService citaService;
    private final VentaService ventaService;
    private final FidelizacionService fidelizacionService;

    public ClienteController(ClienteService clienteService, CitaService citaService,
                             VentaService ventaService, FidelizacionService fidelizacionService) {
        this.clienteService = clienteService;
        this.citaService = citaService;
        this.ventaService = ventaService;
        this.fidelizacionService = fidelizacionService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("clientes", clienteService.buscar(q));
        model.addAttribute("q", q);
        return "clientes/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "clientes/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Integer id, Model model) {
        model.addAttribute("cliente", clienteService.obtener(id));
        return "clientes/form";
    }

    @PostMapping
    public String guardar(@ModelAttribute Cliente cliente, Model model, RedirectAttributes flash) {
        try {
            Cliente guardado = clienteService.guardar(cliente);
            flash.addFlashAttribute("ok", "Cliente " + guardado.getNombres() + " guardado correctamente");
            return "redirect:/clientes/" + guardado.getIdCliente();
        } catch (ReglaNegocioException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("cliente", cliente);
            return "clientes/form";
        }
    }

    @GetMapping("/{id}")
    public String perfil(@PathVariable Integer id, Model model) {
        model.addAttribute("cliente", clienteService.obtener(id));
        model.addAttribute("citas", citaService.historialCliente(id));
        model.addAttribute("ventas", ventaService.historialCliente(id));
        model.addAttribute("tarjeta", fidelizacionService.buscarPorCliente(id).orElse(null));
        return "clientes/perfil";
    }
}
