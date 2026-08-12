package com.boquitassanas.ortocitas.repository;

import com.boquitassanas.ortocitas.model.HorarioDisponible;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HorarioDisponibleRepository extends JpaRepository<HorarioDisponible, Long> {
    List<HorarioDisponible> findByFechaAndEstado_CodigoIgnoreCaseOrderByHoraInicioAsc(LocalDate fecha, String estadoCodigo);
    List<HorarioDisponible> findByOdontologoIdAndFechaOrderByHoraInicioAsc(Long odontologoId, LocalDate fecha);
}
