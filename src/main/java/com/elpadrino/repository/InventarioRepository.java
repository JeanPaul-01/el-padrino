package com.elpadrino.repository;

import com.elpadrino.model.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InventarioRepository extends JpaRepository<Inventario, Integer> {

    Optional<Inventario> findByProductoIdProducto(Integer idProducto);

    @Query("select i from Inventario i join fetch i.producto p order by p.nombre")
    List<Inventario> listarConProducto();

    /** Reporte R04: productos activos con stock actual menor o igual al stock mínimo. */
    @Query("""
           select i from Inventario i join fetch i.producto p
           where p.estado = true and i.stockActual <= p.stockMinimo
           order by i.stockActual asc, (p.stockMinimo - i.stockActual) desc
           """)
    List<Inventario> listarStockBajo();
}
