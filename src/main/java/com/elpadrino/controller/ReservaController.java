package com.elpadrino.controller;

import com.elpadrino.exception.ReglaNegocioException;
import com.elpadrino.model.Cita;
import com.elpadrino.service.BarberoService;
import com.elpadrino.service.CitaService;
import com.elpadrino.service.ServicioService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/** Pantalla 5: Reserva en línea (acceso público para el cliente, sin iniciar sesión). */
@Controller
@RequestMapping("/reserva")
public class ReservaController {

    private final CitaService citaService;
    private final BarberoService barberoService;
    private final ServicioService servicioService;

    public ReservaController(CitaService citaService,
                             BarberoService barberoService, ServicioService servicioService) {
        this.citaService = citaService;
        this.barberoService = barberoService;
        this.servicioService = servicioService;
    }

    @GetMapping
    public String formulario(Model model) {
        cargarCatalogos(model);
        return "reserva/form";
    }

    @GetMapping("/horarios")
    @ResponseBody
    public List<String> horarios(@RequestParam Integer barberoId,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return citaService.horariosDisponibles(barberoId, fecha).stream().map(LocalTime::toString).toList();
    }

    @PostMapping
    public String reservar(@RequestParam(required = false) String nombres,
                           @RequestParam(required = false) String telefono,
                           @RequestParam(required = false) String correo,
                           @RequestParam(required = false) Integer servicioId,
                           @RequestParam(required = false) Integer barberoId,
                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                           @RequestParam(required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime hora,
                           Model model) {
        try {
            Cita cita = citaService.reservarEnLinea(nombres, telefono, correo, barberoId, servicioId, fecha, hora);
            model.addAttribute("cita", cita);
            return "reserva/confirmacion";
        } catch (ReglaNegocioException e) {
            cargarCatalogos(model);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("nombres", nombres);
            model.addAttribute("telefono", telefono);
            model.addAttribute("correo", correo);
            model.addAttribute("servicioId", servicioId);
            model.addAttribute("barberoId", barberoId);
            model.addAttribute("fecha", fecha);
            return "reserva/form";
        }
    }

    private void cargarCatalogos(Model model) {
        model.addAttribute("servicios", servicioService.listarActivos());
        model.addAttribute("barberos", barberoService.listarActivos());
        model.addAttribute("hoy", LocalDate.now());
        model.addAttribute("maximo", LocalDate.now().plusDays(CitaService.DIAS_MAXIMOS_RESERVA));
    }
}
