package com.elpadrino.controller;

import com.elpadrino.dto.InventarioRequest;
import com.elpadrino.exception.ReglaNegocioException;
import com.elpadrino.service.InventarioService;
import com.elpadrino.util.ExcelExporter;
import com.google.common.collect.ImmutableList;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Pantalla 8 (Inventario), ajuste de stock de la pantalla 9 y exportación del reporte R04 a Excel. */
@Controller
@RequestMapping("/inventario")
public class InventarioController {

    private static final List<String> ENCABEZADOS_EXCEL = ImmutableList.of(
            "Producto", "Categoría", "Precio (S/)", "Stock actual", "Stock mínimo", "Estado");

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

    /**
     * Descarga el inventario completo en Excel y marca los productos con stock bajo
     * (reporte R04). Usa Apache POI a través de {@link ExcelExporter}.
     *
     * @return el archivo .xlsx como descarga
     */
    @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportarExcel() {
        List<List<Object>> filas = new ArrayList<>();
        for (var inventario : inventarioService.listarInventario()) {
            var producto = inventario.getProducto();
            int stock = inventario.getStockActual() == null ? 0 : inventario.getStockActual();
            String estado = stock == 0 ? "Agotado"
                    : stock <= producto.getStockMinimo() ? "Stock bajo" : "Normal";
            if (!producto.isActivo()) {
                estado += " (inactivo)";
            }
            filas.add(Arrays.asList(producto.getNombre(), producto.getCategoria(), producto.getPrecio(),
                    stock, producto.getStockMinimo(), estado));
        }

        byte[] excel = ExcelExporter.exportar("Inventario", ENCABEZADOS_EXCEL, filas);
        String nombreArchivo = "inventario_" + LocalDate.now() + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreArchivo + "\"")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }
}
