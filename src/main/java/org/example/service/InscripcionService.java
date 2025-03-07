package org.example.service;


import lombok.RequiredArgsConstructor;
import org.example.exception.BadRequestException;
import org.example.exception.JsonNotFoundException;
import org.example.exception.NotFoundException;
import org.example.model.Asignatura;
import org.example.model.Inscripcion;
import org.example.model.Profesor;
import org.example.model.dto.InscripcionDTO;
import org.example.repository.AsignaturaRepository;
import org.example.repository.InscripcionRepository;
import org.example.repository.ProfesorRepository;
import org.example.utils.Mapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;




/**
 * Clase que se encarga de comunicarse con el repositorio
 * y aplicar la lógica de negocio para manipular inscripciones
 */
@Service
@RequiredArgsConstructor
public class InscripcionService{
    private final InscripcionRepository repositorio;
    private final AsignaturaRepository asignaturaRepository;
    private final ProfesorRepository profesorRepository;


    /**
     * Lista todas las inscripciones
     * @return List<Inscripcion>
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    public List<Inscripcion> listar() throws JsonNotFoundException, NotFoundException {
        List<Inscripcion> inscripciones = new ArrayList<>();
        var dtoList = repositorio.getAll();
        for (InscripcionDTO dto : dtoList){
            // Validamos que la asignatura exista
            var asignatura = validarAsignaturaExistente(dto.idAsignatura());
            // Validamos que el profesor exista
            var profesor = validarProfesorExistente(dto.idProfesor());
            // Mapeamos y guardamos en inscripciones
            inscripciones.add(Mapper.toInscripcion(dto,asignatura,profesor));
        }
        return inscripciones;
    }


    /**
     * Guarda una inscripción
     * @param inscripcion que queremos guardar
     * @return Inscripción que se guarda
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws BadRequestException si existe una inscripción con ese profesor,asignatura y comisión
     */
    public Inscripcion guardar(Inscripcion inscripcion) throws JsonNotFoundException, BadRequestException, NotFoundException {
        var idAsignatura = inscripcion.getAsignatura().getId();
        var idProfesor = inscripcion.getProfesor().getId();
        //Verificamos que no existe una inscripción con esas características, sino lanzamos excepción
        var optional = repositorio.find(idAsignatura, idProfesor, inscripcion.getComision());
        if (optional.isPresent()){
            throw new BadRequestException("Ya existe una inscripción con esas características");
        }

        //Tomamos el ID de Asignatura y verificamos que existe, si no lanzamos excepción
        validarAsignaturaExistente(idAsignatura);

        //Tomamos el ID de Profesor y verificamos que existe, si no lanzamos excepción
        validarProfesorExistente(idProfesor);

        repositorio.save(Mapper.inscripcionToDTO(inscripcion));
        return inscripcion;
    }


    /**
     * Elimina una inscripción por ID
     * @param id de la inscripción que queremos eliminar
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws NotFoundException si no se encuentra una inscripción con ese ID
     */
    public void eliminar(Integer id) throws JsonNotFoundException, NotFoundException {
        //Verificamos que existe una inscripción con ese ID, si no lanzamos excepción
        validarInscripcionExistente(id);

        //Borramos la inscripción por ID
        repositorio.deleteById(id);
    }

    /**
     * Obtiene una inscripción por ID
     * @param id de la inscripción
     * @return Inscripcion con ese ID
     * @throws JsonNotFoundException Sí ocurre un error con el archivo JSON
     * @throws NotFoundException Si no se encuentra la inscripción con ese ID
     */
    public Inscripcion obtener(Integer id) throws JsonNotFoundException, NotFoundException {
        //Validamos que exista la inscripción
        var dto = validarInscripcionExistente(id);

        //Validamos que exista la asignatura
        var asignatura = validarAsignaturaExistente(dto.idAsignatura());

        //Validamos que exista el profesor
        var profesor = validarProfesorExistente(dto.idProfesor());

        return Mapper.toInscripcion(dto,asignatura,profesor);
    }

    /**
     * Modifica una inscripción
     * @param inscripcion que se va a modificar
     * @throws JsonNotFoundException si ocurre un error con el archivo JSON
     * @throws NotFoundException Si no encuentra inscripción o asignatura o profesor
     */
    public void modificar(Inscripcion inscripcion) throws JsonNotFoundException, NotFoundException {
        //Validamos que exista la inscripción
        var dto = validarInscripcionExistente(inscripcion.getId());

        //Validamos que exista la asignatura
        validarAsignaturaExistente(dto.idAsignatura());

        //Validamos que exista el profesor
        validarProfesorExistente(dto.idProfesor());

        //Mapeamos y modificamos la inscripción
        repositorio.modify(Mapper.inscripcionToDTO(inscripcion));
    }



    //Validaciones
    /**
     * Valida la existencia de una inscripción por ID
     * @param id de la inscripción que se quiere verificar
     * @return InscripcionDTO si existe
     * @throws NotFoundException Si no se encuentra la inscripción con ese ID
     * @throws JsonNotFoundException Sí ocurre un error con el archivo JSON
     */
    private InscripcionDTO validarInscripcionExistente(Integer id) throws NotFoundException, JsonNotFoundException {
        return repositorio.find(id)
                .orElseThrow(() -> new NotFoundException(String.format("No existe una inscripción con el id: %d", id)));
    }

    /**
     * Valida la existencia de un Profesor
     * @param idProfesor del profesor que se quiere verificar
     * @return Profesor si existe
     * @throws NotFoundException Si no se encuentra el profesor con ese ID
     * @throws JsonNotFoundException Sí ocurre un error con el archivo JSON
     */
    private Profesor validarProfesorExistente(Integer idProfesor) throws NotFoundException, JsonNotFoundException {
        return profesorRepository.find(idProfesor)
                .orElseThrow(() -> new NotFoundException(String.format("No existe un profesor con el id: %d", idProfesor)));
    }

    /**
     * Valida la existencia de una Asignatura por ID
     * @param idAsignatura de la asignatura que se quiere verificar
     * @return Asignatura si existe
     * @throws NotFoundException Si no se encuentra la asignatura con ese ID
     * @throws JsonNotFoundException Sí ocurre un error con el archivo JSON
     */
    private Asignatura validarAsignaturaExistente(Integer idAsignatura) throws NotFoundException, JsonNotFoundException {
        return asignaturaRepository.find(idAsignatura)
                .orElseThrow(() -> new NotFoundException(String.format("No existe una asignatura con el id: %d", idAsignatura)));
    }
}
