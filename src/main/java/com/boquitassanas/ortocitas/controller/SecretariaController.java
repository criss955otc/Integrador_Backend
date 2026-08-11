package com.boquitassanas.ortocitas.controller;

import com.boquitassanas.ortocitas.dto.ActualizarCitaRequest;
import com.boquitassanas.ortocitas.dto.CambiarEstadoCitaRequest;
import com.boquitassanas.ortocitas.dto.CrearCitaInternaRequest;
import com.boquitassanas.ortocitas.model.Cita;
import com.boquitassanas.ortocitas.model.Paciente;
import com.boquitassanas.ortocitas.service.CitaService;
import com.boquitassanas.ortocitas.service.PacienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Endpoints para el rol SECRETARIA: CRUD completo de Citas y búsqueda de pacientes
 * (para ubicar a quién pertenece una cita), pero sin CRUD de pacientes ni de
 * odontólogos, ni acceso a historiales clínicos. El CRUD de cuentas de Secretaria
 * vive en /api/personal (compartido con ODONTOLOGO).
 */
@RestController
@RequestMapping("/api/secretaria")
@RequiredArgsConstructor
public class SecretariaController {

    private final CitaService citaService;
    private final PacienteService pacienteService;

    // ---- CRUD de citas ----

    @PostMapping("/citas")
    public ResponseEntity<Cita> crearCita(@Valid @RequestBody CrearCitaInternaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(citaService.crearCitaManual(request));
    }

    @GetMapping("/citas")
    public ResponseEntity<List<Cita>> listarCitas() {
        return ResponseEntity.ok(citaService.listarTodas());
    }

    @GetMapping("/citas/pendientes")
    public ResponseEntity<List<Cita>> citasPendientes() {
        return ResponseEntity.ok(citaService.listarPendientes());
    }

    @GetMapping("/citas/buscar")
    public ResponseEntity<List<Cita>> buscarCitas(@RequestParam String q) {
        return ResponseEntity.ok(citaService.buscarPorPacienteNombreOCedula(q));
    }

    @GetMapping("/citas/{citaId}")
    public ResponseEntity<Cita> obtenerCita(@PathVariable UUID citaId) {
        return ResponseEntity.ok(citaService.obtenerPorId(citaId));
    }

    @PutMapping("/citas/{citaId}")
    public ResponseEntity<Cita> actualizarCita(
            @PathVariable UUID citaId,
            @RequestBody ActualizarCitaRequest request) {
        return ResponseEntity.ok(citaService.actualizarCita(citaId, request));
    }

    @PatchMapping("/citas/{citaId}/estado")
    public ResponseEntity<Cita> cambiarEstadoCita(
            @PathVariable UUID citaId,
            @Valid @RequestBody CambiarEstadoCitaRequest request) {
        return ResponseEntity.ok(citaService.cambiarEstado(citaId, request.nuevoEstado()));
    }

    @DeleteMapping("/citas/{citaId}")
    public ResponseEntity<Void> eliminarCita(@PathVariable UUID citaId) {
        citaService.eliminarCita(citaId);
        return ResponseEntity.noContent().build();
    }

    // ---- Búsqueda de pacientes (solo consulta, sin CRUD) ----

    @GetMapping("/pacientes/buscar")
    public ResponseEntity<List<Paciente>> buscarPacientes(@RequestParam String q) {
        return ResponseEntity.ok(pacienteService.buscar(q));
    }
}
