package com.boquitassanas.ortocitas.config;

import com.boquitassanas.ortocitas.model.Rol;
import com.boquitassanas.ortocitas.model.Usuario;
import com.boquitassanas.ortocitas.repository.RolRepository;
import com.boquitassanas.ortocitas.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap-admin.cedula:}")
    private String cedula;

    @Value("${app.bootstrap-admin.nombres:}")
    private String nombres;

    @Value("${app.bootstrap-admin.apellidos:}")
    private String apellidos;

    @Value("${app.bootstrap-admin.password:}")
    private String password;

    @Override
    public void run(String... args) {
        if (!StringUtils.hasText(cedula) || !StringUtils.hasText(password)) {
            return;
        }

        if (usuarioRepository.findByCedulaAndActivoTrue(cedula).isPresent()) {
            return;
        }

        Rol rol = rolRepository.findByCodigoIgnoreCase("ADMIN")
                .orElseThrow(() -> new IllegalStateException("No existe el rol ADMIN en la base de datos"));

        usuarioRepository.save(Usuario.builder()
                .cedula(cedula)
                .nombres(StringUtils.hasText(nombres) ? nombres : "Administrador")
                .apellidos(StringUtils.hasText(apellidos) ? apellidos : "Sistema")
                .passwordHash(passwordEncoder.encode(password))
                .rol(rol)
                .activo(true)
                .build());
    }
}
