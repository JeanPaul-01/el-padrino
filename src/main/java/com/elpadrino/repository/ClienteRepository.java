package com.elpadrino.repository;

import com.elpadrino.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    List<Cliente> findAllByOrderByNombresAsc();
    List<Cliente> findByNombresContainingIgnoreCaseOrTelefonoContainingOrderByNombresAsc(String nombres, String telefono);
    Optional<Cliente> findByTelefono(String telefono);
    boolean existsByTelefono(String telefono);
    boolean existsByTelefonoAndIdClienteNot(String telefono, Integer idCliente);
}
