package com.mtn.pruebasex.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mtn.pruebasex.model.Alumno;
import com.mtn.pruebasex.service.AlumnoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@WebMvcTest(AlumnoController.class) // Test only AlumnoController, not full SpringBootTest
class AlumnoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean // Mocks the AlumnoService in the Spring application context
    private AlumnoService alumnoService;

    @Autowired
    private ObjectMapper objectMapper; // For converting objects to JSON strings

    private Alumno alumno1;
    private Alumno alumno2;

    @BeforeEach
    void setUp() {
        alumno1 = new Alumno();
        alumno1.setId(1L);
        alumno1.setNombre("Carlos");
        alumno1.setApellido("Santana");
        alumno1.setCurso("10A");

        alumno2 = new Alumno();
        alumno2.setId(2L);
        alumno2.setNombre("Luisa");
        alumno2.setApellido("Castro");
        alumno2.setCurso("11B");
    }

    @Test
    void createAlumno_shouldReturnCreatedAlumno() throws Exception {
        Alumno newAlumno = new Alumno();
        newAlumno.setNombre("Nuevo");
        newAlumno.setApellido("Estudiante");
        newAlumno.setCurso("5C");
        
        // We need to mock the service to return the alumno with an ID
        Alumno savedAlumno = new Alumno();
        savedAlumno.setId(3L); // Simulate ID assignment by service/DB
        savedAlumno.setNombre("Nuevo");
        savedAlumno.setApellido("Estudiante");
        savedAlumno.setCurso("5C");

        when(alumnoService.saveAlumno(any(Alumno.class))).thenReturn(savedAlumno);

        mockMvc.perform(post("/api/alumnos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newAlumno)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.nombre", is("Nuevo")));
    }

    @Test
    void getAllAlumnos_shouldReturnListOfAlumnos() throws Exception {
        List<Alumno> alumnos = Arrays.asList(alumno1, alumno2);
        when(alumnoService.getAllAlumnos()).thenReturn(alumnos);

        mockMvc.perform(get("/api/alumnos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre", is(alumno1.getNombre())))
                .andExpect(jsonPath("$[1].nombre", is(alumno2.getNombre())));
    }

    @Test
    void getAlumnoById_whenAlumnoExists_shouldReturnAlumno() throws Exception {
        when(alumnoService.getAlumnoById(1L)).thenReturn(Optional.of(alumno1));

        mockMvc.perform(get("/api/alumnos/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is(alumno1.getNombre())));
    }

    @Test
    void getAlumnoById_whenAlumnoDoesNotExist_shouldReturnNotFound() throws Exception {
        when(alumnoService.getAlumnoById(3L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/alumnos/3")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateAlumno_whenAlumnoExists_shouldReturnUpdatedAlumno() throws Exception {
        Alumno updatedDetails = new Alumno();
        updatedDetails.setNombre("Carlos Updated");
        updatedDetails.setApellido(alumno1.getApellido()); // Keep some old values
        updatedDetails.setCurso("10A_Mod");

        // Mock service finding the existing alumno
        when(alumnoService.getAlumnoById(1L)).thenReturn(Optional.of(alumno1));
        
        // Mock service saving the updated alumno
        // The controller will modify alumno1 and then pass it to saveAlumno
        // So we expect alumno1 (modified) to be passed to saveAlumno
        // We should return this modified alumno1
        when(alumnoService.saveAlumno(any(Alumno.class))).thenAnswer(invocation -> {
            Alumno arg = invocation.getArgument(0);
            // Ensure the ID is maintained and fields are updated
            assertEquals(1L, arg.getId()); 
            assertEquals("Carlos Updated", arg.getNombre());
            assertEquals("10A_Mod", arg.getCurso());
            return arg; // Return the modified alumno
        });


        mockMvc.perform(put("/api/alumnos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Carlos Updated")))
                .andExpect(jsonPath("$.curso", is("10A_Mod")));
    }
    
    @Test
    void updateAlumno_whenAlumnoDoesNotExist_shouldReturnNotFound() throws Exception {
        Alumno updatedDetails = new Alumno();
        updatedDetails.setNombre("No Existo");

        when(alumnoService.getAlumnoById(3L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/alumnos/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteAlumno_whenAlumnoExists_shouldReturnNoContent() throws Exception {
        when(alumnoService.getAlumnoById(1L)).thenReturn(Optional.of(alumno1));
        doNothing().when(alumnoService).deleteAlumno(1L);

        mockMvc.perform(delete("/api/alumnos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteAlumno_whenAlumnoDoesNotExist_shouldReturnNotFound() throws Exception {
        when(alumnoService.getAlumnoById(3L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/alumnos/3"))
                .andExpect(status().isNotFound());
    }
}
