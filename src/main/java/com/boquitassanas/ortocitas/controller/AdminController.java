package com.boquitassanas.ortocitas.controller;

import com.boquitassanas.ortocitas.dto.PersonalDTO;
import com.boquitassanas.ortocitas.model.Servicio;
import com.boquitassanas.ortocitas.model.Usuario;
import com.boquitassanas.ortocitas.repository.ServicioRepository;
import com.boquitassanas.ortocitas.repository.UsuarioRepository;
import com.boquitassanas.ortocitas.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Endpoints exclusivos del rol ADMIN: CRUD de odontólogos y de precios/servicios.
 * El administrador hereda además todos los endpoints de /api/odontologo, /api/secretaria
 * y /api/personal, ya que la matriz de permisos lo incluye en esos tres roles.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final ServicioRepository servicioRepository;

    // ---- Precios / servicios ----

    @GetMapping("/servicios")
    public ResponseEntity<List<Servicio>> listarServicios() {
        return ResponseEntity.ok(servicioRepository.findAll());
    }

    @PostMapping("/servicios")
    public ResponseEntity<Servicio> crearServicio(@RequestBody Servicio servicio) {
        servicio.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(servicioRepository.save(servicio));
    }

    @PutMapping("/servicios/{id}")
    public ResponseEntity<Servicio> actualizarServicio(@PathVariable UUID id, @RequestBody Servicio cambios) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new java.util.NoSuchElementException("Servicio no encontrado"));
        servicio.setNombre(cambios.getNombre());
        servicio.setPrecio(cambios.getPrecio());
        servicio.setActivo(cambios.isActivo());
        return ResponseEntity.ok(servicioRepository.save(servicio));
    }

    @DeleteMapping("/servicios/{id}")
    public ResponseEntity<Void> eliminarServicio(@PathVariable UUID id) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new java.util.NoSuchElementException("Servicio no encontrado"));
        servicio.setActivo(false);
        servicioRepository.save(servicio);
        return ResponseEntity.noContent().build();
    }

    // ---- CRUD de odontólogos ----

    @PostMapping("/odontologos")
    public ResponseEntity<Usuario> crearOdontologo(@Valid @RequestBody PersonalDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crear("ODONTOLOGO", dto));
    }

    @GetMapping("/odontologos")
    public ResponseEntity<List<Usuario>> listarOdontologos() {
        return ResponseEntity.ok(usuarioService.listarPorRol("ODONTOLOGO"));
    }

    @GetMapping("/odontologos/buscar")
    public ResponseEntity<List<Usuario>> buscarOdontologos(@RequestParam String q) {
        return ResponseEntity.ok(usuarioService.buscarPorRolYNombre("ODONTOLOGO", q));
    }

    @PutMapping("/odontologos/{id}")
    public ResponseEntity<Usuario> actualizarOdontologo(
            @PathVariable UUID id,
            @Valid @RequestBody PersonalDTO dto) {
        return ResponseEntity.ok(usuarioService.actualizar(id, "ODONTOLOGO", dto));
    }

    @DeleteMapping("/odontologos/{id}")
    public ResponseEntity<Void> eliminarOdontologo(@PathVariable UUID id) {
        usuarioService.eliminar(id, "ODONTOLOGO");
        return ResponseEntity.noContent().build();
    }

    // ---- Listado general (todos los roles, uso administrativo) ----

    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        usuarios.forEach(u -> u.setPasswordHash(null));
        return ResponseEntity.ok(usuarios);
    }
}
