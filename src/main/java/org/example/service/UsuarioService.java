package org.example.service;


import org.example.exception.BadRequestException;
import org.example.exception.JsonNotFoundException;
import org.example.exception.NotFoundException;
import org.example.model.Profesor;
import org.example.model.Rol;
import org.example.model.Usuario;
import org.example.model.dto.UsuarioDTO;
import org.example.repository.ProfesorRepository;
import org.example.repository.RolRepository;
import org.example.repository.UsuarioRepository;
import org.example.utils.Mapper;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * Clase que se encarga de comunicarse con el repositorio
 * y aplicar la lógica de negocio para manipular usuarios
 */
@Service
public class UsuarioService{
    private final UsuarioRepository repositorioUsuario;
    private final RolRepository repositorioRol;
    private final ProfesorRepository profesorRepository;

    public UsuarioService(UsuarioRepository repositorioUsuario, RolRepository repositorioRol, ProfesorRepository profesorRepository) {
        this.repositorioUsuario = repositorioUsuario;
        this.repositorioRol = repositorioRol;
        this.profesorRepository = profesorRepository;
    }


    /**
     * Lista todos los usuarios
     * @return List<Usuario>
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    public List<Usuario> listar() throws JsonNotFoundException, NotFoundException {
        List<Usuario> usuarios = new ArrayList<>();
        var dtoList = repositorioUsuario.getAll();
        for (UsuarioDTO dto : dtoList){
            var rol = validarRolExistente(dto.idRol());
            var profesor = validarProfesorExistente(dto.idProfesor());
            usuarios.add(Mapper.toUsuario(dto,rol,profesor));
        }
        return usuarios;
    }


    /**
     * Guarda un usuario
     * @param usuario que queremos save
     * @return Usuario que se guarda
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws BadRequestException si existe un usuario con ese username
     */
    public Usuario guardar(Usuario usuario) throws JsonNotFoundException, NotFoundException, BadRequestException {
        //Verificamos que no existe un usuario con ese username, sino lanzamos excepción
        var optional = repositorioUsuario.findByUsername(usuario.getUsername());
        if (optional.isPresent()){
            throw new BadRequestException(String.format("Ya existe un usuario con el nombre de usuario: %s", usuario.getUsername()));
        }

        //Tomamos el ID del rol y verificamos que existe, si no lanzamos excepción
        validarRolExistente(usuario.getRol().getId());

        //Tomamos el ID del profesor y verificamos que existe, si no lanzamos excepción
        validarProfesorExistente(usuario.getProfesor().getId());

        // Codifica la contraseña usando bcrypt
        usuario.setPassword(BCrypt.hashpw(usuario.getPassword(), BCrypt.gensalt()));


        repositorioUsuario.save(Mapper.usuarioToDto(usuario));
        return usuario;
    }


    /**
     * Elimina un usuario por ID
     * @param id del usuario que queremos eliminar
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws NotFoundException si no se encuentra un usuario con ese id
     */
    public void eliminar(Integer id) throws JsonNotFoundException, NotFoundException {
        //Verificamos que existe un usuario con ese ID, si no lanzamos excepción
        validarUsuarioExistente(id);

        repositorioUsuario.deleteById(id);
    }

    /**
     * Obtiene Usuario por ID
     * @param id del usuario a obtener
     * @return Usuario con el ID del parámetro
     * @throws JsonNotFoundException si ocurre un error con el archivo JSON
     * @throws NotFoundException si no encuentra el usuario con ese ID
     */
    public Usuario obtener(Integer id) throws JsonNotFoundException, NotFoundException {
        //Validamos que exista el usuario
        var dto = validarUsuarioExistente(id);

        //Validamos que exista el rol del usuario
        var rol = validarRolExistente(dto.idRol());
        var profesor = validarProfesorExistente(dto.idProfesor());

        //Mapeamos y retornamos el usuario con su rol y el profesor que representa
        return Mapper.toUsuario(dto,rol,profesor);
    }

    /**
     * Modifica un usuario
     * @param usuario que se quiere modificar
     * @throws JsonNotFoundException si ocurre un error con el archivo JSON
     * @throws NotFoundException si no encuentra el usuario que se quiere modificar
     */
    public void modificar(Usuario usuario) throws JsonNotFoundException, NotFoundException {
        //Validamos que exista el usuario
        validarUsuarioExistente(usuario.getId());

        //Validamos que exista su rol
        validarRolExistente(usuario.getRol().getId());

        //Validamos que exista el profesor que representa
        validarProfesorExistente(usuario.getProfesor().getId());

        // Codifica la contraseña usando bcrypt
        usuario.setPassword(BCrypt.hashpw(usuario.getPassword(), BCrypt.gensalt()));

        //Mapeamos y modificamos el usuario
        repositorioUsuario.modify(Mapper.usuarioToDto(usuario));
    }

    // Validaciones

    /**
     * Valida que exista un rol con ese ID
     * @param idRol del rol a validar
     * @return Rol si existe el rol
     * @throws NotFoundException si no existe rol con ese ID
     * @throws JsonNotFoundException sí existe un problema con el archivo JSON
     */
    private Rol validarRolExistente(Integer idRol) throws NotFoundException, JsonNotFoundException {
        return repositorioRol.findById(idRol)
                .orElseThrow(() -> new NotFoundException(String.format("No existe un rol con el id: %d", idRol)));
    }

    /**
     * Valida que exista un usuario con ese ID
     * @param idUsuario del usuario a validar
     * @return UsuarioDTO si existe el usuario
     * @throws NotFoundException si no existe usuario con ese ID
     * @throws JsonNotFoundException sí existe un problema con el archivo JSON
     */
    private UsuarioDTO validarUsuarioExistente(Integer idUsuario) throws NotFoundException, JsonNotFoundException {
        return repositorioUsuario.findById(idUsuario)
                .orElseThrow(() -> new NotFoundException(String.format("No existe un usuario con el id: %d", idUsuario)));
    }

    /**
     * Valida si existe un usuario con ese username
     * @param username a validar
     * @throws BadRequestException si existe usuario con ese username
     * @throws JsonNotFoundException sí existe un problema con el archivo JSON
     */
    private void validarUsernameUnico(String username) throws JsonNotFoundException, BadRequestException {
        var user = repositorioUsuario.findByUsername(username);
        if (user.isPresent()){
            throw new BadRequestException("El username ya esta en uso");
        }
    }

    /**
     * Valida la existencia de un Profesor por ID
     * @param id del profesor que se quiere verificar
     * @return Profesor si existe
     * @throws NotFoundException Si no se encuentra el profesor con ese ID
     * @throws JsonNotFoundException Sí ocurre un error con el archivo JSON
     */
    private Profesor validarProfesorExistente(Integer id) throws NotFoundException, JsonNotFoundException {
        return profesorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("No existe un profesor con el id: %d", id)));
    }
}
