package com.boquitassanas.ortocitas.repository;

import com.boquitassanas.ortocitas.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCedulaAndActivoTrue(String cedula);

    List<Usuario> findByRol_CodigoIgnoreCaseAndActivoTrueOrderByApellidosAscNombresAsc(String rolCodigo);

    List<Usuario> findByRol_CodigoIgnoreCaseAndActivoTrueAndNombresContainingIgnoreCaseOrRol_CodigoIgnoreCaseAndActivoTrueAndApellidosContainingIgnoreCase(
            String rolCodigo1, String nombres, String rolCodigo2, String apellidos);

    boolean existsByCedulaAndIdNot(String cedula, Long id);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}
