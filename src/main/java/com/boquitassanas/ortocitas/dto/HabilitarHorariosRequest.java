package com.boquitassanas.ortocitas.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record HabilitarHorariosRequest(
        @NotNull(message = "La fecha es obligatoria")
        @FutureOrPresent(message = "No se pueden habilitar horarios en fechas pasadas")
        LocalDate fecha,

        @NotNull(message = "La hora de inicio de la jornada es obligatoria")
        LocalTime horaInicioJornada,

        @NotNull(message = "La hora de fin de la jornada es obligatoria")
        LocalTime horaFinJornada
) {}
