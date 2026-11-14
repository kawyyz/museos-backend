package com.museogestion.backend.repository;

import com.museogestion.backend.model.Museo;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MuseoRepository extends JpaRepository<Museo, Long> {

    List<Museo> findByNombreContainingIgnoreCaseOrZonaContainingIgnoreCase(String nombre, String zona);

    // Bloquea la fila mientras dura la transacción: evita que dos reservas simultáneas superen el cupo
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Museo m where m.id = :id")
    Optional<Museo> findByIdForUpdate(@Param("id") Long id);
}