package com.elpadrino.repository;

import com.elpadrino.model.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Integer> {
    List<DetalleVenta> findByVentaIdVentaOrderByIdDetalleAsc(Integer idVenta);
}
