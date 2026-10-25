package com.elpadrino.controller;

import com.elpadrino.service.ReporteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

/** Pantalla 2: Dashboard con el resumen del día (reporte R07). */
@Controller
public class HomeController {

    private final ReporteService reporteService;

    public HomeController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/")
    public String inicio() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("resumen", reporteService.resumenDelDia());
        model.addAttribute("hoy", LocalDate.now());
        return "dashboard";
    }
}
