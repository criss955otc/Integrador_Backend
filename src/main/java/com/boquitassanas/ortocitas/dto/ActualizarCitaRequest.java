package com.boquitassanas.ortocitas.dto;


public record ActualizarCitaRequest(
        Long nuevoHorarioId,
        Long servicioId,
        String estado,
        String notas
) {}
