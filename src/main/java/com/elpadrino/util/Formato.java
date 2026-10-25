package com.elpadrino.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Formatos usados en pantallas y reportes (sección 8.6.2 del informe):
 * fechas en dd/mm/aaaa y montos en soles con dos decimales (S/ 1,234.50).
 * Se expone en las vistas JSP como el objeto "f", por ejemplo: ${f.soles(venta.total)}.
 */
public class Formato {

    public static final Locale PERU = new Locale("es", "PE");
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter MES = DateTimeFormatter.ofPattern("MM/yyyy");

    public String fecha(LocalDate fecha) {
        return fecha == null ? "" : fecha.format(FECHA);
    }

    public String fechaHora(LocalDateTime fechaHora) {
        return fechaHora == null ? "" : fechaHora.format(FECHA_HORA);
    }

    public String hora(LocalTime hora) {
        return hora == null ? "" : hora.format(HORA);
    }

    public String mes(LocalDate fecha) {
        return fecha == null ? "" : fecha.format(MES);
    }

    /** Nombre corto del día con la fecha, por ejemplo "Lun 05/10". */
    public String dia(LocalDate fecha) {
        if (fecha == null) {
            return "";
        }
        String nombre = fecha.getDayOfWeek().getDisplayName(TextStyle.SHORT, PERU).replace(".", "");
        return Character.toUpperCase(nombre.charAt(0)) + nombre.substring(1)
                + " " + fecha.format(DateTimeFormatter.ofPattern("dd/MM"));
    }

    public String soles(BigDecimal monto) {
        return "S/ " + numero(monto);
    }

    public static String numero(BigDecimal monto) {
        BigDecimal valor = monto == null ? BigDecimal.ZERO : monto.setScale(2, RoundingMode.HALF_UP);
        DecimalFormat formato = new DecimalFormat("#,##0.00", DecimalFormatSymbols.getInstance(Locale.US));
        return formato.format(valor);
    }

    /** Valor ISO (aaaa-mm-dd) para los campos input type="date". */
    public String iso(LocalDate fecha) {
        return fecha == null ? "" : fecha.toString();
    }

    public boolean esHoy(LocalDate fecha) {
        return LocalDate.now().equals(fecha);
    }
}
