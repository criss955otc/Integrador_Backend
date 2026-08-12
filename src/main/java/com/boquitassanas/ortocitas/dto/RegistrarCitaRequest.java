package com.boquitassanas.ortocitas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;


public record RegistrarCitaRequest(
        @NotBlank(message = "La cédula es obligatoria")
        @Pattern(regexp = "^[0-9]{10}$", message = "La cédula debe tener exactamente 10 dígitos")
        String cedula,

        @NotBlank(message = "Los nombres son obligatorios")
        String nombres,

        @NotBlank(message = "Los apellidos son obligatorios")
        String apellidos,

        String telefono,

        @NotNull(message = "Debe seleccionar un horario disponible")
        Long horarioId
) {}
