package org.example.repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.exception.JsonNotFoundException;
import org.example.model.dto.UsuarioDTO;
import org.springframework.stereotype.Repository;


import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


/**
 * Repositorio de usuario
 * Implementa JSONRepository
 * Su responsabilidad es interactuar con el JSON
 */
@Repository
public class UsuarioRepository implements JSONRepository<Integer, UsuarioDTO> {
    private final String ruta = "./json/usuarios.json";
    /**
     * Retorna la ruta al json
     * que se quiere utilizar en el default
     * de la interfaz
     * @return la ruta al JSON
     */
    @Override
    public String getRuta() {
        return ruta;
    }

    /**
     * Obtiene el gson que utiliza este repositorio
     * @return Gson que se va a utilizar
     */
    @Override
    public Gson getGson() {
        return new Gson();
    }

    /**
     * Escribe un archivo JSON
     * @param list List<UsuarioDTO> que se quiere escribir
     * @throws JsonNotFoundException si ocurre un problema con el archivo JSON
     */
    public void write(List<UsuarioDTO> list) throws JsonNotFoundException {
        try (FileWriter writer = new FileWriter(getRuta())) {
            getGson().toJson(list, writer);
        } catch (IOException e) {
            throw new JsonNotFoundException(String.format("No se encontró el archivo JSON: %s", ruta));
        }
    }

    /**
     * Guarda un nuevo usuario en el JSON llamando a write
     * @param dto el nuevo usuario que se guarda en el JSON
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public void save(UsuarioDTO dto) throws JsonNotFoundException {
        // Listamos todos los usuarios (DTO)
        var dtos = getAll();

        // Verificamos el último ID y generamos el próximo
        var lastId = dtos.stream()
                .mapToInt(UsuarioDTO::id)
                .max()
                .orElse(0);
        dto = new UsuarioDTO(lastId + 1, dto.username(), dto.password(), dto.idRol(),dto.idProfesor());

        // Agregamos el nuevo usuario
        dtos.add(dto);

        // Guarda los cambios en el archivo JSON
        write(dtos);
    }

    /**
     * Devuelve la lista de usuarios que tenemos en el JSON
     * @return List<UsuarioDTO> lista de los DTO de usuarios en el JSON
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public List<UsuarioDTO> getAll() throws JsonNotFoundException {
        try (FileReader reader = new FileReader(ruta)) {
            //Usamos UsuarioDTO porque guardamos solo el ID del Rol que corresponde al usuario en este json
            var usuarioListType = new TypeToken<List<UsuarioDTO>>() {}.getType();
            return getGson().fromJson(reader, usuarioListType);
        } catch (IOException e) {
            throw new JsonNotFoundException(String.format("No se encontró el archivo JSON: %s", ruta));
        }
    }


    /**
     * Busca un usuario por ID
     * @param id para buscar y devolver el usuario
     * @return Optional<UsuarioDTO> el DTO si lo encuentra u Optional.empty() si no
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public Optional<UsuarioDTO> findById(Integer id) throws JsonNotFoundException {
        //Usamos stream para filtrar por id
        //Devuelve el usuario si existe
        //Devuelve optional.empty() sino
        return getAll().stream()
                .filter(dto -> Objects.equals(dto.id(), id))
                .findFirst();
    }


    /**
     * Borra un usuario por ID
     * @param id para buscar y borrar el usuario
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public void deleteById(Integer id) throws JsonNotFoundException {
        //Traemos todos los usuarios y borramos el que tenga ese ID
        var usuarios = getAll();
        usuarios.removeIf(dto -> Objects.equals(dto.id(), id));

        // Guarda los cambios en el archivo JSON
        write(usuarios);
    }

    /**
     * Modifica un usuario
     * @param dto que queremos modificar
     * @throws JsonNotFoundException si no encuentra el archivo JSON
     */
    @Override
    public void modify(UsuarioDTO dto) throws JsonNotFoundException {
        // Obtén todos los usuarios del JSON
        var dtos = getAll();

        // Busca el índice del usuario que deseas modificar
        int index = -1;
        for (int i = 0; i < dtos.size(); i++) {
            if (Objects.equals(dtos.get(i).id(), dto.id())) {
                index = i;
                break;
            }
        }
        // Si no se encuentra el índice, lanza la excepción
        if (index == -1) {
            throw new JsonNotFoundException(String.format("No se encontró el archivo JSON: %s", ruta));
        }
        // Reemplaza el objeto en la lista con una nueva instancia actualizada
        var nuevoDTO = new UsuarioDTO(dto.id(),dto.username(),dto.password(),dto.idRol(),dto.idProfesor());
        dtos.set(index, nuevoDTO);

        // Guarda los cambios en el archivo JSON
        write(dtos);
    }

    /**
     * Busca un usuario por username
     * @param username para buscar y devolver el usuario
     * @return Optional<UsuarioDTO> el DTO si lo encuentra u Optional.empty() si no
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    public Optional<UsuarioDTO> findByUsername(String username) throws JsonNotFoundException {
        //Usamos stream para filtrar por username
        //Devuelve el usuario si existe
        //Devuelve optional.empty() sino
        return getAll().stream()
                .filter(dto -> dto.username().equals(username))
                .findFirst();
    }
}

