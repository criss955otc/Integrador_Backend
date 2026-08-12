package com.boquitassanas.ortocitas.model;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "rol_usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String codigo;

    @Column(nullable = false, length = 80)
    private String nombre;
}
