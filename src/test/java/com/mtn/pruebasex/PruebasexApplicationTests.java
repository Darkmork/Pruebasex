package com.mtn.pruebasex;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PruebasexApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        // Check that the application context is not null, indicating it loaded successfully
        assertThat(applicationContext).isNotNull();
    }

    // You could add more specific bean checks here if needed, for example:
    // @Test
    // void alumnoControllerBeanExists() {
    //     assertThat(applicationContext.getBean(com.mtn.pruebasex.controller.AlumnoController.class)).isNotNull();
    // }
    //
    // @Test
    // void alumnoServiceBeanExists() {
    //     assertThat(applicationContext.getBean(com.mtn.pruebasex.service.AlumnoService.class)).isNotNull();
    // }
    //
    // @Test
    // void alumnoRepositoryBeanExists() {
    //     assertThat(applicationContext.getBean(com.mtn.pruebasex.repository.AlumnoRepository.class)).isNotNull();
    // }
    
    // However, for general application startup, just checking the context is often enough.
    // If an H2 database is configured for the 'test' profile, this test would also implicitly
    // check if the database connection and schema generation (if any) are working.
}
