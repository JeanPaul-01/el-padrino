package com.elpadrino.util;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Pruebas unitarias de la exportación a Excel con Apache POI. */
class ExcelExporterTest {

    @Test
    @DisplayName("El Excel generado contiene encabezados y datos")
    void generaEncabezadosYFilas() throws Exception {
        byte[] archivo = ExcelExporter.exportar("Inventario",
                List.of("Producto", "Stock"),
                List.of(Arrays.asList("Cera mate", 4), Arrays.asList("Shampoo", new BigDecimal("10"))));

        try (XSSFWorkbook libro = new XSSFWorkbook(new ByteArrayInputStream(archivo))) {
            Sheet hoja = libro.getSheet("Inventario");
            assertEquals("Producto", hoja.getRow(0).getCell(0).getStringCellValue());
            assertEquals("Cera mate", hoja.getRow(1).getCell(0).getStringCellValue());
            assertEquals(4.0, hoja.getRow(1).getCell(1).getNumericCellValue());
            assertEquals(2, hoja.getLastRowNum());
        }
    }

    @Test
    @DisplayName("Rechaza una fila con menos columnas que los encabezados")
    void rechazaFilaIncompleta() {
        assertThrows(IllegalArgumentException.class, () -> ExcelExporter.exportar("Inventario",
                List.of("Producto", "Stock"), List.of(List.of("Cera mate"))));
    }
}
