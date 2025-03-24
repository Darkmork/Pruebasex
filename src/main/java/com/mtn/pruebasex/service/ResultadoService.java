package com.mtn.pruebasex.service;

import com.mtn.pruebasex.model.Resultado;
import com.mtn.pruebasex.repository.ResultadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResultadoService {

    @Autowired
    private ResultadoRepository resultadoRepository;

    public List<Resultado> getAllResultados() {
        return resultadoRepository.findAll();
    }

    public Optional<Resultado> getResultadoById(Long id) {
        return resultadoRepository.findById(id);
    }

    public Resultado saveResultado(Resultado resultado) {
        return resultadoRepository.save(resultado);
    }

    public void deleteResultado(Long id) {
        resultadoRepository.deleteById(id);
    }
}