package org.example.service;



import lombok.RequiredArgsConstructor;
import org.example.exception.NotFoundException;
import org.example.model.Asignatura;
import org.example.repository.AsignaturaRepository;
import org.springframework.stereotype.Service;

import java.util.List;



/**
 * Clase que se encarga de comunicarse con el repositorio
 * y aplicar la lógica de negocio para manipular asignaturas
 */
@Service
@RequiredArgsConstructor
public class AsignaturaService{
    private final AsignaturaRepository repositorio;

    /**
     * Lista todas las asignaturas
     * @return List<Asignatura>
     */
    public List<Asignatura> listar() {
        return repositorio.findAll();
    }

    /**
     * Obtiene una asignatura por ID
     * @param id de la asignatura que queremos obtener
     * @return Asignatura con ese ID
     */
    public Asignatura obtener(Integer id) throws NotFoundException {
        return repositorio.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe la asignatura"));
    }
}
