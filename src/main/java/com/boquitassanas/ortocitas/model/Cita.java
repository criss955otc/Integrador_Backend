package com.boquitassanas.ortocitas.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "citas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "odontologo_id")
    private Usuario odontologo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "horario_id", nullable = false, unique = true)
    private HorarioDisponible horario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servicio_id")
    private Servicio servicio;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "estado_cita_id", nullable = false)
    private EstadoCita estado;

    private String notas;

    @Column(name = "creado_en", updatable = false)
    private Instant creadoEn;

    @Column(name = "actualizado_en")
    private Instant actualizadoEn;

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        creadoEn = now;
        actualizadoEn = now;
    }

    @PreUpdate
    void preUpdate() {
        actualizadoEn = Instant.now();
    }
}
