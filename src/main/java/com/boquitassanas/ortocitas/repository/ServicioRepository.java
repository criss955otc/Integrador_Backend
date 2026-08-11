package com.boquitassanas.ortocitas.repository;

import com.boquitassanas.ortocitas.model.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ServicioRepository extends JpaRepository<Servicio, UUID> {
    List<Servicio> findByActivoTrue();
}
