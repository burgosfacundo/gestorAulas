package org.example.service;


import lombok.RequiredArgsConstructor;
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
public class RolService {
    private final RolRepository repositorio;

    /**
     * Lista todos los roles
     *
     * @return List<Rol>
     */
    public List<Rol> listar() {
        return repositorio.findAll();
    }

    /**
     * Obtiene un rol por ID
     *
     * @param id del rol que queremos obtener
     * @return Rol con ese ID
     * @throws NotFoundException si no se encuentra un rol con ese ID
     */
    public Rol obtener(Integer id) throws NotFoundException {
        return repositorio.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe el rol"));
    }

    public Rol validarRolPorNombre(String nombre) throws NotFoundException {
        return repositorio.findByNombre(nombre)
                .orElseThrow(() -> new NotFoundException(String.format("No existe un rol con el nombre: %s", nombre)));
    }
}