package com.mtn.pruebasex.service;

import com.mtn.pruebasex.model.Prueba;
import com.mtn.pruebasex.repository.PruebaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PruebaService {

    @Autowired
    private PruebaRepository pruebaRepository;

    public List<Prueba> getAllPruebas() {
        return pruebaRepository.findAll();
    }

    public Optional<Prueba> getPruebaById(Long id) {
        return pruebaRepository.findById(id);
    }

    public Prueba savePrueba(Prueba prueba) {
        return pruebaRepository.save(prueba);
    }

    public void deletePrueba(Long id) {
        pruebaRepository.deleteById(id);
    }
}