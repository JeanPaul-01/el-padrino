package com.elpadrino.service;

import com.elpadrino.exception.RecursoNoEncontradoException;
import com.elpadrino.model.Barbero;
import com.elpadrino.repository.BarberoRepository;
import com.elpadrino.util.Validaciones;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Mantenimiento de barberos. */
@Service
@Transactional(readOnly = true)
public class BarberoService {

    private final BarberoRepository barberoRepository;

    public BarberoService(BarberoRepository barberoRepository) {
        this.barberoRepository = barberoRepository;
    }

    public List<Barbero> listar() {
        return barberoRepository.findAllByOrderByNombresAsc();
    }

    public List<Barbero> listarActivos() {
        return barberoRepository.findByEstadoTrueOrderByNombresAsc();
    }

    public Barbero obtener(Integer id) {
        return barberoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Barbero", id));
    }

    @Transactional
    public Barbero guardar(Barbero datos) {
        Barbero barbero = datos.getIdBarbero() == null ? new Barbero() : obtener(datos.getIdBarbero());
        barbero.setNombres(Validaciones.obligatorio(datos.getNombres(), "nombres"));
        barbero.setTelefono(datos.getTelefono() == null || datos.getTelefono().isBlank()
                ? null : Validaciones.telefono(datos.getTelefono()));
        barbero.setEspecialidad(datos.getEspecialidad());
        if (barbero.getEstado() == null) {
            barbero.setEstado(true);
        }
        return barberoRepository.save(barbero);
    }

    @Transactional
    public void cambiarEstado(Integer id) {
        Barbero barbero = obtener(id);
        barbero.setEstado(!barbero.isActivo());
    }
}
