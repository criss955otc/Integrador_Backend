package com.boquitassanas.ortocitas.controller;

import com.boquitassanas.ortocitas.dto.*;
import com.boquitassanas.ortocitas.model.Cita;
import com.boquitassanas.ortocitas.model.HorarioDisponible;
import com.boquitassanas.ortocitas.model.Paciente;
import com.boquitassanas.ortocitas.model.Servicio;
import com.boquitassanas.ortocitas.repository.CitaRepository;
import com.boquitassanas.ortocitas.repository.ServicioRepository;
import com.boquitassanas.ortocitas.repository.UsuarioRepository;
import com.boquitassanas.ortocitas.service.CitaService;
import com.boquitassanas.ortocitas.service.PacienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Endpoints exclusivos para el rol ODONTOLOGO (y ADMIN, que hereda estos permisos).
 * Matriz de permisos de este controlador: CRUD completo de Pacientes y Citas,
 * más la gestión de horarios (siempre desde la fecha actual en adelante).
 * El CRUD de cuentas de Secretaria vive en /api/personal (compartido con SECRETARIA).
 */
@RestController
@RequestMapping("/api/odontologo")
@RequiredArgsConstructor
public class OdontologoController {

    private final PacienteService pacienteService;
    private final CitaService citaService;
    private final CitaRepository citaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ServicioRepository servicioRepository;

    @GetMapping("/servicios")
    public ResponseEntity<List<Servicio>> listarServicios() {
        return ResponseEntity.ok(servicioRepository.findByActivoTrue());
    }

    // ---- CRUD de pacientes ----

    @PostMapping("/pacientes")
    public ResponseEntity<Paciente> registrarPaciente(@Valid @RequestBody PacienteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pacienteService.registrarNuevoPaciente(dto));
    }

    @GetMapping("/pacientes/{pacienteId}")
    public ResponseEntity<Paciente> obtenerPaciente(@PathVariable UUID pacienteId) {
        return ResponseEntity.ok(pacienteService.obtenerPorId(pacienteId));
    }

    @PutMapping("/pacientes/{pacienteId}")
    public ResponseEntity<Paciente> actualizarPaciente(
            @PathVariable UUID pacienteId,
            @Valid @RequestBody PacienteDTO dto) {
        return ResponseEntity.ok(pacienteService.actualizarPaciente(pacienteId, dto));
    }

    @DeleteMapping("/pacientes/{pacienteId}")
    public ResponseEntity<Void> eliminarPaciente(@PathVariable UUID pacienteId) {
        pacienteService.eliminar(pacienteId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pacientes/buscar")
    public ResponseEntity<List<Paciente>> buscarPacientes(@RequestParam String q) {
        return ResponseEntity.ok(pacienteService.buscar(q));
    }

    @GetMapping("/pacientes/{pacienteId}/historial")
    public ResponseEntity<List<Cita>> historialPaciente(@PathVariable UUID pacienteId) {
        return ResponseEntity.ok(citaRepository.findByPacienteIdOrderByCreadoEnDesc(pacienteId));
    }

    // ---- Horarios (siempre desde la fecha actual en adelante) ----

    @PostMapping("/horarios")
    public ResponseEntity<List<HorarioDisponible>> habilitarHorarios(
            Authentication authentication,
            @Valid @RequestBody HabilitarHorariosRequest request) {
        return ResponseEntity.ok(citaService.habilitarHorarios(idOdontologoAutenticado(authentication), request));
    }

    @GetMapping("/horarios")
    public ResponseEntity<List<HorarioDisponible>> horariosDelOdontologo(
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(citaService.horariosDeOdontologo(idOdontologoAutenticado(authentication), fecha));
    }

    @PatchMapping("/horarios/{horarioId}")
    public ResponseEntity<HorarioDisponible> actualizarHorario(
            @PathVariable UUID horarioId,
            @RequestParam String estado) {
        return ResponseEntity.ok(citaService.actualizarHorario(horarioId, estado));
    }

    @DeleteMapping("/horarios/{horarioId}")
    public ResponseEntity<Void> eliminarHorario(@PathVariable UUID horarioId) {
        citaService.eliminarHorario(horarioId);
        return ResponseEntity.noContent().build();
    }

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

    // authentication.getName() es la cédula (subject del JWT); se resuelve el id real del odontólogo.
    private UUID idOdontologoAutenticado(Authentication authentication) {
        return usuarioRepository.findByCedulaAndActivoTrue(authentication.getName())
                .orElseThrow(() -> new NoSuchElementException("Usuario autenticado no encontrado"))
                .getId();
    }
}
