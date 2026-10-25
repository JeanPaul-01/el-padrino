package com.elpadrino.service;

import com.elpadrino.dto.DiaAgenda;
import com.elpadrino.exception.RecursoNoEncontradoException;
import com.elpadrino.exception.ReglaNegocioException;
import com.elpadrino.model.*;
import com.elpadrino.repository.CitaRepository;
import com.elpadrino.util.Validaciones;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Gestión de citas (proceso TO-BE "Gestión de Citas").
 * Regla principal: no se permiten dos citas activas en el mismo horario para el mismo barbero.
 */
@Service
@Transactional(readOnly = true)
public class CitaService {

    public static final LocalTime HORA_APERTURA = LocalTime.of(9, 0);
    public static final LocalTime HORA_CIERRE = LocalTime.of(20, 0);
    public static final int MINUTOS_POR_TURNO = 30;
    /** Días máximos de anticipación para reservar. */
    public static final int DIAS_MAXIMOS_RESERVA = 60;

    private final CitaRepository citaRepository;
    private final ClienteService clienteService;
    private final BarberoService barberoService;
    private final ServicioService servicioService;

    public CitaService(CitaRepository citaRepository, ClienteService clienteService,
                       BarberoService barberoService, ServicioService servicioService) {
        this.citaRepository = citaRepository;
        this.clienteService = clienteService;
        this.barberoService = barberoService;
        this.servicioService = servicioService;
    }

    public Cita obtener(Integer id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cita", id));
    }

    /** Todos los turnos del día según el horario de atención. */
    public List<LocalTime> turnosDelDia() {
        List<LocalTime> turnos = new ArrayList<>();
        for (LocalTime t = HORA_APERTURA; t.isBefore(HORA_CIERRE); t = t.plusMinutes(MINUTOS_POR_TURNO)) {
            turnos.add(t);
        }
        return turnos;
    }

    /** Horarios libres de un barbero en una fecha (solo muestra horarios disponibles). */
    public List<LocalTime> horariosDisponibles(Integer idBarbero, LocalDate fecha) {
        if (idBarbero == null || fecha == null || fecha.isBefore(LocalDate.now()) || !esDiaLaborable(fecha)) {
            return List.of();
        }
        Set<LocalTime> ocupados = citaRepository
                .findByBarberoIdBarberoAndFechaAndEstadoNot(idBarbero, fecha, EstadoCita.CANCELADA)
                .stream().map(Cita::getHora).collect(Collectors.toSet());
        LocalDateTime ahora = LocalDateTime.now();
        return turnosDelDia().stream()
                .filter(t -> !ocupados.contains(t))
                .filter(t -> fecha.atTime(t).isAfter(ahora))
                .toList();
    }

    /** Registra una cita validando campos obligatorios, horario y cruces. */
    @Transactional
    public Cita registrar(Integer idCliente, Integer idBarbero, Integer idServicio,
                          LocalDate fecha, LocalTime hora, String observaciones) {
        Validaciones.obligatorio(idCliente, "cliente");
        Validaciones.obligatorio(idBarbero, "barbero");
        Validaciones.obligatorio(idServicio, "servicio");
        Validaciones.obligatorio(fecha, "fecha");
        Validaciones.obligatorio(hora, "hora");

        Cliente cliente = clienteService.obtener(idCliente);
        Barbero barbero = barberoService.obtener(idBarbero);
        Servicio servicio = servicioService.obtener(idServicio);

        if (!barbero.isActivo()) {
            throw new ReglaNegocioException("El barbero " + barbero.getNombres() + " no está disponible");
        }
        if (!servicio.isActivo()) {
            throw new ReglaNegocioException("El servicio " + servicio.getNombre() + " no está disponible");
        }
        validarFechaHora(fecha, hora);
        if (citaRepository.existsByBarberoIdBarberoAndFechaAndHoraAndEstadoNot(
                idBarbero, fecha, hora, EstadoCita.CANCELADA)) {
            throw new ReglaNegocioException("El horario " + hora + " del " + fecha
                    + " ya está ocupado para " + barbero.getNombres() + ". Elija otro horario");
        }

        Cita cita = new Cita();
        cita.setCliente(cliente);
        cita.setBarbero(barbero);
        cita.setServicio(servicio);
        cita.setFecha(fecha);
        cita.setHora(hora);
        cita.setEstado(EstadoCita.PENDIENTE);
        cita.setObservaciones(observaciones == null || observaciones.isBlank() ? null : observaciones.trim());
        return citaRepository.save(cita);
    }

