package com.elpadrino.repository;

import com.elpadrino.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Integer> {

    /** Ventas de un periodo con sus detalles (base de los reportes R01, R02, R03 y R05). */
    @Query("""
           select distinct v from Venta v
           left join fetch v.detalles d
           left join fetch d.servicio
           left join fetch d.producto
           where v.fecha >= :desde and v.fecha < :hasta
           order by v.fecha desc
           """)
    List<Venta> buscarPorPeriodo(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    @Query("select distinct v from Venta v left join fetch v.detalles where v.cliente.idCliente = :idCliente order by v.fecha desc")
    List<Venta> buscarPorCliente(@Param("idCliente") Integer idCliente);

    @Query("select coalesce(sum(v.total), 0) from Venta v where v.fecha >= :desde and v.fecha < :hasta")
    BigDecimal sumarTotal(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    /** Canjes de corte gratis: ventas con descuento mayor a cero (reporte R06). */
    long countByDescuentoGreaterThanAndFechaGreaterThanEqual(BigDecimal descuento, LocalDateTime desde);
}
