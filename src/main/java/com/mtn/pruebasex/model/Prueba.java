package com.mtn.pruebasex.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "prueba")
public class Prueba {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "tipo", nullable = false)
    private String tipo;  // Puede ser "SEPA", "MIDEUC", "Diagnóstico", etc.

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;  // Fecha en que se aplicó la prueba
}