package com.mtn.pruebasex.controller;

import com.mtn.pruebasex.model.Resultado;
import com.mtn.pruebasex.service.ResultadoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/resultados")
@AllArgsConstructor
public class ResultadoController {

    private final ResultadoService resultadoService;

    @GetMapping
    public ResponseEntity<List<Resultado>> getAllResultados() {
        List<Resultado> resultados = resultadoService.getAllResultados();
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resultado> getResultadoById(@PathVariable Long id) {
        Optional<Resultado> resultadoOptional = resultadoService.getResultadoById(id);
        return resultadoOptional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Resultado> createResultado(@RequestBody Resultado resultado) {
        if (resultado.getId() != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        // Additional validation might be needed here:
        // e.g., check if Alumno and Prueba exist before saving.
        // For now, assuming service layer or DB constraints handle this.
        Resultado savedResultado = resultadoService.saveResultado(resultado);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedResultado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Resultado> updateResultado(@PathVariable Long id, @RequestBody Resultado resultadoDetails) {
        Optional<Resultado> resultadoOptional = resultadoService.getResultadoById(id);
        if (resultadoOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Resultado existingResultado = resultadoOptional.get();
        // Update fields of existingResultado with details from resultadoDetails
        // Resultado model has: alumno, prueba, puntaje, area
        // Lombok @Data generates setters.
        // Typically, you might not change the alumno or prueba of an existing result this way.
        // We will update puntaje and area.
        existingResultado.setPuntaje(resultadoDetails.getPuntaje());
        existingResultado.setArea(resultadoDetails.getArea());

        // If updating Alumno or Prueba references is desired, it would look like:
        // existingResultado.setAlumno(resultadoDetails.getAlumno());
        // existingResultado.setPrueba(resultadoDetails.getPrueba());
        // However, this requires sending full Alumno/Prueba objects in the request
        // or DTOs with their IDs, and then fetching them in the service.
        // For simplicity, this example only updates puntaje and area.

        Resultado updatedResultado = resultadoService.saveResultado(existingResultado);
        return ResponseEntity.ok(updatedResultado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResultado(@PathVariable Long id) {
        Optional<Resultado> resultadoOptional = resultadoService.getResultadoById(id);
        if (resultadoOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        resultadoService.deleteResultado(id);
        return ResponseEntity.noContent().build();
    }
}
