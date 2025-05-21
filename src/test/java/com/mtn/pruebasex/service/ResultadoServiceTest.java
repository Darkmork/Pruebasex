package com.mtn.pruebasex.service;

import com.mtn.pruebasex.model.Alumno;
import com.mtn.pruebasex.model.Prueba;
import com.mtn.pruebasex.model.Resultado;
import com.mtn.pruebasex.repository.ResultadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResultadoServiceTest {

    @Mock
    private ResultadoRepository resultadoRepository;

    @InjectMocks
    private ResultadoService resultadoService;

    private Alumno alumno1;
    private Prueba prueba1;
    private Resultado resultado1;
    private Resultado resultado2;

    @BeforeEach
    void setUp() {
        alumno1 = new Alumno();
        alumno1.setId(1L);
        alumno1.setNombre("Juan");
        alumno1.setApellido("Perez");
        alumno1.setCurso("10A");

        prueba1 = new Prueba();
        prueba1.setId(1L);
        prueba1.setNombre("Matematicas Q1");
        prueba1.setTipo("Diagnostico");
        prueba1.setFecha(LocalDate.of(2024, 3, 15));

        resultado1 = new Resultado();
        resultado1.setId(1L);
        resultado1.setAlumno(alumno1);
        resultado1.setPrueba(prueba1);
        resultado1.setPuntaje(85.5);
        resultado1.setArea("Algebra");

        resultado2 = new Resultado();
        resultado2.setId(2L);
        resultado2.setAlumno(alumno1); // Same student, different test/area or score
        Prueba prueba2 = new Prueba();
        prueba2.setId(2L);
        prueba2.setNombre("Lectura Critica S1");
        resultado2.setPrueba(prueba2);
        resultado2.setPuntaje(92.0);
        resultado2.setArea("Comprension Lectora");
    }

    @Test
    void getAllResultados_shouldReturnListOfResultados() {
        when(resultadoRepository.findAll()).thenReturn(Arrays.asList(resultado1, resultado2));

        List<Resultado> resultados = resultadoService.getAllResultados();

        assertEquals(2, resultados.size());
        assertEquals(85.5, resultados.get(0).getPuntaje());
        assertEquals("Comprension Lectora", resultados.get(1).getArea());
        verify(resultadoRepository, times(1)).findAll();
    }

    @Test
    void getResultadoById_whenResultadoExists_shouldReturnResultado() {
        when(resultadoRepository.findById(1L)).thenReturn(Optional.of(resultado1));

        Optional<Resultado> foundResultadoOptional = resultadoService.getResultadoById(1L);

        assertTrue(foundResultadoOptional.isPresent());
        assertEquals(85.5, foundResultadoOptional.get().getPuntaje());
        verify(resultadoRepository, times(1)).findById(1L);
    }

    @Test
    void getResultadoById_whenResultadoDoesNotExist_shouldReturnEmptyOptional() {
        when(resultadoRepository.findById(3L)).thenReturn(Optional.empty());

        Optional<Resultado> foundResultadoOptional = resultadoService.getResultadoById(3L);

        assertFalse(foundResultadoOptional.isPresent());
        verify(resultadoRepository, times(1)).findById(3L);
    }

    @Test
    void saveResultado_shouldReturnSavedResultado() {
        Resultado newResultado = new Resultado();
        newResultado.setAlumno(alumno1);
        newResultado.setPrueba(prueba1);
        newResultado.setPuntaje(77.0);
        newResultado.setArea("Geometria");

        when(resultadoRepository.save(any(Resultado.class))).thenAnswer(invocation -> {
            Resultado r = invocation.getArgument(0);
            if (r.getId() == null) {
                r.setId(3L); // Simulate ID generation
            }
            return r;
        });

        Resultado savedResultado = resultadoService.saveResultado(newResultado);

        assertNotNull(savedResultado.getId());
        assertEquals(77.0, savedResultado.getPuntaje());
        assertEquals("Geometria", savedResultado.getArea());
        verify(resultadoRepository, times(1)).save(newResultado);
    }
    
    @Test
    void saveResultado_whenUpdatingExistingResultado_shouldReturnUpdatedResultado() {
        resultado1.setPuntaje(88.0); // Update score

        when(resultadoRepository.save(any(Resultado.class))).thenReturn(resultado1);
        
        Resultado updatedResultado = resultadoService.saveResultado(resultado1);
        
        assertEquals(1L, updatedResultado.getId());
        assertEquals(88.0, updatedResultado.getPuntaje());
        verify(resultadoRepository, times(1)).save(resultado1);
    }

    @Test
    void deleteResultado_shouldCallDeleteByIdOnRepository() {
        Long resultadoIdToDelete = 1L;
        doNothing().when(resultadoRepository).deleteById(resultadoIdToDelete);

        resultadoService.deleteResultado(resultadoIdToDelete);

        verify(resultadoRepository, times(1)).deleteById(resultadoIdToDelete);
    }
}
