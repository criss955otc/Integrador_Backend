package com.boquitassanas.ortocitas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record CrearCitaInternaRequest(
        @NotBlank(message = "La cédula es obligatoria")
        @Pattern(regexp = "^[0-9]{10}$", message = "La cédula debe tener exactamente 10 dígitos")
        String cedula,

        @NotBlank(message = "Los nombres del paciente son obligatorios")
        String nombresPaciente,

        @NotBlank(message = "Los apellidos del paciente son obligatorios")
        String apellidosPaciente,

        String telefonoPaciente,

        @NotNull(message = "Debe seleccionar un horario disponible")
        UUID horarioId,

        UUID servicioId,

        String notas
) {}
