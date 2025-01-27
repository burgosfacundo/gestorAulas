package org.example.repository;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.example.adaptadores.AdaptadorLocalDate;
import org.example.adaptadores.AdaptadorLocalDateTime;
import org.example.exception.JsonNotFoundException;
import org.example.model.dto.ReservaDTO;
import org.springframework.stereotype.Repository;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


/**
 * Repositorio de reservas
 * Implementa JSONRepository
 * Su responsabilidad es interactuar con el JSON
 */
@Repository
public class ReservaRepository implements JSONRepository<Integer, ReservaDTO> {
    private static final String RUTA = "./json/reservas.json";
    private static final String ERROR = String.format("No se encontró el archivo JSON: %s", RUTA);
    /**
     * Retorna la ruta al json
     * que se quiere utilizar en el default
     * de la interfaz
     * @return la ruta al JSON
     */
    @Override
    public String getRuta() {
        return RUTA;
    }

    /**
     * Obtiene el gson que utiliza este repositorio
     * @return Gson que se va a utilizar
     */
    @Override
    public Gson getGson() {
        return new GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new AdaptadorLocalDate())
                .registerTypeAdapter(LocalDateTime.class, new AdaptadorLocalDateTime())
                .create();
    }

    /**
     * Escribe un archivo JSON
     * @param list List<ReservaDTO> que se quiere escribir
     * @throws JsonNotFoundException si ocurre un problema con el archivo JSON
     */
    public void write(List<ReservaDTO> list) throws JsonNotFoundException {
        try (FileWriter writer = new FileWriter(getRuta())) {
            getGson().toJson(list, writer);
        } catch (IOException e) {
            throw new JsonNotFoundException(ERROR);
        }
    }

    /**
     * Guarda una nueva reserva en el JSON llamando a write
     * @param dto la nueva reserva que se guarda en el JSON
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public void save(ReservaDTO dto) throws JsonNotFoundException {
        // Listamos todos las reservas (DTO)
        var dtos = getAll();

        // Verificamos el último ID y generamos el próximo
        var lastId = dtos.stream()
                .mapToInt(ReservaDTO::id)
                .max()
                .orElse(0);

        dto = new ReservaDTO(lastId + 1, dto.fechaInicio(), dto.fechaFin(),
                                dto.idAula(),dto.idInscripcion(),dto.diasYBloques());

        // Agregamos la nueva reserva
        dtos.add(dto);

        // Guarda los cambios en el archivo JSON
        write(dtos);
    }

    /**
     * Devuelve la lista de reservas que tenemos en el JSON
     * @return List<UsuarioDTO> lista de los DTO de usuarios en el JSON
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public List<ReservaDTO> getAll() throws JsonNotFoundException {
        try (FileReader reader = new FileReader(RUTA)) {
            //Usamos ReservaDTO porque guardamos solo el ID de inscripción y aula que corresponde al usuario en este json
            var listType = new TypeToken<List<ReservaDTO>>() {}.getType();
            return getGson().fromJson(reader, listType);
        } catch (IOException e) {
            throw new JsonNotFoundException(ERROR);
        }
    }


    /**
     * Busca una reserva por ID
     * @param id para buscar y devolver la reserva
     * @return Optional<ReservaDTO> el DTO si lo encuentra u Optional.empty() si no
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public Optional<ReservaDTO> findById(Integer id) throws JsonNotFoundException {
        //Usamos stream para filtrar por id
        //Devuelve la reserva si existe
        //Devuelve optional.empty() sino
        return getAll().stream()
                .filter(dto -> Objects.equals(dto.id(), id))
                .findFirst();
    }


    /**
     * Borra una reserva por ID
     * @param id para buscar y borrar la reserva
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public void deleteById(Integer id) throws JsonNotFoundException {
        //Traemos todas las reservas y borramos el que tenga ese ID
        var reservas = getAll();
        reservas.removeIf(dto -> Objects.equals(dto.id(), id));

        // Guarda los cambios en el archivo JSON
        write(reservas);
    }

    /**
     * Modifica una reserva
     * @param dto que queremos modificar
     * @throws JsonNotFoundException si no encuentra el archivo JSON
     */
    @Override
    public void modify(ReservaDTO dto) throws JsonNotFoundException {
        // Obtén todas las reservas del JSON
        var dtos = getAll();

        // Busca el índice de la reserva que deseas modificar
        int index = -1;
        for (int i = 0; i < dtos.size(); i++) {
            if (Objects.equals(dtos.get(i).id(), dto.id())) {
                index = i;
                break;
            }
        }
        // Si no se encuentra el índice, lanza la excepción
        if (index == -1) {
            throw new JsonNotFoundException(ERROR);
        }
        // Reemplaza el objeto en la lista con una nueva instancia actualizada
        var nuevoDTO = new ReservaDTO(dto.id(), dto.fechaInicio(),dto.fechaFin(),dto.idAula(),
                dto.idInscripcion(),dto.diasYBloques());
        dtos.set(index, nuevoDTO);

        // Guarda los cambios en el archivo JSON
        write(dtos);
    }
}
