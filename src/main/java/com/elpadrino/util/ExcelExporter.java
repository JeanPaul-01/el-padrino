package com.elpadrino.util;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Genera archivos Excel (.xlsx) para los reportes del sistema con Apache POI.
 * <p>
 * Es genérico: cualquier reporte nuevo (stock bajo, ingresos, clientes frecuentes)
 * lo reutiliza pasando sus encabezados y filas, sin modificar esta clase.
 */
public final class ExcelExporter {

    private static final Logger log = LoggerFactory.getLogger(ExcelExporter.class);
    private static final int ANCHO_COLUMNA = 22 * 256;

    private ExcelExporter() {
        // Clase de utilidades: no se instancia.
    }

    /**
     * Crea un libro de Excel con una hoja, una fila de encabezados y las filas de datos.
     *
     * @param nombreHoja  nombre de la hoja (se ajusta a las reglas de Excel)
     * @param encabezados títulos de las columnas
     * @param filas       datos; cada fila debe tener tantos valores como encabezados
     * @return el contenido del archivo .xlsx
     * @throws IllegalArgumentException si faltan encabezados o una fila no coincide
     */
    public static byte[] exportar(String nombreHoja, List<String> encabezados, List<? extends List<?>> filas) {
        Preconditions.checkArgument(StringUtils.isNotBlank(nombreHoja), "La hoja necesita un nombre");
        Preconditions.checkArgument(encabezados != null && !encabezados.isEmpty(), "Faltan los encabezados");
        Preconditions.checkNotNull(filas, "La lista de filas no puede ser nula");

        try (XSSFWorkbook libro = new XSSFWorkbook();
             ByteArrayOutputStream salida = new ByteArrayOutputStream()) {

            Sheet hoja = libro.createSheet(WorkbookUtil.createSafeSheetName(nombreHoja));
            CellStyle estiloEncabezado = libro.createCellStyle();
            Font negrita = libro.createFont();
            negrita.setBold(true);
            estiloEncabezado.setFont(negrita);

            Row filaEncabezado = hoja.createRow(0);
            for (int i = 0; i < encabezados.size(); i++) {
                Cell celda = filaEncabezado.createCell(i);
                celda.setCellValue(encabezados.get(i));
                celda.setCellStyle(estiloEncabezado);
                hoja.setColumnWidth(i, ANCHO_COLUMNA);
            }

            int numeroFila = 1;
            for (List<?> fila : filas) {
                Preconditions.checkArgument(fila.size() == encabezados.size(),
                        "La fila %s tiene %s valores y se esperaban %s", numeroFila, fila.size(), encabezados.size());
                Row filaExcel = hoja.createRow(numeroFila++);
                for (int j = 0; j < fila.size(); j++) {
                    escribir(filaExcel.createCell(j), fila.get(j));
                }
            }

            libro.write(salida);
            log.info("Excel '{}' generado con {} filas", nombreHoja, filas.size());
            return salida.toByteArray();
        } catch (IOException e) {
            log.error("No se pudo generar el Excel '{}'", nombreHoja, e);
            throw new UncheckedIOException("No se pudo generar el archivo Excel", e);
        }
    }

    private static void escribir(Cell celda, Object valor) {
        if (valor == null) {
            celda.setBlank();
        } else if (valor instanceof BigDecimal decimal) {
            celda.setCellValue(decimal.doubleValue());
        } else if (valor instanceof Number numero) {
            celda.setCellValue(numero.doubleValue());
        } else if (valor instanceof Boolean booleano) {
            celda.setCellValue(booleano);
        } else {
            celda.setCellValue(valor.toString());
        }
    }
}
