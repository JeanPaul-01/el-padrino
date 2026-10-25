package com.elpadrino.repository;

import com.elpadrino.model.Fidelizacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FidelizacionRepository extends JpaRepository<Fidelizacion, Integer> {
    Optional<Fidelizacion> findByClienteIdCliente(Integer idCliente);
    List<Fidelizacion> findAllByOrderByCortesAcumuladosDescPuntosDesc();
}
