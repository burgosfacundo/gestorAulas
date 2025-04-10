package org.example.service;


import lombok.RequiredArgsConstructor;
import org.example.exception.BadRequestException;
import org.example.exception.NotFoundException;
import org.example.model.Usuario;
import org.example.model.dto.UsuarioDTO;
import org.example.repository.UsuarioRepository;
import org.example.utils.Mapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
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
    /**
     * Lista todos los usuarios
     * @return List<UsuarioDTO>
     */
    public List<Usuario> listar() {
        return repositorioUsuario.findAll();
    }

    /**
     * Guarda un usuario
     *
     * @param dto que queremos save
     * @throws BadRequestException si existe un usuario con ese username
     */
    public void guardar(UsuarioDTO dto) throws NotFoundException, BadRequestException {
        try {
            repositorioUsuario.save(Mapper.toUsuario(dto));
        } catch (DataIntegrityViolationException e) {
            // Analizamos la causa
            String causa = e.getMostSpecificCause().getMessage();

            if (causa.contains("usuarios.username_UNIQUE")) { // Nombre de la restricción en la DB
                throw new BadRequestException(String.format("El nombre de usuario '%s' ya está en uso.", dto.username()));
            } else if (causa.contains("FOREIGN KEY (`rol_id`)")) {
                throw new NotFoundException("El rol especificado no existe.");
            } else if (causa.contains("FOREIGN KEY (`profesor_id`)")) {
                throw new NotFoundException("El profesor especificado no existe.");
            }

            throw new BadRequestException("Error de integridad de datos.");
        }
    }

    /**
     * Elimina un usuario por ID
     * @param id del usuario que queremos eliminar
     * @throws NotFoundException si no se encuentra un usuario con ese id
     */
    public void eliminar(Integer id) throws NotFoundException {
        try{
            repositorioUsuario.deleteById(id);
        }catch (EmptyResultDataAccessException e){
            throw new NotFoundException("El usuario no existe");
        }
    }

    /**
     * Obtiene Usuario por ID
     * @param id del usuario a obtener
     * @return UsuarioDTO con el ID del parámetro
     * @throws NotFoundException si no encuentra el usuario con ese ID
     */
    public UsuarioDTO obtener(Integer id) throws NotFoundException {
        return repositorioUsuario.findById(id)
                .map(Mapper::usuarioToDto)
                .orElseThrow(() -> new NotFoundException("No existe el usuario"));
    }

    /**
     * Modifica un usuario
     * @param dto que se quiere modificar
     * @throws NotFoundException si no encuentra el usuario que se quiere modificar
     */
    public void modificar(UsuarioDTO dto) throws NotFoundException {
        // Validamos que el usuario exista
        if (!repositorioUsuario.existsById(dto.id())) {
            throw new NotFoundException("El usuario no existe");
        }

        try {
            repositorioUsuario.save(Mapper.toUsuario(dto));
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("Uno de los valores referenciados (Rol o Profesor) no existe.");
        }
    }
}
