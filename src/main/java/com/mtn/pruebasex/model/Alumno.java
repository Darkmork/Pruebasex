package com.mtn.pruebasex.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "alumno")  // Nombre de la tabla en PostgreSQL
public class Alumno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "apellido", nullable = false)
    private String apellido;

    @Column(name = "curso", nullable = false)
    private String curso;

    @OneToMany(mappedBy = "alumno", cascade = CascadeType.ALL)
    private List<Resultado> resultados;
}