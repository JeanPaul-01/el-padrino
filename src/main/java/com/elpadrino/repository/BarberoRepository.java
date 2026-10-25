package com.elpadrino.repository;

import com.elpadrino.model.Barbero;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BarberoRepository extends JpaRepository<Barbero, Integer> {
    List<Barbero> findAllByOrderByNombresAsc();
    List<Barbero> findByEstadoTrueOrderByNombresAsc();
}
