package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.exception.NotFoundException;
import org.example.model.Inscripcion;
import org.example.repository.InscripcionRepository;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Clase que se encarga de comunicarse con el repositorio
 * y aplicar la lógica de negocio para manipular inscripciones
 */
@Service
@RequiredArgsConstructor
public class InscripcionService{
    private final InscripcionRepository repositorio;

    /**
     * Lista todas las inscripciones
     * @return List<Inscripcion>
     *
     */
    public List<Inscripcion> listar() {
        return repositorio.findAll();
    }

    /**
     * Obtiene una inscripción por ID
     * @param id de la inscripción
     * @return Inscripcion con ese ID
     * @throws NotFoundException Si no se encuentra la inscripción con ese ID
     */
    public Inscripcion obtener(Integer id) throws NotFoundException {
        return repositorio.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe la inscripción"));
    }
}
