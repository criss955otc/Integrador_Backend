package com.boquitassanas.ortocitas.repository;

import com.boquitassanas.ortocitas.model.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface EstadoCitaRepository extends JpaRepository<EstadoCita, UUID> {
    Optional<EstadoCita> findByCodigoIgnoreCase(String codigo);
}
