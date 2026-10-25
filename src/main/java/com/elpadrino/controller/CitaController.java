package com.elpadrino.controller;

import com.elpadrino.exception.ReglaNegocioException;
import com.elpadrino.model.EstadoCita;
import com.elpadrino.service.BarberoService;
import com.elpadrino.service.CitaService;
import com.elpadrino.service.ClienteService;
import com.elpadrino.service.ServicioService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/** Pantallas 3 (Calendario de citas) y 4 (Nueva cita). */
@Controller
@RequestMapping("/citas")
public class CitaController {

    private final CitaService citaService;
    private final ClienteService clienteService;
    private final BarberoService barberoService;
    private final ServicioService servicioService;

    public CitaController(CitaService citaService, ClienteService clienteService,
                          BarberoService barberoService, ServicioService servicioService) {
        this.citaService = citaService;
        this.clienteService = clienteService;
        this.barberoService = barberoService;
        this.servicioService = servicioService;
    }

    @GetMapping
    public String calendario(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate semana,
                             @RequestParam(required = false) Integer barbero, Model model) {
        LocalDate lunes = CitaService.inicioDeSemana(semana);
        model.addAttribute("dias", citaService.agendaSemanal(lunes, barbero));
        model.addAttribute("lunes", lunes);
        model.addAttribute("semanaAnterior", lunes.minusWeeks(1));
        model.addAttribute("semanaSiguiente", lunes.plusWeeks(1));
        model.addAttribute("barberos", barberoService.listar());
        model.addAttribute("barberoSeleccionado", barbero);
        model.addAttribute("estados", EstadoCita.values());
        return "citas/calendario";
    }

    @GetMapping("/nueva")
    public String nueva(@RequestParam(required = false) Integer clienteId,
                        @RequestParam(required = false) Integer barberoId,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                        Model model) {
        cargarFormulario(model);
        model.addAttribute("clienteId", clienteId);
        model.addAttribute("barberoId", barberoId);
        model.addAttribute("fecha", fecha == null ? LocalDate.now() : fecha);
        return "citas/form";
    }

    @PostMapping
    public String registrar(@RequestParam(required = false) Integer clienteId,
                            @RequestParam(required = false) Integer barberoId,
                            @RequestParam(required = false) Integer servicioId,
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                            @RequestParam(required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime hora,
                            @RequestParam(required = false) String observaciones,
                            Model model, RedirectAttributes flash) {
        try {
            var cita = citaService.registrar(clienteId, barberoId, servicioId, fecha, hora, observaciones);
            flash.addFlashAttribute("ok", "Cita registrada para " + cita.getCliente().getNombres()
                    + " el " + cita.getFecha() + " a las " + cita.getHora());
            return "redirect:/citas?semana=" + cita.getFecha() + "&barbero=" + cita.getBarbero().getIdBarbero();
        } catch (ReglaNegocioException e) {
            cargarFormulario(model);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("clienteId", clienteId);
            model.addAttribute("barberoId", barberoId);
            model.addAttribute("servicioId", servicioId);
            model.addAttribute("fecha", fecha == null ? LocalDate.now() : fecha);
            model.addAttribute("observaciones", observaciones);
            return "citas/form";
        }
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Integer id, @RequestParam EstadoCita estado,
                                @RequestParam(required = false) String volver, RedirectAttributes flash) {
        try {
            var cita = citaService.cambiarEstado(id, estado);
            flash.addFlashAttribute("ok", "La cita de " + cita.getCliente().getNombres() + " ahora está "
                    + cita.getEstado().getEtiqueta().toLowerCase());
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:" + (volver != null && volver.startsWith("/") ? volver : "/citas");
    }

    /** "Atender" una cita lleva al registro de venta con los datos de la cita. */
    @GetMapping("/{id}/atender")
    public String atender(@PathVariable Integer id) {
        return "redirect:/ventas/nueva?citaId=" + id;
    }

    /** Horarios libres en formato JSON para el formulario de cita. */
    @GetMapping("/horarios")
    @ResponseBody
    public List<String> horarios(@RequestParam Integer barberoId,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return citaService.horariosDisponibles(barberoId, fecha).stream().map(LocalTime::toString).toList();
    }

    private void cargarFormulario(Model model) {
        model.addAttribute("clientes", clienteService.listar());
        model.addAttribute("barberos", barberoService.listarActivos());
        model.addAttribute("servicios", servicioService.listarActivos());
        model.addAttribute("hoy", LocalDate.now());
    }
}
