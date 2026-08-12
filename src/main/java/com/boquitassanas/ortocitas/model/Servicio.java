package com.boquitassanas.ortocitas.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "servicios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    private BigDecimal precio;

    @Builder.Default
    private boolean activo = true;
}
