package com.mtn.pruebasex.service;

import com.mtn.pruebasex.model.Alumno;
import com.mtn.pruebasex.repository.AlumnoRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DataJpaTest
public class AlumnoServiceTest {

    @Autowired
    private AlumnoService alumnoService;

    @Mock
    private AlumnoRepository alumnoRepository;

    /**
     * Test cases for the AlumnoService's getAlumnoById method.
     * The method retrieves an Alumno entity from the repository by its ID.
     */

    @Test
    public void givenValidId_whenGetAlumnoById_thenReturnAlumno() {
        Alumno alumno = new Alumno();
        alumno.setId(1L);
        alumno.setNombre("John");
        alumno.setApellido("Doe");
        alumno.setCurso("Math");

        when(alumnoRepository.findById(1L)).thenReturn(Optional.of(alumno));

        Alumno result = alumnoService.getAlumnoById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John", result.getNombre());
        assertEquals("Doe", result.getApellido());
        assertEquals("Math", result.getCurso());
        verify(alumnoRepository, times(1)).findById(1L);
    }

    @Test
    public void givenInvalidId_whenGetAlumnoById_thenThrowException() {
        when(alumnoRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> alumnoService.getAlumnoById(2L));

        verify(alumnoRepository, times(1)).findById(2L);
    }

    @Test
    public void givenNullId_whenGetAlumnoById_thenThrowException() {
        assertThrows(IllegalArgumentException.class, () -> alumnoService.getAlumnoById(null));

        verify(alumnoRepository, never()).findById(Mockito.any());
    }
}
