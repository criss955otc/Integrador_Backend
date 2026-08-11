package com.boquitassanas.ortocitas.repository;

import com.boquitassanas.ortocitas.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface RolRepository extends JpaRepository<Rol, UUID> {
    Optional<Rol> findByCodigoIgnoreCase(String codigo);
}
