package com.boquitassanas.ortocitas.repository;

import com.boquitassanas.ortocitas.model.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EstadoCitaRepository extends JpaRepository<EstadoCita, Long> {
    Optional<EstadoCita> findByCodigoIgnoreCase(String codigo);
}
