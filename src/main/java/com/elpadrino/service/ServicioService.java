package com.elpadrino.service;

import com.elpadrino.exception.RecursoNoEncontradoException;
import com.elpadrino.exception.ReglaNegocioException;
import com.elpadrino.model.Servicio;
import com.elpadrino.repository.ServicioRepository;
import com.elpadrino.util.Validaciones;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Mantenimiento del catálogo de servicios y precios. */
@Service
@Transactional(readOnly = true)
public class ServicioService {

    private final ServicioRepository servicioRepository;

    public ServicioService(ServicioRepository servicioRepository) {
        this.servicioRepository = servicioRepository;
    }

    public List<Servicio> listar() {
        return servicioRepository.findAllByOrderByNombreAsc();
    }

    public List<Servicio> listarActivos() {
        return servicioRepository.findByEstadoTrueOrderByNombreAsc();
    }

    public Servicio obtener(Integer id) {
        return servicioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Servicio", id));
    }

    @Transactional
    public Servicio guardar(Servicio datos) {
        String nombre = Validaciones.obligatorio(datos.getNombre(), "nombre");
        
        // Validar longitud del nombre
        if (nombre.length() > 100) {
            throw new ReglaNegocioException(
                "El nombre del servicio no puede superar los 100 caracteres"
            );
        }

        // Validar longitud de la descripcion
        String descripcion = datos.getDescripcion();
        if (descripcion != null && descripcion.length() > 255) {
            throw new ReglaNegocioException(
                "La descripcion no puede superar los 255 caracteres"
            );
        }
        
        // Validar precio
        Validaciones.montoNoNegativo(datos.getPrecio(), "precio");


        boolean duplicado = datos.getIdServicio() == null
                ? servicioRepository.existsByNombreIgnoreCase(nombre)
                : servicioRepository.existsByNombreIgnoreCaseAndIdServicioNot(nombre, datos.getIdServicio());
        if (duplicado) {
            throw new ReglaNegocioException("Ya existe un servicio llamado " + nombre);
        }

        Servicio servicio = datos.getIdServicio() == null ? new Servicio() : obtener(datos.getIdServicio());
        servicio.setNombre(nombre);
        servicio.setDescripcion(datos.getDescripcion());
        servicio.setPrecio(datos.getPrecio());
        if (servicio.getEstado() == null) {
            servicio.setEstado(true);
        }
        return servicioRepository.save(servicio);
    }

    @Transactional
    public void cambiarEstado(Integer id) {
        Servicio servicio = obtener(id);
        servicio.setEstado(!servicio.isActivo());
    }
}
