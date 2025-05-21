package com.mtn.pruebasex.controller;

import com.mtn.pruebasex.model.Alumno;
import com.mtn.pruebasex.service.AlumnoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/alumnos")
@AllArgsConstructor
public class AlumnoController {

    private final AlumnoService alumnoService;

    @GetMapping
    public ResponseEntity<List<Alumno>> getAllAlumnos() {
        List<Alumno> alumnos = alumnoService.getAllAlumnos();
        return ResponseEntity.ok(alumnos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alumno> getAlumnoById(@PathVariable Long id) {
        Optional<Alumno> alumnoOptional = alumnoService.getAlumnoById(id);
        return alumnoOptional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Alumno> createAlumno(@RequestBody Alumno alumno) {
        if (alumno.getId() != null) {
            // Or handle as a bad request if ID should not be present on creation
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); 
        }
        Alumno savedAlumno = alumnoService.saveAlumno(alumno);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAlumno);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Alumno> updateAlumno(@PathVariable Long id, @RequestBody Alumno alumnoDetails) {
        Optional<Alumno> alumnoOptional = alumnoService.getAlumnoById(id);
        if (alumnoOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Alumno existingAlumno = alumnoOptional.get();
        // Update fields of existingAlumno with details from alumnoDetails
        // Alumno model has: nombre, apellido, curso
        // Lombok @Data generates setters: setNombre, setApellido, setCurso
        existingAlumno.setNombre(alumnoDetails.getNombre());
        existingAlumno.setApellido(alumnoDetails.getApellido());
        existingAlumno.setCurso(alumnoDetails.getCurso());
        // Note: We are not updating the 'resultados' list here, 
        // that would typically be handled by a separate endpoint or a more complex logic.

        Alumno updatedAlumno = alumnoService.saveAlumno(existingAlumno);
        return ResponseEntity.ok(updatedAlumno);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlumno(@PathVariable Long id) {
        Optional<Alumno> alumnoOptional = alumnoService.getAlumnoById(id);
        if (alumnoOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        alumnoService.deleteAlumno(id);
        return ResponseEntity.noContent().build();
    }
}
