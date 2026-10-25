package com.elpadrino.service;

import com.elpadrino.exception.RecursoNoEncontradoException;
import com.elpadrino.exception.ReglaNegocioException;
import com.elpadrino.model.Cliente;
import com.elpadrino.repository.ClienteRepository;
import com.elpadrino.util.Validaciones;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Gestión de clientes: búsqueda, registro sin duplicados y actualización. */
@Service
@Transactional(readOnly = true)
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    /** Búsqueda por nombre o teléfono (pantalla 6). */
    public List<Cliente> buscar(String texto) {
        if (texto == null || texto.isBlank()) {
            return clienteRepository.findAllByOrderByNombresAsc();
        }
        String q = texto.trim();
        return clienteRepository.findByNombresContainingIgnoreCaseOrTelefonoContainingOrderByNombresAsc(q, q);
    }

    public List<Cliente> listar() {
        return clienteRepository.findAllByOrderByNombresAsc();
    }

    public Cliente obtener(Integer id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente", id));
    }

    /** Registra o actualiza un cliente evitando teléfonos duplicados. */
    @Transactional
    public Cliente guardar(Cliente datos) {
        String nombres = Validaciones.obligatorio(datos.getNombres(), "nombres");
        String telefono = Validaciones.telefono(datos.getTelefono());
        String correo = Validaciones.correoOpcional(datos.getCorreo());

        boolean duplicado = datos.getIdCliente() == null
                ? clienteRepository.existsByTelefono(telefono)
                : clienteRepository.existsByTelefonoAndIdClienteNot(telefono, datos.getIdCliente());
        if (duplicado) {
            throw new ReglaNegocioException("Ya existe un cliente registrado con el teléfono " + telefono);
        }

        Cliente cliente = datos.getIdCliente() == null ? new Cliente() : obtener(datos.getIdCliente());
        cliente.setNombres(nombres);
        cliente.setTelefono(telefono);
        cliente.setCorreo(correo);
        return clienteRepository.save(cliente);
    }

    /**
     * Usado por la reserva en línea: si el teléfono ya existe se reutiliza el cliente,
     * de lo contrario se registra uno nuevo.
     */
    @Transactional
    public Cliente buscarORegistrar(String nombres, String telefono, String correo) {
        String telefonoValido = Validaciones.telefono(telefono);
        return clienteRepository.findByTelefono(telefonoValido).orElseGet(() -> {
            Cliente nuevo = new Cliente();
            nuevo.setNombres(nombres);
            nuevo.setTelefono(telefonoValido);
            nuevo.setCorreo(correo);
            return guardar(nuevo);
        });
    }
}
