package com.boquitassanas.ortocitas.model;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "estado_cita")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoCita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String codigo;

    @Column(nullable = false, length = 80)
    private String nombre;
}
