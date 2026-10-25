package com.elpadrino.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.elpadrino.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    List<Cliente> findAllByOrderByNombresAsc();

    List<Cliente> findByNombresContainingIgnoreCaseOrTelefonoContainingOrCorreoContainingIgnoreCaseOrderByNombresAsc(
            String nombres, String telefono, String correo);

    Optional<Cliente> findByTelefono(String telefono);

    boolean existsByTelefono(String telefono);

    boolean existsByTelefonoAndIdClienteNot(String telefono, Integer idCliente);
}