    /**
     * Reserva hecha por el propio cliente (pantalla 5): identifica al cliente por su teléfono
     * o lo registra, y crea la cita en una sola transacción.
     */
    @Transactional
    public Cita reservarEnLinea(String nombres, String telefono, String correo, Integer idBarbero,
                                Integer idServicio, LocalDate fecha, LocalTime hora) {
        Validaciones.obligatorio(nombres, "nombres");
        Cliente cliente = clienteService.buscarORegistrar(nombres, telefono, correo);
        return registrar(cliente.getIdCliente(), idBarbero, idServicio, fecha, hora, "Reserva en línea");
    }

    private void validarFechaHora(LocalDate fecha, LocalTime hora) {
        if (fecha.atTime(hora).isBefore(LocalDateTime.now())) {
            throw new ReglaNegocioException("No se puede registrar una cita en una fecha u hora pasada");
        }
        if (!esDiaLaborable(fecha)) {
            throw new ReglaNegocioException("La barbería atiende de lunes a sábado. Elija otra fecha");
        }
        if (fecha.isAfter(LocalDate.now().plusDays(DIAS_MAXIMOS_RESERVA))) {
            throw new ReglaNegocioException("Solo se aceptan reservas hasta con " + DIAS_MAXIMOS_RESERVA + " días de anticipación");
        }
        if (hora.isBefore(HORA_APERTURA) || !hora.isBefore(HORA_CIERRE)
                || hora.getMinute() % MINUTOS_POR_TURNO != 0) {
            throw new ReglaNegocioException("La hora debe estar entre " + HORA_APERTURA + " y " + HORA_CIERRE
                    + " en turnos de " + MINUTOS_POR_TURNO + " minutos");
        }
    }

    /** Confirmar o cancelar una cita desde el calendario. */
    @Transactional
    public Cita cambiarEstado(Integer idCita, EstadoCita nuevoEstado) {
        Cita cita = obtener(idCita);
        if (!cita.getEstado().isModificable()) {
            throw new ReglaNegocioException("La cita ya está " + cita.getEstado().getEtiqueta().toLowerCase()
                    + " y no puede modificarse");
        }
        if (nuevoEstado == EstadoCita.ATENDIDA) {
            throw new ReglaNegocioException("Una cita se marca como atendida al registrar su venta");
        }
        cita.setEstado(nuevoEstado);
        return cita;
    }

    /** Se invoca desde VentaService cuando se cobra la cita. */
    @Transactional
    public void marcarAtendida(Integer idCita) {
        Cita cita = obtener(idCita);
        if (!cita.getEstado().isModificable()) {
            throw new ReglaNegocioException("La cita seleccionada ya fue atendida o cancelada");
        }
        cita.setEstado(EstadoCita.ATENDIDA);
    }

    /** Calendario semanal (lunes a sábado) con filtro opcional por barbero. */
    public List<DiaAgenda> agendaSemanal(LocalDate cualquierDia, Integer idBarbero) {
        LocalDate lunes = inicioDeSemana(cualquierDia);
        LocalDate sabado = lunes.plusDays(5);
        Map<LocalDate, List<Cita>> porDia = citaRepository.buscarPorRango(lunes, sabado, idBarbero)
                .stream().collect(Collectors.groupingBy(Cita::getFecha));
        List<DiaAgenda> semana = new ArrayList<>();
        for (LocalDate d = lunes; !d.isAfter(sabado); d = d.plusDays(1)) {
            semana.add(new DiaAgenda(d, porDia.getOrDefault(d, List.of())));
        }
        return semana;
    }

    /** La barbería atiende de lunes a sábado. */
    public static boolean esDiaLaborable(LocalDate fecha) {
        return fecha.getDayOfWeek() != DayOfWeek.SUNDAY;
    }

    public static LocalDate inicioDeSemana(LocalDate fecha) {
        LocalDate base = fecha == null ? LocalDate.now() : fecha;
        return base.with(DayOfWeek.MONDAY);
    }

    public List<Cita> citasDeHoy() {
        return citaRepository.findByFechaAndEstadoNotOrderByHoraAsc(LocalDate.now(), EstadoCita.CANCELADA);
    }

    public List<Cita> historialCliente(Integer idCliente) {
        return citaRepository.findByClienteIdClienteOrderByFechaDescHoraDesc(idCliente);
    }
}
