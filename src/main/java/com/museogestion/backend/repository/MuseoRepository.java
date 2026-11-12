package com.museogestion.backend.repository;

import com.museogestion.backend.model.Museo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MuseoRepository extends JpaRepository<Museo, Long> {

    List<Museo> findByNombreContainingIgnoreCaseOrZonaContainingIgnoreCase(String nombre, String zona);
}