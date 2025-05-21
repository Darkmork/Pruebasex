package com.mtn.pruebasex.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mtn.pruebasex.model.Prueba;
import com.mtn.pruebasex.service.PruebaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@WebMvcTest(PruebaController.class)
class PruebaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PruebaService pruebaService;

    @Autowired
    private ObjectMapper objectMapper;

    private Prueba prueba1;
    private Prueba prueba2;

    @BeforeEach
    void setUp() {
        // Register JavaTimeModule for LocalDate serialization/deserialization
        objectMapper.registerModule(new JavaTimeModule()); 
        objectMapper.findAndRegisterModules(); // Ensure all modules are registered, including for LocalDate

        prueba1 = new Prueba();
        prueba1.setId(1L);
        prueba1.setNombre("Matematicas Avanzadas");
        prueba1.setTipo("Sumativa");
        prueba1.setFecha(LocalDate.of(2024, 8, 15));

        prueba2 = new Prueba();
        prueba2.setId(2L);
        prueba2.setNombre("Comprension Lectora Global");
        prueba2.setTipo("Diagnostico");
        prueba2.setFecha(LocalDate.of(2024, 9, 1));
    }

    @Test
    void createPrueba_shouldReturnCreatedPrueba() throws Exception {
        Prueba newPrueba = new Prueba();
        newPrueba.setNombre("Nueva Prueba");
        newPrueba.setTipo("Formativa");
        newPrueba.setFecha(LocalDate.of(2024, 10, 5));
        
        Prueba savedPrueba = new Prueba();
        savedPrueba.setId(3L);
        savedPrueba.setNombre("Nueva Prueba");
        savedPrueba.setTipo("Formativa");
        savedPrueba.setFecha(LocalDate.of(2024, 10, 5));

        when(pruebaService.savePrueba(any(Prueba.class))).thenReturn(savedPrueba);

        mockMvc.perform(post("/api/pruebas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPrueba)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.nombre", is("Nueva Prueba")))
                .andExpect(jsonPath("$.fecha", is("2024-10-05")));
    }

    @Test
    void getAllPruebas_shouldReturnListOfPruebas() throws Exception {
        List<Prueba> pruebas = Arrays.asList(prueba1, prueba2);
        when(pruebaService.getAllPruebas()).thenReturn(pruebas);

        mockMvc.perform(get("/api/pruebas")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre", is(prueba1.getNombre())))
                .andExpect(jsonPath("$[1].nombre", is(prueba2.getNombre())));
    }

    @Test
    void getPruebaById_whenPruebaExists_shouldReturnPrueba() throws Exception {
        when(pruebaService.getPruebaById(1L)).thenReturn(Optional.of(prueba1));

        mockMvc.perform(get("/api/pruebas/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is(prueba1.getNombre())))
                .andExpect(jsonPath("$.fecha", is(prueba1.getFecha().toString())));
    }

    @Test
    void getPruebaById_whenPruebaDoesNotExist_shouldReturnNotFound() throws Exception {
        when(pruebaService.getPruebaById(3L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/pruebas/3")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatePrueba_whenPruebaExists_shouldReturnUpdatedPrueba() throws Exception {
        Prueba updatedDetails = new Prueba();
        updatedDetails.setNombre("Matematicas Super Avanzadas");
        updatedDetails.setTipo(prueba1.getTipo());
        updatedDetails.setFecha(LocalDate.of(2024, 8, 20));

        when(pruebaService.getPruebaById(1L)).thenReturn(Optional.of(prueba1));
        
        when(pruebaService.savePrueba(any(Prueba.class))).thenAnswer(invocation -> {
            Prueba arg = invocation.getArgument(0);
            assertEquals(1L, arg.getId());
            assertEquals("Matematicas Super Avanzadas", arg.getNombre());
            assertEquals(LocalDate.of(2024, 8, 20), arg.getFecha());
            return arg;
        });

        mockMvc.perform(put("/api/pruebas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Matematicas Super Avanzadas")))
                .andExpect(jsonPath("$.fecha", is("2024-08-20")));
    }
    
    @Test
    void updatePrueba_whenPruebaDoesNotExist_shouldReturnNotFound() throws Exception {
        Prueba updatedDetails = new Prueba();
        updatedDetails.setNombre("No Existo");
        updatedDetails.setFecha(LocalDate.now());

        when(pruebaService.getPruebaById(3L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/pruebas/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePrueba_whenPruebaExists_shouldReturnNoContent() throws Exception {
        when(pruebaService.getPruebaById(1L)).thenReturn(Optional.of(prueba1));
        doNothing().when(pruebaService).deletePrueba(1L);

        mockMvc.perform(delete("/api/pruebas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletePrueba_whenPruebaDoesNotExist_shouldReturnNotFound() throws Exception {
        when(pruebaService.getPruebaById(3L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/pruebas/3"))
                .andExpect(status().isNotFound());
    }
}
