package com.mtn.pruebasex.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mtn.pruebasex.model.Alumno;
import com.mtn.pruebasex.model.Prueba;
import com.mtn.pruebasex.model.Resultado;
import com.mtn.pruebasex.service.ResultadoService;
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

@WebMvcTest(ResultadoController.class)
class ResultadoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResultadoService resultadoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Alumno alumno1;
    private Prueba prueba1;
    private Resultado resultado1;
    private Resultado resultado2;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules();

        alumno1 = new Alumno();
        alumno1.setId(1L);
        alumno1.setNombre("Test Alumno");
        alumno1.setApellido("Apellido");
        alumno1.setCurso("1A");

        prueba1 = new Prueba();
        prueba1.setId(1L);
        prueba1.setNombre("Test Prueba");
        prueba1.setTipo("Diagnostico");
        prueba1.setFecha(LocalDate.of(2024, 1, 1));

        resultado1 = new Resultado();
        resultado1.setId(1L);
        resultado1.setAlumno(alumno1);
        resultado1.setPrueba(prueba1);
        resultado1.setPuntaje(90.5);
        resultado1.setArea("Matematicas");

        Alumno alumno2 = new Alumno(); // Different Alumno for variety
        alumno2.setId(2L);
        alumno2.setNombre("Otro Alumno");
        
        Prueba prueba2 = new Prueba(); // Different Prueba
        prueba2.setId(2L);
        prueba2.setNombre("Otra Prueba");

        resultado2 = new Resultado();
        resultado2.setId(2L);
        resultado2.setAlumno(alumno2); 
        resultado2.setPrueba(prueba2);
        resultado2.setPuntaje(88.0);
        resultado2.setArea("Lenguaje");
    }

    @Test
    void createResultado_shouldReturnCreatedResultado() throws Exception {
        Resultado newResultado = new Resultado();
        newResultado.setAlumno(alumno1); // Assuming we send full Alumno/Prueba objects or at least IDs
        newResultado.setPrueba(prueba1);
        newResultado.setPuntaje(75.0);
        newResultado.setArea("Ciencias");
        
        Resultado savedResultado = new Resultado();
        savedResultado.setId(3L);
        savedResultado.setAlumno(alumno1);
        savedResultado.setPrueba(prueba1);
        savedResultado.setPuntaje(75.0);
        savedResultado.setArea("Ciencias");

        when(resultadoService.saveResultado(any(Resultado.class))).thenReturn(savedResultado);

        mockMvc.perform(post("/api/resultados")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newResultado)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.puntaje", is(75.0)))
                .andExpect(jsonPath("$.area", is("Ciencias")))
                .andExpect(jsonPath("$.alumno.id", is(1))) // Verify nested object ID
                .andExpect(jsonPath("$.prueba.id", is(1)));
    }

    @Test
    void getAllResultados_shouldReturnListOfResultados() throws Exception {
        List<Resultado> resultados = Arrays.asList(resultado1, resultado2);
        when(resultadoService.getAllResultados()).thenReturn(resultados);

        mockMvc.perform(get("/api/resultados")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].puntaje", is(resultado1.getPuntaje())))
                .andExpect(jsonPath("$[1].area", is(resultado2.getArea())));
    }

    @Test
    void getResultadoById_whenResultadoExists_shouldReturnResultado() throws Exception {
        when(resultadoService.getResultadoById(1L)).thenReturn(Optional.of(resultado1));

        mockMvc.perform(get("/api/resultados/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.puntaje", is(resultado1.getPuntaje())))
                .andExpect(jsonPath("$.alumno.nombre", is(alumno1.getNombre())));
    }

    @Test
    void getResultadoById_whenResultadoDoesNotExist_shouldReturnNotFound() throws Exception {
        when(resultadoService.getResultadoById(3L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/resultados/3")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateResultado_whenResultadoExists_shouldReturnUpdatedResultado() throws Exception {
        Resultado updatedDetails = new Resultado();
        // For simplicity, assume client sends only fields to be updated for Resultado
        // Alumno and Prueba are not changed in this update request for Resultado
        updatedDetails.setPuntaje(95.0);
        updatedDetails.setArea("Matematicas Avanzadas");
        // The existingResultado (resultado1) already has Alumno and Prueba set.

        when(resultadoService.getResultadoById(1L)).thenReturn(Optional.of(resultado1));
        
        // Mock the saveResultado to reflect changes
        when(resultadoService.saveResultado(any(Resultado.class))).thenAnswer(invocation -> {
            Resultado arg = invocation.getArgument(0);
            assertEquals(1L, arg.getId()); // ID should be preserved
            assertEquals(95.0, arg.getPuntaje()); // New puntaje
            assertEquals("Matematicas Avanzadas", arg.getArea()); // New area
            // Alumno and Prueba should be the same as in resultado1
            assertEquals(alumno1.getId(), arg.getAlumno().getId()); 
            assertEquals(prueba1.getId(), arg.getPrueba().getId());
            return arg;
        });

        mockMvc.perform(put("/api/resultados/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.puntaje", is(95.0)))
                .andExpect(jsonPath("$.area", is("Matematicas Avanzadas")))
                .andExpect(jsonPath("$.alumno.id", is(alumno1.getId().intValue())))
                .andExpect(jsonPath("$.prueba.id", is(prueba1.getId().intValue())));
    }
    
    @Test
    void updateResultado_whenResultadoDoesNotExist_shouldReturnNotFound() throws Exception {
        Resultado updatedDetails = new Resultado();
        updatedDetails.setPuntaje(100.0);

        when(resultadoService.getResultadoById(3L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/resultados/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteResultado_whenResultadoExists_shouldReturnNoContent() throws Exception {
        when(resultadoService.getResultadoById(1L)).thenReturn(Optional.of(resultado1));
        doNothing().when(resultadoService).deleteResultado(1L);

        mockMvc.perform(delete("/api/resultados/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteResultado_whenResultadoDoesNotExist_shouldReturnNotFound() throws Exception {
        when(resultadoService.getResultadoById(3L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/resultados/3"))
                .andExpect(status().isNotFound());
    }
}
