package com.elpadrino.repository;

import com.elpadrino.model.Cita;
import com.elpadrino.model.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Integer> {

    /** Citas de un rango de fechas (calendario semanal), filtrando opcionalmente por barbero. */
    @Query("""
           select c from Cita c
           where c.fecha between :desde and :hasta
             and (:idBarbero is null or c.barbero.idBarbero = :idBarbero)
           order by c.fecha, c.hora
           """)
    List<Cita> buscarPorRango(@Param("desde") LocalDate desde,
                              @Param("hasta") LocalDate hasta,
                              @Param("idBarbero") Integer idBarbero);

    /** Regla RN-01: un barbero no puede tener dos citas activas en el mismo horario. */
    boolean existsByBarberoIdBarberoAndFechaAndHoraAndEstadoNot(Integer idBarbero, LocalDate fecha,
                                                               LocalTime hora, EstadoCita estado);

    List<Cita> findByBarberoIdBarberoAndFechaAndEstadoNot(Integer idBarbero, LocalDate fecha, EstadoCita estado);

    List<Cita> findByClienteIdClienteOrderByFechaDescHoraDesc(Integer idCliente);

    List<Cita> findByFechaAndEstadoNotOrderByHoraAsc(LocalDate fecha, EstadoCita estado);
}
