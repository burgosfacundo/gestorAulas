package org.example.service;


import lombok.RequiredArgsConstructor;
import org.example.exception.BadRequestException;
import org.example.exception.NotFoundException;
import org.example.model.Rol;
import org.example.repository.RolRepository;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Clase que se encarga de comunicarse con el repositorio
 * y aplicar la lógica de negocio para manipular roles
 */
@Service
@RequiredArgsConstructor
public class RolService{
    private final RolRepository repositorio;

    /**
     * Lista todos los roles
     * @return List<Rol>
     */
    public List<Rol> listar() {
        return repositorio.findAll();
    }


    /**
     * Guarda un rol
     * @param rol que queremos guarde
     * @return Rol que se guarda* @throws BadRequestException si existe un rol con ese nombre
     */
    public Rol guardar(Rol rol) throws BadRequestException {
        // Verificamos que no existe ese rol y si existe lanzamos la excepción
        var optional = repositorio.findByNombre(rol.getNombre());
        if (optional.isPresent()){
            throw new BadRequestException(String.format("Ya existe un rol: %s", rol.getNombre()));
        }

        repositorio.save(rol);
        return rol;
    }

    /**
     * Elimina un rol por ID
     * @param id del rol que queremos eliminar
     * @throws NotFoundException si no se encuentra un rol con ese ID
     */
    public void eliminar(Integer id) throws NotFoundException {
        // Verificamos que existe un rol con ese ID y si no lanzamos la excepción
        validarRolExistente(id);

        //Borramos el rol con ese ID
        repositorio.deleteById(id);
    }

    /**
     * Obtiene un rol por ID
     * @param id del rol que queremos obtener
     * @return Rol con ese ID
     * @throws NotFoundException si no se encuentra un rol con ese ID
     */
    public Rol obtener(Integer id) throws NotFoundException {
        // Validamos y retornamos el rol
        return validarRolExistente(id);
    }

    // Validaciones

    /**
     * Valida que exista un rol con ese ID
     * @param idRol del rol a validar
     * @return Rol si existe el rol
     * @throws NotFoundException si no existe rol con ese ID
     */
    private Rol validarRolExistente(Integer idRol) throws NotFoundException {
        return repositorio.findById(idRol)
                .orElseThrow(() -> new NotFoundException(String.format("No existe un rol con el id: %d", idRol)));
    }

    public Rol validarRolPorNombre(String nombre) throws NotFoundException{
        return repositorio.findByNombre(nombre)
                .orElseThrow(() -> new NotFoundException(String.format("No existe un rol con el nombre: %s", nombre)));
    }
}
