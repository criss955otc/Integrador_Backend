package com.boquitassanas.ortocitas.service;

import com.boquitassanas.ortocitas.dto.LoginRequest;
import com.boquitassanas.ortocitas.dto.LoginResponse;
import com.boquitassanas.ortocitas.model.Usuario;
import com.boquitassanas.ortocitas.repository.UsuarioRepository;
import com.boquitassanas.ortocitas.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.cedula(), request.password())
            );
        } catch (Exception ex) {
            throw new BadCredentialsException("Cédula o contraseña incorrectas");
        }

        Usuario usuario = usuarioRepository.findByCedulaAndActivoTrue(request.cedula())
                .orElseThrow(() -> new BadCredentialsException("Cédula o contraseña incorrectas"));

        String token = jwtUtil.generarToken(
                usuario.getCedula(),
                Map.of(
                        "rol", usuario.getRol().getCodigo(),
                        "nombres", usuario.getNombres(),
                        "apellidos", usuario.getApellidos()
                )
        );

        return new LoginResponse(
                token,
                usuario.getCedula(),
                usuario.getNombres(),
                usuario.getApellidos(),
                usuario.getRol().getCodigo()
        );
    }
}
