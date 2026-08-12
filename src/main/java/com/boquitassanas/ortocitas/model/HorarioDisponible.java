package com.boquitassanas.ortocitas.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "horarios_disponibles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorarioDisponible {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "odontologo_id", nullable = false)
    private Usuario odontologo;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "estado_horario_id", nullable = false)
    private EstadoHorario estado;

    @Column(name = "creado_en", updatable = false)
    private Instant creadoEn;

    @PrePersist
    void prePersist() {
        if (creadoEn == null) creadoEn = Instant.now();
    }
}
