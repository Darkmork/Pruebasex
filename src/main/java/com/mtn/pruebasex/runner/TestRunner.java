package com.mtn.pruebasex.runner;

import com.mtn.pruebasex.model.Alumno;
import com.mtn.pruebasex.service.AlumnoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest  // ¡Esta anotación es crucial!
@Transactional
@Rollback
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestRunner {

    @Autowired
    private AlumnoService alumnoService;

    @Test
    public void testSaveAndGetAlumno() {
        // Crear un alumno
        Alumno alumno = new Alumno();
        alumno.setNombre("Juan");
        alumno.setApellido("Pérez");
        alumno.setCurso("4°A");

        // Guardar el alumno en la base de datos
        Alumno savedAlumno = alumnoService.saveAlumno(alumno);

        // Verificar que el alumno se ha guardado correctamente
        assertNotNull(savedAlumno.getId());
        assertEquals("Juan", savedAlumno.getNombre());
        assertEquals("4°A", savedAlumno.getCurso());

        // Obtener el alumno por ID
        Alumno foundAlumno = alumnoService.getAlumnoById(savedAlumno.getId()).orElse(null);
        assertNotNull(foundAlumno);
        assertEquals("Juan", foundAlumno.getNombre());
    }

    @Test
    public void testSaveAlumnoWithInvalidData() {
        Alumno alumno = new Alumno(); // Falta nombre y otros datos obligatorios

        Exception exception = assertThrows(Exception.class, () -> {
            alumnoService.saveAlumno(alumno);
        });

        assertNotNull(exception); // Verificar que ocurrió una excepción
    }

    @Test
    public void testGetNonExistentAlumno() {
        Alumno alumno = alumnoService.getAlumnoById(999L).orElse(null);

        assertNull(alumno); // Verifica que no se encuentra el ID
    }
}