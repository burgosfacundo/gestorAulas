package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.exception.BadRequestException;
import org.example.exception.NotFoundException;
import org.example.model.Inscripcion;
import org.example.model.dto.InscripcionDTO;
import org.example.repository.InscripcionRepository;
import org.example.utils.Mapper;
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
     * Guarda una inscripción
     * @param dto que queremos guardar
     * @return Inscripción que se guarda
     * @throws BadRequestException sí existe una inscripción con ese profesor, asignatura y comisión
     */
    public InscripcionDTO guardar(InscripcionDTO dto) throws BadRequestException {
        var idAsignatura = dto.idAsignatura();
        var idProfesor = dto.idProfesor();
        //Verificamos que no existe una inscripción con esas características, si no lanzamos excepción
        if (repositorio.find(idAsignatura, idProfesor, dto.comision()).isPresent()){
            throw new BadRequestException("Ya existe una inscripción con esas características");
        }

        repositorio.save(Mapper.toInscripcion(dto));
        return dto;
    }


    /**
     * Elimina una inscripción por ID
     * @param id de la inscripción que queremos eliminar
     * @throws NotFoundException si no se encuentra una inscripción con ese ID
     */
    public void eliminar(Integer id) throws NotFoundException {
        //Verificamos que existe una inscripción con ese ID, si no lanzamos excepción
        validarInscripcionExistente(id);

        //Borramos la inscripción por ID
        repositorio.deleteById(id);
    }

    /**
     * Obtiene una inscripción por ID
     * @param id de la inscripción
     * @return Inscripcion con ese ID
     * @throws NotFoundException Si no se encuentra la inscripción con ese ID
     */
    public Inscripcion obtener(Integer id) throws NotFoundException {
        //Validamos que exista la inscripción
        return validarInscripcionExistente(id);
    }

    //Validaciones
    /**
     * Valída la existencia de una inscripción por ID
     * @param id de la inscripción que se quiere verificar
     * @return Inscripcion si existe
     * @throws NotFoundException Si no se encuentra la inscripción con ese ID
     */
    private Inscripcion validarInscripcionExistente(Integer id) throws NotFoundException {
        return repositorio.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("No existe una inscripción con el id: %d", id)));
    }
}
