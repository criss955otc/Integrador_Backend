package com.boquitassanas.ortocitas.controller;

import com.boquitassanas.ortocitas.dto.ConsultarCitaRequest;
import com.boquitassanas.ortocitas.dto.RegistrarCitaRequest;
import com.boquitassanas.ortocitas.model.Cita;
import com.boquitassanas.ortocitas.model.HorarioDisponible;
import com.boquitassanas.ortocitas.service.CitaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Endpoints accesibles sin autenticación desde la landing page:
 * consultar cita pendiente, ver horarios disponibles y registrar una nueva cita.
 */
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicoController {

    private final CitaService citaService;

    @PostMapping("/citas/consultar")
    public ResponseEntity<Cita> consultarCita(@Valid @RequestBody ConsultarCitaRequest request) {
        return ResponseEntity.ok(citaService.consultarCitaPendiente(request.cedula()));
    }

    @GetMapping("/horarios")
    public ResponseEntity<List<HorarioDisponible>> horariosDisponibles(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(citaService.horariosDisponibles(fecha));
    }

    @PostMapping("/citas/registrar")
    public ResponseEntity<Cita> registrarCita(@Valid @RequestBody RegistrarCitaRequest request) {
        Cita cita = citaService.registrarCita(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(cita);
    }
}
