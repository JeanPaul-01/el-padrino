package com.elpadrino.service;

import com.elpadrino.dto.SesionUsuario;
import com.elpadrino.dto.VentaRequest;
import com.elpadrino.exception.RecursoNoEncontradoException;
import com.elpadrino.exception.ReglaNegocioException;
import com.elpadrino.model.*;
import com.elpadrino.repository.VentaRepository;
import com.elpadrino.util.Validaciones;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Registro de ventas y pagos (proceso TO-BE "Gestión de Ventas").
 * Al confirmar una venta: calcula el total, descuenta el stock de los productos,
 * aplica el corte gratis cuando corresponde, actualiza la fidelización y marca la cita como atendida.
 */
@Service
@Transactional(readOnly = true)
public class VentaService {

    public static final String TIPO_SERVICIO = "SERVICIO";
    public static final String TIPO_PRODUCTO = "PRODUCTO";

    private final VentaRepository ventaRepository;
    private final ClienteService clienteService;
    private final BarberoService barberoService;
    private final ServicioService servicioService;
    private final InventarioService inventarioService;
    private final FidelizacionService fidelizacionService;
    private final CitaService citaService;
    private final UsuarioService usuarioService;

    public VentaService(VentaRepository ventaRepository, ClienteService clienteService,
                        BarberoService barberoService, ServicioService servicioService,
                        InventarioService inventarioService, FidelizacionService fidelizacionService,
                        CitaService citaService, UsuarioService usuarioService) {
        this.ventaRepository = ventaRepository;
        this.clienteService = clienteService;
        this.barberoService = barberoService;
        this.servicioService = servicioService;
        this.inventarioService = inventarioService;
        this.fidelizacionService = fidelizacionService;
        this.citaService = citaService;
        this.usuarioService = usuarioService;
    }

    public Venta obtener(Integer id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Venta", id));
    }

    public List<Venta> listarPorPeriodo(LocalDate desde, LocalDate hasta) {
        return ventaRepository.buscarPorPeriodo(desde.atStartOfDay(), hasta.plusDays(1).atStartOfDay());
    }

    public List<Venta> historialCliente(Integer idCliente) {
        return ventaRepository.buscarPorCliente(idCliente);
    }

    @Transactional
    public Venta registrar(VentaRequest request, SesionUsuario sesion) {
        Validaciones.obligatorio(request.getClienteId(), "cliente");
        Validaciones.obligatorio(request.getBarberoId(), "barbero");
        Validaciones.obligatorio(request.getMetodoPago(), "método de pago");
        if (request.getNumeroLineas() == 0) {
            throw new ReglaNegocioException("Agregue al menos un servicio o producto a la venta");
        }

        Cliente cliente = clienteService.obtener(request.getClienteId());
        Barbero barbero = barberoService.obtener(request.getBarberoId());
        Usuario usuario = usuarioService.obtener(sesion.getIdUsuario());

        if (request.getCitaId() != null) {
            Cita cita = citaService.obtener(request.getCitaId());
            if (!cita.getCliente().getIdCliente().equals(cliente.getIdCliente())) {
                throw new ReglaNegocioException("La cita seleccionada pertenece a otro cliente");
            }
        }

        Venta venta = new Venta();
        venta.setCliente(cliente);
        venta.setBarbero(barbero);
        venta.setUsuario(usuario);
        venta.setMetodoPago(request.getMetodoPago());
        venta.setFecha(LocalDateTime.now());

        int serviciosVendidos = 0;
        DetalleVenta primerServicio = null;
        for (int i = 0; i < request.getNumeroLineas(); i++) {
            DetalleVenta detalle = crearDetalle(request, i);
            venta.agregarDetalle(detalle);
            if (detalle.isEsServicio()) {
                serviciosVendidos += detalle.getCantidad();
                if (primerServicio == null) {
                    primerServicio = detalle;
                }
            }
        }

        // Programa de fidelización: si el cliente acumuló 5 cortes, el siguiente servicio es gratis.
        boolean aplicaCorteGratis = primerServicio != null && fidelizacionService.tieneCorteGratis(cliente.getIdCliente());
        BigDecimal descuento = aplicaCorteGratis ? primerServicio.getPrecioUnitario() : BigDecimal.ZERO;
        venta.setDescuento(descuento);
        venta.setTotal(venta.getSubtotal().subtract(descuento).max(BigDecimal.ZERO));

        Venta guardada = ventaRepository.save(venta);

        for (DetalleVenta detalle : guardada.getDetalles()) {
            if (!detalle.isEsServicio()) {
                inventarioService.descontarStock(detalle.getProducto(), detalle.getCantidad());
            }
        }
        if (serviciosVendidos > 0) {
            int serviciosPagados = serviciosVendidos - (aplicaCorteGratis ? 1 : 0);
            fidelizacionService.registrarServicios(cliente, serviciosPagados, aplicaCorteGratis);
        }
        if (request.getCitaId() != null) {
            citaService.marcarAtendida(request.getCitaId());
        }
        return guardada;
    }

    private DetalleVenta crearDetalle(VentaRequest request, int indice) {
        String tipo = request.getTipos().get(indice);
        Integer itemId = indice < request.getItemIds().size() ? request.getItemIds().get(indice) : null;
        Integer cantidad = indice < request.getCantidades().size() ? request.getCantidades().get(indice) : null;
        if (itemId == null) {
            throw new ReglaNegocioException("Seleccione el servicio o producto de la línea " + (indice + 1));
        }
        int unidades = Validaciones.enteroPositivo(cantidad, "cantidad de la línea " + (indice + 1));

        if (TIPO_PRODUCTO.equalsIgnoreCase(tipo)) {
            Producto producto = inventarioService.obtenerProducto(itemId);
            if (!producto.isActivo()) {
                throw new ReglaNegocioException("El producto " + producto.getNombre() + " no está a la venta");
            }
            int stock = inventarioService.obtenerInventario(itemId).getStockActual();
            if (stock < unidades) {
                throw new ReglaNegocioException("Stock insuficiente de " + producto.getNombre()
                        + ": disponible " + stock + ", solicitado " + unidades);
            }
            return DetalleVenta.deProducto(producto, unidades);
        }
        Servicio servicio = servicioService.obtener(itemId);
        if (!servicio.isActivo()) {
            throw new ReglaNegocioException("El servicio " + servicio.getNombre() + " no está disponible");
        }
        return DetalleVenta.deServicio(servicio, unidades);
    }
}
