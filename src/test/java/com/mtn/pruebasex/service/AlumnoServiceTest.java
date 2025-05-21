package com.mtn.pruebasex.service;

import com.mtn.pruebasex.model.Alumno;
import com.mtn.pruebasex.repository.AlumnoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlumnoServiceTest {

    @Mock
    private AlumnoRepository alumnoRepository;

    @InjectMocks
    private AlumnoService alumnoService;

    private Alumno alumno1;
    private Alumno alumno2;

    @BeforeEach
    void setUp() {
        alumno1 = new Alumno();
        alumno1.setId(1L);
        alumno1.setNombre("Juan");
        alumno1.setApellido("Perez");
        alumno1.setCurso("10A");

        alumno2 = new Alumno();
        alumno2.setId(2L);
        alumno2.setNombre("Ana");
        alumno2.setApellido("Gomez");
        alumno2.setCurso("11B");
    }

    @Test
    void getAllAlumnos_shouldReturnListOfAlumnos() {
        when(alumnoRepository.findAll()).thenReturn(Arrays.asList(alumno1, alumno2));

        List<Alumno> alumnos = alumnoService.getAllAlumnos();

        assertEquals(2, alumnos.size());
        assertEquals("Juan", alumnos.get(0).getNombre());
        assertEquals("Ana", alumnos.get(1).getNombre());
        verify(alumnoRepository, times(1)).findAll();
    }

    @Test
    void getAlumnoById_whenAlumnoExists_shouldReturnAlumno() {
        when(alumnoRepository.findById(1L)).thenReturn(Optional.of(alumno1));

        Optional<Alumno> foundAlumnoOptional = alumnoService.getAlumnoById(1L);

        assertTrue(foundAlumnoOptional.isPresent());
        assertEquals("Juan", foundAlumnoOptional.get().getNombre());
        verify(alumnoRepository, times(1)).findById(1L);
    }

    @Test
    void getAlumnoById_whenAlumnoDoesNotExist_shouldReturnEmptyOptional() {
        when(alumnoRepository.findById(3L)).thenReturn(Optional.empty());

        Optional<Alumno> foundAlumnoOptional = alumnoService.getAlumnoById(3L);

        assertFalse(foundAlumnoOptional.isPresent());
        verify(alumnoRepository, times(1)).findById(3L);
    }

    @Test
    void saveAlumno_shouldReturnSavedAlumno() {
        Alumno newAlumno = new Alumno();
        newAlumno.setNombre("Carlos");
        newAlumno.setApellido("Ruiz");
        newAlumno.setCurso("9C");

        // Mocking the save operation
        // When alumnoRepository.save is called with any Alumno object, 
        // it should return that same object, but we'll simulate an ID being set.
        when(alumnoRepository.save(any(Alumno.class))).thenAnswer(invocation -> {
            Alumno a = invocation.getArgument(0);
            if (a.getId() == null) { // Simulate ID generation for new entities
                a.setId(3L); 
            }
            return a;
        });

        Alumno savedAlumno = alumnoService.saveAlumno(newAlumno);

        assertNotNull(savedAlumno.getId()); // Check if ID was set
        assertEquals("Carlos", savedAlumno.getNombre());
        verify(alumnoRepository, times(1)).save(newAlumno);
    }
    
    @Test
    void saveAlumno_whenUpdatingExistingAlumno_shouldReturnUpdatedAlumno() {
        // Assume alumno1 already exists and we are updating it
        alumno1.setCurso("10B_updated"); // Change a field

        when(alumnoRepository.save(any(Alumno.class))).thenReturn(alumno1);

        Alumno updatedAlumno = alumnoService.saveAlumno(alumno1);

        assertEquals(1L, updatedAlumno.getId()); // Ensure ID remains the same
        assertEquals("10B_updated", updatedAlumno.getCurso());
        verify(alumnoRepository, times(1)).save(alumno1);
    }

    @Test
    void deleteAlumno_shouldCallDeleteByIdOnRepository() {
        Long alumnoIdToDelete = 1L;
        // Mock the deleteById method - it doesn't return anything
        doNothing().when(alumnoRepository).deleteById(alumnoIdToDelete);

        alumnoService.deleteAlumno(alumnoIdToDelete);

        // Verify that deleteById was called on the repository with the correct ID
        verify(alumnoRepository, times(1)).deleteById(alumnoIdToDelete);
    }
}
