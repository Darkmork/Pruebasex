package com.mtn.pruebasex.service;

import com.mtn.pruebasex.model.Prueba;
import com.mtn.pruebasex.repository.PruebaRepository;
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
class PruebaServiceTest {

    @Mock
    private PruebaRepository pruebaRepository;

    @InjectMocks
    private PruebaService pruebaService;

    private Prueba prueba1;
    private Prueba prueba2;

    @BeforeEach
    void setUp() {
        prueba1 = new Prueba();
        prueba1.setId(1L);
        prueba1.setNombre("Matematicas Q1");
        prueba1.setTipo("Diagnostico");
        prueba1.setFecha(LocalDate.of(2024, 3, 15));

        prueba2 = new Prueba();
        prueba2.setId(2L);
        prueba2.setNombre("Lectura Critica S1");
        prueba2.setTipo("SEPA");
        prueba2.setFecha(LocalDate.of(2024, 5, 10));
    }

    @Test
    void getAllPruebas_shouldReturnListOfPruebas() {
        when(pruebaRepository.findAll()).thenReturn(Arrays.asList(prueba1, prueba2));

        List<Prueba> pruebas = pruebaService.getAllPruebas();

        assertEquals(2, pruebas.size());
        assertEquals("Matematicas Q1", pruebas.get(0).getNombre());
        assertEquals("SEPA", pruebas.get(1).getTipo());
        verify(pruebaRepository, times(1)).findAll();
    }

    @Test
    void getPruebaById_whenPruebaExists_shouldReturnPrueba() {
        when(pruebaRepository.findById(1L)).thenReturn(Optional.of(prueba1));

        Optional<Prueba> foundPruebaOptional = pruebaService.getPruebaById(1L);

        assertTrue(foundPruebaOptional.isPresent());
        assertEquals("Matematicas Q1", foundPruebaOptional.get().getNombre());
        verify(pruebaRepository, times(1)).findById(1L);
    }

    @Test
    void getPruebaById_whenPruebaDoesNotExist_shouldReturnEmptyOptional() {
        when(pruebaRepository.findById(3L)).thenReturn(Optional.empty());

        Optional<Prueba> foundPruebaOptional = pruebaService.getPruebaById(3L);

        assertFalse(foundPruebaOptional.isPresent());
        verify(pruebaRepository, times(1)).findById(3L);
    }

    @Test
    void savePrueba_shouldReturnSavedPrueba() {
        Prueba newPrueba = new Prueba();
        newPrueba.setNombre("Ciencias Naturales Final");
        newPrueba.setTipo("MIDEUC");
        newPrueba.setFecha(LocalDate.of(2024, 11, 20));

        when(pruebaRepository.save(any(Prueba.class))).thenAnswer(invocation -> {
            Prueba p = invocation.getArgument(0);
            if (p.getId() == null) {
                p.setId(3L); // Simulate ID generation
            }
            return p;
        });

        Prueba savedPrueba = pruebaService.savePrueba(newPrueba);

        assertNotNull(savedPrueba.getId());
        assertEquals("Ciencias Naturales Final", savedPrueba.getNombre());
        verify(pruebaRepository, times(1)).save(newPrueba);
    }
    
    @Test
    void savePrueba_whenUpdatingExistingPrueba_shouldReturnUpdatedPrueba() {
        prueba1.setTipo("Diagnostico Avanzado");

        when(pruebaRepository.save(any(Prueba.class))).thenReturn(prueba1);

        Prueba updatedPrueba = pruebaService.savePrueba(prueba1);
        
        assertEquals(1L, updatedPrueba.getId());
        assertEquals("Diagnostico Avanzado", updatedPrueba.getTipo());
        verify(pruebaRepository, times(1)).save(prueba1);
    }

    @Test
    void deletePrueba_shouldCallDeleteByIdOnRepository() {
        Long pruebaIdToDelete = 1L;
        doNothing().when(pruebaRepository).deleteById(pruebaIdToDelete);

        pruebaService.deletePrueba(pruebaIdToDelete);

        verify(pruebaRepository, times(1)).deleteById(pruebaIdToDelete);
    }
}
