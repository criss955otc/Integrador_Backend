package com.boquitassanas.ortocitas.repository;

import com.boquitassanas.ortocitas.model.EstadoHorario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EstadoHorarioRepository extends JpaRepository<EstadoHorario, Long> {
    Optional<EstadoHorario> findByCodigoIgnoreCase(String codigo);
}
