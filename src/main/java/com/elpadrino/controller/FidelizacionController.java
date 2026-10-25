package com.elpadrino.controller;

import com.elpadrino.model.Fidelizacion;
import com.elpadrino.service.FidelizacionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedHashMap;
import java.util.Map;

/** Pantalla 11: programa de fidelización (reporte R06). */
@Controller
public class FidelizacionController {

    private final FidelizacionService fidelizacionService;

    public FidelizacionController(FidelizacionService fidelizacionService) {
        this.fidelizacionService = fidelizacionService;
    }

    @GetMapping("/fidelizacion")
    public String index(Model model) {
        model.addAttribute("resumen", fidelizacionService.resumen());
        return "fidelizacion/index";
    }

    /** Estado de la tarjeta de un cliente en JSON, usado por la pantalla de venta. */
    @GetMapping("/fidelizacion/cliente/{idCliente}")
    @ResponseBody
    public Map<String, Object> estadoCliente(@PathVariable Integer idCliente) {
        Map<String, Object> estado = new LinkedHashMap<>();
        Fidelizacion tarjeta = fidelizacionService.buscarPorCliente(idCliente).orElse(null);
        estado.put("cortes", tarjeta == null ? 0 : tarjeta.getCortesTarjeta());
        estado.put("requeridos", Fidelizacion.CORTES_REQUERIDOS);
        estado.put("corteGratis", tarjeta != null && tarjeta.isCorteGratisDisponible());
        return estado;
    }
}
