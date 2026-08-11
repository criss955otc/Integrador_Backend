package com.boquitassanas.ortocitas.dto;

public record LoginResponse(
        String token,
        String cedula,
        String nombres,
        String apellidos,
        String rol
) {}
