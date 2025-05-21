package com.mtn.pruebasex.controller;

import com.mtn.pruebasex.model.Prueba;
import com.mtn.pruebasex.service.PruebaService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pruebas")
@AllArgsConstructor
public class PruebaController {

    private final PruebaService pruebaService;

    @GetMapping
    public ResponseEntity<List<Prueba>> getAllPruebas() {
        List<Prueba> pruebas = pruebaService.getAllPruebas();
        return ResponseEntity.ok(pruebas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Prueba> getPruebaById(@PathVariable Long id) {
        Optional<Prueba> pruebaOptional = pruebaService.getPruebaById(id);
        return pruebaOptional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Prueba> createPrueba(@RequestBody Prueba prueba) {
        if (prueba.getId() != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Prueba savedPrueba = pruebaService.savePrueba(prueba);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPrueba);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Prueba> updatePrueba(@PathVariable Long id, @RequestBody Prueba pruebaDetails) {
        Optional<Prueba> pruebaOptional = pruebaService.getPruebaById(id);
        if (pruebaOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Prueba existingPrueba = pruebaOptional.get();
        // Update fields of existingPrueba with details from pruebaDetails
        // Prueba model has: nombre, tipo, fecha
        // Lombok @Data generates setters: setNombre, setTipo, setFecha
        existingPrueba.setNombre(pruebaDetails.getNombre());
        existingPrueba.setTipo(pruebaDetails.getTipo());
        existingPrueba.setFecha(pruebaDetails.getFecha());

        Prueba updatedPrueba = pruebaService.savePrueba(existingPrueba);
        return ResponseEntity.ok(updatedPrueba);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrueba(@PathVariable Long id) {
        Optional<Prueba> pruebaOptional = pruebaService.getPruebaById(id);
        if (pruebaOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        pruebaService.deletePrueba(id);
        return ResponseEntity.noContent().build();
    }
}
