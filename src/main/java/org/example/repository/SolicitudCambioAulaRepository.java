package org.example.repository;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.example.adaptadores.AdaptadorLocalDate;
import org.example.adaptadores.AdaptadorLocalDateTime;
import org.example.enums.BloqueHorario;
import org.example.enums.EstadoSolicitud;
import org.example.exception.JsonNotFoundException;
import org.example.model.dto.SolicitudCambioAulaDTO;
import org.springframework.stereotype.Repository;


import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Repositorio de solicitud de cambio de aula
 * Implementa JSONRepository
 * Su responsabilidad es interactuar con el JSON
 */
@Repository
public class SolicitudCambioAulaRepository implements JSONRepository<Integer, SolicitudCambioAulaDTO>{
    private final String ruta = "./json/solicitudes.json";
    /**
     * Retorna la ruta al json
     * que se quiere utilizar en el default
     * de la interfaz
     * @return la ruta al JSON
     */
    @Override
    public String getRuta() {
        return this.ruta;
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
     * @param list List<SolicitudCambioAulaDTO> que se quiere escribir
     * @throws JsonNotFoundException si ocurre un problema con el archivo JSON
     */
    public void write(List<SolicitudCambioAulaDTO> list) throws JsonNotFoundException {
        try (FileWriter writer = new FileWriter(getRuta())) {
            getGson().toJson(list, writer);
        } catch (IOException e) {
            throw new JsonNotFoundException(String.format("No se encontró el archivo JSON: %s", ruta));
        }
    }

    /**
     * Guarda una nueva solicitud en el JSON llamando a write
     * @param dto la nueva solicitud que se guarda en el JSON
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public void save(SolicitudCambioAulaDTO dto) throws JsonNotFoundException {
        // Listamos todos las solicitudes (DTO)
        var dtos = getAll();

        // Verificamos el último ID y generamos el próximo
        var lastId = dtos.stream()
                .mapToInt(SolicitudCambioAulaDTO::id)
                .max()
                .orElse(0);

        dto = new SolicitudCambioAulaDTO(lastId + 1, dto.idProfesor(),dto.idReserva(),dto.idAula(),dto.estadoSolicitud(),
                dto.tipoSolicitud(),dto.fechaInicio(),dto.fechaFin(),dto.diasYBloques(), dto.comentarioEstado(),
                dto.comentarioProfesor(),dto.fechaHoraSolicitud());

        // Agregamos la nueva solicitud
        dtos.add(dto);

        // Guarda los cambios en el archivo JSON
        write(dtos);
    }

    /**
     * Devuelve la lista de solicitudes que tenemos en el JSON
     * @return List<SolicitudCambioAulaDTO> lista de los DTO de solicitudes en el JSON
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public List<SolicitudCambioAulaDTO> getAll() throws JsonNotFoundException {
        try (FileReader reader = new FileReader(ruta)) {
            //Usamos SolicitudCambioAulaDTO porque guardamos solo el ID de Clases que contiene en el json
            var listType = new TypeToken<List<SolicitudCambioAulaDTO>>() {}.getType();
            return getGson().fromJson(reader, listType);
        } catch (IOException e) {
            throw new JsonNotFoundException(String.format("No se encontró el archivo JSON: %s", ruta));
        }
    }

    /**
     * Busca una solicitud por ID
     * @param id para buscar y devolver la solicitud
     * @return Optional<SolicitudCambioAulaDTO> el DTO si lo encuentra u Optional.empty() si no
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public Optional<SolicitudCambioAulaDTO> findById(Integer id) throws JsonNotFoundException {
        //Usamos stream para filtrar por id
        //Devuelve la solicitud si existe
        //Devuelve optional.empty() sino
        return getAll().stream()
                .filter(dto -> Objects.equals(dto.id(), id))
                .findFirst();
    }

    /**
     * Borra una solicitud por ID
     * @param id para buscar y borrar la solicitud
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public void deleteById(Integer id) throws JsonNotFoundException {
        //Traemos todas las solicitudes y borramos el que tenga ese ID
        var solicitudes = getAll();
        solicitudes.removeIf(dto -> Objects.equals(dto.id(), id));

        // Guarda los cambios en el archivo JSON
        write(solicitudes);
    }

    /**
     * Modifica una solicitud
     * @param dto que queremos modificar
     * @throws JsonNotFoundException si no encuentra el archivo JSON
     */
    @Override
    public void modify(SolicitudCambioAulaDTO dto) throws JsonNotFoundException {
        // Obtén todas las solicitudes del JSON
        var dtos = getAll();

        // Busca el índice de la solicitud que deseas modificar
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
        var nuevoDTO = new SolicitudCambioAulaDTO(dto.id(), dto.idProfesor(),dto.idReserva(),dto.idAula(),dto.estadoSolicitud(),
                dto.tipoSolicitud(),dto.fechaInicio(),dto.fechaFin(),dto.diasYBloques(), dto.comentarioEstado(),
                dto.comentarioProfesor(),dto.fechaHoraSolicitud());
        dtos.set(index, nuevoDTO);

        // Guarda los cambios en el archivo JSON
        write(dtos);
    }

    /**
     * Busca una solicitud de cambio de aula que coincida con los parámetros dados.
     * @param idProfesor    el ID del profesor
     * @param idAula        el ID del aula
     * @param idReserva     el ID de la reserva original
     * @param fechaInicio   la fecha de inicio de la solicitud
     * @param fechaFin      la fecha de fin de la solicitud
     * @param diasYBloques el bloque horario y los días de la semana de la solicitud
     * @return Optional<SolicitudCambioAulaDTO> el DTO si se encuentra una coincidencia u Optional.empty() si no
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    public Optional<SolicitudCambioAulaDTO> find(Integer idProfesor, Integer idAula, Integer idReserva,
                                                 LocalDate fechaInicio, LocalDate fechaFin,
                                                 Map<DayOfWeek, Set<BloqueHorario>> diasYBloques) throws JsonNotFoundException {
        return getAll().stream()
                .filter(dto -> Objects.equals(dto.idProfesor(), idProfesor) &&
                        Objects.equals(dto.idAula(), idAula) &&
                        Objects.equals(dto.idReserva(), idReserva) &&
                        dto.fechaInicio().equals(fechaInicio) &&
                        dto.fechaFin().equals(fechaFin) &&
                        dto.diasYBloques().equals(diasYBloques))
                .findFirst();
    }



    /**
     * Busca las solicitudes de cambio de aula que coincida con los parámetros dados.
     * @param idAula        el ID del aula
     * @param fechaInicio   la fecha de inicio de las solicitudes
     * @param fechaFin      la fecha de fin de las solicitudes
     * @param diasYBloques el bloque horario y los días de la semana de la solicitud
     * @param estadoSolicitud el estado de las solicitudes
     * @return List<SolicitudCambioAulaDTO> lista de las solicitudes que cumplan con estos parámetros
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    public List<SolicitudCambioAulaDTO> find(Integer idAula, LocalDate fechaInicio,
                                             LocalDate fechaFin, Map<DayOfWeek, Set<BloqueHorario>> diasYBloques,
                                             EstadoSolicitud estadoSolicitud) throws JsonNotFoundException {
        return getAll().stream()
                .filter(dto -> Objects.equals(dto.idAula(), idAula) &&
                        dto.fechaInicio().equals(fechaInicio) &&
                        dto.fechaFin().equals(fechaFin) &&
                        dto.diasYBloques().equals(diasYBloques) &&
                        dto.estadoSolicitud().equals(estadoSolicitud))
                .collect(Collectors.toList());
    }


}
