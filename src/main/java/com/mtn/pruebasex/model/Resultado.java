package com.mtn.pruebasex.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "resultado")
public class Resultado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "alumno_id", nullable = false)
    private Alumno alumno;

    @ManyToOne
    @JoinColumn(name = "prueba_id", nullable = false)
    private Prueba prueba;

    @Column(name = "puntaje", nullable = false)
    private Double puntaje;

    @Column(name = "area", nullable = false)
    private String area;
}