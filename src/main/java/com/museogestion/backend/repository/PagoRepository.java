package com.museogestion.backend.repository;

import com.museogestion.backend.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagoRepository extends JpaRepository<Pago, Long> {
}