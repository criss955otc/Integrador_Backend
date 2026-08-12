package com.boquitassanas.ortocitas.service;

import com.boquitassanas.ortocitas.dto.PacienteDTO;
import com.boquitassanas.ortocitas.model.Paciente;
import com.boquitassanas.ortocitas.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    @Transactional
    public Paciente registrarNuevoPaciente(PacienteDTO dto) {
        pacienteRepository.findByCedula(dto.cedula()).ifPresent(p -> {
            throw new DataIntegrityViolationException("Ya existe un paciente registrado con esta cédula");
        });

        return pacienteRepository.save(Paciente.builder()
                .cedula(dto.cedula())
                .nombres(dto.nombres())
                .apellidos(dto.apellidos())
                .telefono(dto.telefono())
                .email(dto.email())
                .build());
    }

    @Transactional
    public Paciente actualizarPaciente(Long pacienteId, PacienteDTO dto) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new NoSuchElementException("Paciente no encontrado"));

        if (!paciente.getCedula().equals(dto.cedula())) {
            pacienteRepository.findByCedula(dto.cedula()).ifPresent(otro -> {
                throw new DataIntegrityViolationException("Ya existe otro paciente registrado con esa cédula");
            });
        }

        paciente.setCedula(dto.cedula());
        paciente.setNombres(dto.nombres());
        paciente.setApellidos(dto.apellidos());
        paciente.setTelefono(dto.telefono());
        paciente.setEmail(dto.email());

        return pacienteRepository.save(paciente);
    }

    @Transactional(readOnly = true)
    public Paciente obtenerPorId(Long pacienteId) {
        return pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new NoSuchElementException("Paciente no encontrado"));
    }

    @Transactional(readOnly = true)
    public List<Paciente> buscar(String texto) {
        return pacienteRepository.findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCaseOrCedulaContaining(
                texto, texto, texto);
    }

    @Transactional
    public void eliminar(Long pacienteId) {
        if (!pacienteRepository.existsById(pacienteId)) {
            throw new NoSuchElementException("Paciente no encontrado");
        }
        pacienteRepository.deleteById(pacienteId);
    }
}
