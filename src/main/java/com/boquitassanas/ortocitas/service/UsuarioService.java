package com.boquitassanas.ortocitas.service;

import com.boquitassanas.ortocitas.dto.PersonalDTO;
import com.boquitassanas.ortocitas.model.Rol;
import com.boquitassanas.ortocitas.model.Usuario;
import com.boquitassanas.ortocitas.repository.RolRepository;
import com.boquitassanas.ortocitas.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario crear(String codigoRol, PersonalDTO dto) {
        if (!StringUtils.hasText(dto.password())) {
            throw new IllegalArgumentException("La contraseña es obligatoria al crear un usuario nuevo");
        }

        if (usuarioRepository.findByCedulaAndActivoTrue(dto.cedula()).isPresent()) {
            throw new DataIntegrityViolationException("Ya existe un usuario activo con esta cédula");
        }

        if (StringUtils.hasText(dto.email()) && usuarioRepository.existsByEmailIgnoreCase(dto.email())) {
            throw new DataIntegrityViolationException("Ya existe un usuario con este correo");
        }

        Rol rol = obtenerRol(codigoRol);

        Usuario usuario = Usuario.builder()
                .cedula(dto.cedula())
                .nombres(dto.nombres())
                .apellidos(dto.apellidos())
                .email(StringUtils.hasText(dto.email()) ? dto.email() : null)
                .passwordHash(passwordEncoder.encode(dto.password()))
                .rol(rol)
                .activo(true)
                .build();

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario actualizar(Long id, String codigoRol, PersonalDTO dto) {
        Usuario usuario = obtenerPorIdYRol(id, codigoRol);

        if (!usuario.getCedula().equals(dto.cedula()) && usuarioRepository.findByCedulaAndActivoTrue(dto.cedula()).isPresent()) {
            throw new DataIntegrityViolationException("Ya existe otro usuario con esa cédula");
        }

        if (StringUtils.hasText(dto.email()) && usuarioRepository.existsByEmailIgnoreCaseAndIdNot(dto.email(), id)) {
            throw new DataIntegrityViolationException("Ya existe otro usuario con ese correo");
        }

        usuario.setCedula(dto.cedula());
        usuario.setNombres(dto.nombres());
        usuario.setApellidos(dto.apellidos());
        usuario.setEmail(StringUtils.hasText(dto.email()) ? dto.email() : null);

        if (StringUtils.hasText(dto.password())) {
            usuario.setPasswordHash(passwordEncoder.encode(dto.password()));
        }

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void eliminar(Long id, String codigoRol) {
        Usuario usuario = obtenerPorIdYRol(id, codigoRol);
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarPorRol(String codigoRol) {
        return usuarioRepository.findByRol_CodigoIgnoreCaseAndActivoTrueOrderByApellidosAscNombresAsc(codigoRol);
    }

    @Transactional(readOnly = true)
    public List<Usuario> buscarPorRolYNombre(String codigoRol, String texto) {
        return usuarioRepository
                .findByRol_CodigoIgnoreCaseAndActivoTrueAndNombresContainingIgnoreCaseOrRol_CodigoIgnoreCaseAndActivoTrueAndApellidosContainingIgnoreCase(
                        codigoRol, texto, codigoRol, texto);
    }

    private Rol obtenerRol(String codigoRol) {
        return rolRepository.findByCodigoIgnoreCase(codigoRol)
                .orElseThrow(() -> new NoSuchElementException("Rol no encontrado: " + codigoRol));
    }

    private Usuario obtenerPorIdYRol(Long id, String codigoRol) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        if (usuario.getRol() == null || !codigoRol.equalsIgnoreCase(usuario.getRol().getCodigo())) {
            throw new NoSuchElementException("Usuario no encontrado con el rol esperado");
        }
        return usuario;
    }
}
