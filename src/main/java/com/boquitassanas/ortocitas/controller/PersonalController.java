package com.boquitassanas.ortocitas.controller;

import com.boquitassanas.ortocitas.dto.PersonalDTO;
import com.boquitassanas.ortocitas.model.Usuario;
import com.boquitassanas.ortocitas.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * CRUD de cuentas de secretaria. Vive en una ruta separada (/api/personal) porque, a
 * diferencia del resto, la comparten tres roles a la vez: ADMIN, ODONTOLOGO y SECRETARIA
 * (ver matriz de permisos en SecurityConfig).
 */
@RestController
@RequestMapping("/api/personal")
@RequiredArgsConstructor
public class PersonalController {

    private final UsuarioService usuarioService;

    @PostMapping("/secretarias")
    public ResponseEntity<Usuario> crearSecretaria(@Valid @RequestBody PersonalDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crear("SECRETARIA", dto));
    }

    @GetMapping("/secretarias")
    public ResponseEntity<List<Usuario>> listarSecretarias() {
        return ResponseEntity.ok(usuarioService.listarPorRol("SECRETARIA"));
    }

    @GetMapping("/secretarias/buscar")
    public ResponseEntity<List<Usuario>> buscarSecretarias(@RequestParam String q) {
        return ResponseEntity.ok(usuarioService.buscarPorRolYNombre("SECRETARIA", q));
    }

    @PutMapping("/secretarias/{id}")
    public ResponseEntity<Usuario> actualizarSecretaria(
            @PathVariable UUID id,
            @Valid @RequestBody PersonalDTO dto) {
        return ResponseEntity.ok(usuarioService.actualizar(id, "SECRETARIA", dto));
    }

    @DeleteMapping("/secretarias/{id}")
    public ResponseEntity<Void> eliminarSecretaria(@PathVariable UUID id) {
        usuarioService.eliminar(id, "SECRETARIA");
        return ResponseEntity.noContent().build();
    }
}
