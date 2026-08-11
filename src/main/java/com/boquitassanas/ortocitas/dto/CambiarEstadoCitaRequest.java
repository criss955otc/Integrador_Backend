package com.boquitassanas.ortocitas.dto;

import jakarta.validation.constraints.NotBlank;

public record CambiarEstadoCitaRequest(
        @NotBlank(message = "El nuevo estado es obligatorio")
        String nuevoEstado
) {}
