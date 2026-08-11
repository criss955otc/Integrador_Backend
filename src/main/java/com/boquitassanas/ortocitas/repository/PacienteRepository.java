package com.boquitassanas.ortocitas.repository;

import com.boquitassanas.ortocitas.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PacienteRepository extends JpaRepository<Paciente, UUID> {
    Optional<Paciente> findByCedula(String cedula);

    List<Paciente> findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCaseOrCedulaContaining(
            String nombres, String apellidos, String cedula);
}
