package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.model.Profesor;
import org.example.repository.ProfesorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfesorService {
    private final ProfesorRepository repositorio;

    /**
     * Lista todos los profesores
     * @return List<Profesor>
     */
    public List<Profesor> listar() {
        return repositorio.findAll();
    }
}
