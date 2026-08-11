package com.boquitassanas.ortocitas.repository;

import com.boquitassanas.ortocitas.model.EstadoHorario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface EstadoHorarioRepository extends JpaRepository<EstadoHorario, UUID> {
    Optional<EstadoHorario> findByCodigoIgnoreCase(String codigo);
}
