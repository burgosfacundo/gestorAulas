package org.example.service;


import lombok.RequiredArgsConstructor;
import org.example.exception.BadRequestException;
import org.example.exception.NotFoundException;
import org.example.model.Usuario;
import org.example.model.dto.UsuarioDTO;
import org.example.repository.ProfesorRepository;
import org.example.repository.RolRepository;
import org.example.repository.UsuarioRepository;
import org.example.utils.Mapper;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Clase que se encarga de comunicarse con el repositorio
 * y aplicar la lógica de negocio para manipular usuarios
 */
@Service
@RequiredArgsConstructor
public class UsuarioService{
    private final UsuarioRepository repositorioUsuario;
    private final RolRepository repositorioRol;
    private final ProfesorRepository profesorRepository;
    /**
     * Lista todos los usuarios
     * @return List<UsuarioDTO>
     */
    public List<Usuario> listar() {
        return repositorioUsuario.findAll();
    }

    /**
     * Guarda un usuario
     * @param dto que queremos save
     * @return UsuarioDTO que se guarda
     * @throws BadRequestException si existe un usuario con ese username
     */
    public UsuarioDTO guardar(UsuarioDTO dto) throws NotFoundException, BadRequestException {
        //Verificamos que no existe un usuario con ese username, sino lanzamos excepción
        var optional = repositorioUsuario.findByUsername(dto.username());
        if (optional.isPresent()){
            throw new BadRequestException(String.format("Ya existe un usuario con el nombre de usuario: %s", dto.username()));
        }

        //Tomamos el ID del rol y verificamos que existe, si no lanzamos excepción
        validarRolExistente(dto.idRol());

        //Tomamos el ID del profesor y verificamos que existe, si no lanzamos excepción
        validarProfesorExistente(dto.idProfesor());

        repositorioUsuario.save(Mapper.toUsuario(dto));
        return dto;
    }


    /**
     * Elimina un usuario por ID
     * @param id del usuario que queremos eliminar
     * @throws NotFoundException si no se encuentra un usuario con ese id
     */
    public void eliminar(Integer id) throws NotFoundException {
        //Verificamos que existe un usuario con ese ID, si no lanzamos excepción
        validarUsuarioExistente(id);

        repositorioUsuario.deleteById(id);
    }

    /**
     * Obtiene Usuario por ID
     * @param id del usuario a obtener
     * @return UsuarioDTO con el ID del parámetro
     * @throws NotFoundException si no encuentra el usuario con ese ID
     */
    public UsuarioDTO obtener(Integer id) throws NotFoundException {
        return Mapper.usuarioToDto(validarUsuarioExistente(id));
    }

    /**
     * Modifica un usuario
     * @param dto que se quiere modificar
     * @throws NotFoundException si no encuentra el usuario que se quiere modificar
     */
    public void modificar(UsuarioDTO dto) throws NotFoundException {
        //Validamos que exista el usuario
        validarUsuarioExistente(dto.id());

        //Validamos que exista su rol
        validarRolExistente(dto.idRol());

        //Validamos que exista el profesor que representa
        validarProfesorExistente(dto.idProfesor());

        //Mapeamos y modificamos el usuario
        repositorioUsuario.save(Mapper.toUsuario(dto));
    }

    // Validaciones

    /**
     * Valida que exista un rol con ese ID
     *
     * @param idRol del rol a validar
     * @throws NotFoundException si no existe rol con ese ID
     */
    private void validarRolExistente(Integer idRol) throws NotFoundException {
        repositorioRol.findById(idRol)
                .orElseThrow(() -> new NotFoundException(String.format("No existe un rol con el id: %d", idRol)));
    }

    /**
     * Valida que exista un usuario con ese ID
     * @param idUsuario del usuario a validar
     * @return UsuarioDTO si existe el usuario
     * @throws NotFoundException si no existe usuario con ese ID
     */
    private Usuario validarUsuarioExistente(Integer idUsuario) throws NotFoundException {
        return repositorioUsuario.findById(idUsuario)
                .orElseThrow(() -> new NotFoundException(String.format("No existe un usuario con el id: %d", idUsuario)));
    }


    /**
     * Valída la existencia de un Profesor por ID
     *
     * @param id del profesor que se quiere verificar
     * @throws NotFoundException Si no se encuentra el profesor con ese ID
     */
    private void validarProfesorExistente(Integer id) throws NotFoundException {
        profesorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("No existe un profesor con el id: %d", id)));
    }
}
