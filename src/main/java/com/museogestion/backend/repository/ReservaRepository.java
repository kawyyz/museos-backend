package com.museogestion.backend.repository;

import com.museogestion.backend.model.EstadoReserva;
import com.museogestion.backend.model.Reserva;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByUsuarioIdOrderByCreadaEnDesc(Long usuarioId);

    List<Reserva> findAllByOrderByCreadaEnDesc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Reserva r where r.id = :id")
    Optional<Reserva> findByIdForUpdate(@Param("id") Long id);

    // Personas ya reservadas en un museo y fecha, sin contar las canceladas ni la reserva indicada
    @Query("select coalesce(sum(r.personas), 0L) from Reserva r "
            + "where r.museo.id = :museoId and r.fecha = :fecha "
            + "and r.estado <> :cancelada and r.id <> :excluirId")
    Long personasReservadas(@Param("museoId") Long museoId,
                            @Param("fecha") LocalDate fecha,
                            @Param("cancelada") EstadoReserva cancelada,
                            @Param("excluirId") Long excluirId);
}