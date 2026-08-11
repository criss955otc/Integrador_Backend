package com.boquitassanas.ortocitas.dto;

import java.util.UUID;

public record ActualizarCitaRequest(
        UUID nuevoHorarioId,
        UUID servicioId,
        String estado,
        String notas
) {}
