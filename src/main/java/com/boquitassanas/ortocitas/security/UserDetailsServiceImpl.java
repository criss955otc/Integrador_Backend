package com.boquitassanas.ortocitas.security;

import com.boquitassanas.ortocitas.model.Usuario;
import com.boquitassanas.ortocitas.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String cedula) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCedulaAndActivoTrue(cedula)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado o inactivo: " + cedula));

        return new User(
                usuario.getCedula(),
                usuario.getPasswordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getCodigo()))
        );
    }
}
