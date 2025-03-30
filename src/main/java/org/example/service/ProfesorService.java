package org.example.service;



import lombok.RequiredArgsConstructor;
import org.example.exception.BadRequestException;
import org.example.exception.NotFoundException;
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


    /**
     * Guarda un profesor
     * @param profesor que queremos guarde
     * @return Profesor que se guarda
     * @throws BadRequestException sí existe un profesor con esa matrícula
     */
    public Profesor guardar(Profesor profesor) throws BadRequestException {
        // Verificamos que no existe esa asignatura y si existe lanzamos la excepción
        if (repositorio.findByMatricula(profesor.getMatricula()).isPresent()){
            throw new BadRequestException("Ya existe un profesor con la matrícula: " + profesor.getMatricula());
        }

        repositorio.save(profesor);
        return profesor;
    }

    /**
     * Elimina un profesor por ID
     * @param id del profesor que queremos eliminar
     * @throws NotFoundException si no se encuentra un profesor con ese ID
     */
    public void eliminar(Integer id) throws NotFoundException {
        // Verificamos que existe un profesor con ese ID y si no lanzamos la excepción
        validarProfesorExistente(id);
        repositorio.deleteById(id);
    }

    /**
     * Obtiene un profesor por ID
     * @param id del profesor que queremos obtener
     * @return Profesor con ese ID
     * @throws NotFoundException si no se encuentra una asignatura con ese ID
     */
    public Profesor obtener(Integer id) throws NotFoundException {
        // Validamos y retornamos el profesor
        return validarProfesorExistente(id);
    }


    // Validaciones

    /**
     * Valída la existencia de un Profesor por ID
     * @param id del profesor que se quiere verificar
     * @return Profesor si existe
     * @throws NotFoundException Si no se encuentra el profesor con ese ID
     */
    private Profesor validarProfesorExistente(Integer id) throws NotFoundException {
        return repositorio.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe un profesor con el id: " + id));
    }
}
