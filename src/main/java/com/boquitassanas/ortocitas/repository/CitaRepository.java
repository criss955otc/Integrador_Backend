package com.boquitassanas.ortocitas.repository;

import com.boquitassanas.ortocitas.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findByEstado_CodigoIgnoreCaseOrderByHorario_FechaAscHorario_HoraInicioAsc(String estadoCodigo);

    List<Cita> findByPacienteIdOrderByCreadoEnDesc(Long pacienteId);

    Optional<Cita> findFirstByPaciente_CedulaAndEstado_CodigoIgnoreCaseOrderByHorario_FechaAsc(
            String cedula, String estadoCodigo);

    List<Cita> findByPaciente_CedulaOrderByCreadoEnDesc(String cedula);

    List<Cita> findAllByOrderByHorario_FechaDescHorario_HoraInicioDesc();

    List<Cita> findByPaciente_NombresContainingIgnoreCaseOrPaciente_ApellidosContainingIgnoreCaseOrPaciente_CedulaContainingOrderByHorario_FechaDesc(
            String nombres, String apellidos, String cedula);
}
