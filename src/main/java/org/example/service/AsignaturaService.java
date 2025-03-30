package org.example.service;



import lombok.RequiredArgsConstructor;
import org.example.exception.BadRequestException;
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
     * Guarda una asignatura
     * @param asignatura que queremos guarde
     * @return Asignatura que se guarda
     * @throws BadRequestException sí existe una asignatura con ese código
     */
    public Asignatura guardar(Asignatura asignatura) throws BadRequestException {
        // Verificamos que no existe esa asignatura y si existe lanzamos la excepción
        if(repositorio.findByCodigo(asignatura.getCodigo()).isPresent()){
            throw new BadRequestException(String.format("Ya existe una asignatura con el código: %s", asignatura.getCodigo()));
        }
        // Guardamos la asignatura
        repositorio.save(asignatura);
        return asignatura;
    }

    /**
     * Elimina una asignatura por ID
     * @param id de la asignatura que queremos eliminar
     * @throws NotFoundException si no se encuentra una asignatura con ese ID
     */
    public void eliminar(Integer id) throws NotFoundException {
        // Verificamos que existe una asignatura con ese ID y si no lanzamos la excepción
        validarAsignaturaExistenteById(id);
        repositorio.deleteById(id);
    }

    /**
     * Obtiene una asignatura por ID
     * @param id de la asignatura que queremos obtener
     * @return Asignatura con ese ID
     */
    public Asignatura obtener(Integer id) throws NotFoundException {
        // Validamos y retornamos la asignatura
        return validarAsignaturaExistenteById(id);
    }

    // Validaciones

    /**
     * Valída la existencia de una Asignatura por ID
     * @param idAsignatura de la asignatura que se quiere verificar
     * @return Asignatura si existe
     * @throws NotFoundException Si no se encuentra la asignatura con ese ID
     */
    private Asignatura validarAsignaturaExistenteById(Integer idAsignatura) throws NotFoundException {
        return repositorio.findById(idAsignatura)
                .orElseThrow(() -> new NotFoundException(String.format("No existe una asignatura con el id: %d", idAsignatura)));
    }
}
