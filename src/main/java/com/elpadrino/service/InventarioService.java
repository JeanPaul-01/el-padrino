package com.elpadrino.service;

import com.elpadrino.dto.InventarioRequest;
import com.elpadrino.exception.RecursoNoEncontradoException;
import com.elpadrino.exception.ReglaNegocioException;
import com.elpadrino.model.Inventario;
import com.elpadrino.model.Producto;
import com.elpadrino.repository.InventarioRepository;
import com.elpadrino.repository.ProductoRepository;
import com.elpadrino.util.Validaciones;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Gestión de productos e inventario (proceso TO-BE "Gestión de Inventario").
 * El stock actual vive solo en la tabla INVENTARIO para evitar datos duplicados.
 */
@Service
@Transactional(readOnly = true)
public class InventarioService {

    private final ProductoRepository productoRepository;
    private final InventarioRepository inventarioRepository;

    public InventarioService(ProductoRepository productoRepository, InventarioRepository inventarioRepository) {
        this.productoRepository = productoRepository;
        this.inventarioRepository = inventarioRepository;
    }

    public List<Inventario> listarInventario() {
        return inventarioRepository.listarConProducto();
    }

    public List<Producto> listarProductosActivos() {
        return productoRepository.findByEstadoTrueOrderByNombreAsc();
    }

    /** Reporte R04 y alertas del dashboard. */
    public List<Inventario> listarStockBajo() {
        return inventarioRepository.listarStockBajo();
    }

    public Producto obtenerProducto(Integer id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", id));
    }

    public Inventario obtenerInventario(Integer idProducto) {
        return inventarioRepository.findByProductoIdProducto(idProducto)
                .orElseThrow(() -> new RecursoNoEncontradoException("Inventario del producto", idProducto));
    }

    /**
     * Crea o edita un producto (pantalla 9). Al crearlo se registra su stock inicial.
     * Valida precio y stock numéricos mayores o iguales a 0 y nombres no repetidos.
     */
    @Transactional
    public Producto guardarProducto(Producto datos, Integer stockInicial) {
        String nombre = Validaciones.obligatorio(datos.getNombre(), "nombre");
        Validaciones.montoNoNegativo(datos.getPrecio(), "precio");
        Validaciones.enteroNoNegativo(datos.getStockMinimo(), "stock mínimo");

        boolean nuevo = datos.getIdProducto() == null;
        boolean duplicado = nuevo
                ? productoRepository.existsByNombreIgnoreCase(nombre)
                : productoRepository.existsByNombreIgnoreCaseAndIdProductoNot(nombre, datos.getIdProducto());
        if (duplicado) {
            throw new ReglaNegocioException("Ya existe un producto llamado " + nombre);
        }

        Producto producto = nuevo ? new Producto() : obtenerProducto(datos.getIdProducto());
        producto.setNombre(nombre);
        producto.setDescripcion(datos.getDescripcion());
        producto.setCategoria(datos.getCategoria());
        producto.setPrecio(datos.getPrecio());
        producto.setStockMinimo(datos.getStockMinimo());
        if (producto.getEstado() == null) {
            producto.setEstado(true);
        }
        producto = productoRepository.save(producto);

        if (nuevo) {
            Inventario inventario = new Inventario();
            inventario.setProducto(producto);
            inventario.setStockActual(stockInicial == null ? 0 : Validaciones.enteroNoNegativo(stockInicial, "stock inicial"));
            inventarioRepository.save(inventario);
            producto.setInventario(inventario);
        }
        return producto;
    }

    @Transactional
    public void cambiarEstadoProducto(Integer idProducto) {
        Producto producto = obtenerProducto(idProducto);
        producto.setEstado(!producto.isActivo());
    }

    /** Ajuste manual de stock: entrada por compra o salida por merma. */
    @Transactional
    public Inventario ajustarStock(InventarioRequest request) {
        Validaciones.obligatorio(request.getProductoId(), "producto");
        int cantidad = Validaciones.enteroPositivo(request.getCantidad(), "cantidad");
        Inventario inventario = obtenerInventario(request.getProductoId());

        int nuevoStock = request.getTipo() == InventarioRequest.TipoMovimiento.SALIDA
                ? inventario.getStockActual() - cantidad
                : inventario.getStockActual() + cantidad;
        if (nuevoStock < 0) {
            throw new ReglaNegocioException("No hay stock suficiente: solo quedan "
                    + inventario.getStockActual() + " unidades de " + inventario.getProducto().getNombre());
        }
        inventario.setStockActual(nuevoStock);
        return inventarioRepository.save(inventario);
    }

    /** Descuenta stock al confirmar una venta (regla: no se vende sin stock). */
    @Transactional
    public void descontarStock(Producto producto, int cantidad) {
        Inventario inventario = obtenerInventario(producto.getIdProducto());
        if (inventario.getStockActual() < cantidad) {
            throw new ReglaNegocioException("Stock insuficiente de " + producto.getNombre()
                    + ": disponible " + inventario.getStockActual() + ", solicitado " + cantidad);
        }
        inventario.setStockActual(inventario.getStockActual() - cantidad);
        inventarioRepository.save(inventario);
    }
}
