package org.example.service;



import lombok.AllArgsConstructor;
import org.example.enums.BloqueHorario;
import org.example.exception.BadRequestException;
import org.example.exception.JsonNotFoundException;
import org.example.exception.NotFoundException;
import org.example.model.Aula;
import org.example.model.Laboratorio;
import org.example.model.dto.ReservaDTO;
import org.example.repository.AulaRepository;
import org.example.repository.ReservaRepository;
import org.example.utils.Utils;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;



/**
 * Clase que se encarga de comunicarse con el repositorio
 * y aplicar la lógica de negocio para manipular aulas
 */
@Service
@AllArgsConstructor
public class AulaService{
    private final AulaRepository repositorio;
    private final ReservaRepository reservaRepository;


    /**
     * Lista todas las aulas
     * @return List<Aula>
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    public List<Aula> listar() throws JsonNotFoundException {
        return repositorio.getAll();
    }

    /**
     * Guarda un aula
     * @param aula que queremos guarde
     * @return Aula que se guarda
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws BadRequestException si existe un aula con ese código
     */
    public Aula guardar(Aula aula) throws JsonNotFoundException, BadRequestException {
        // Verificamos que no existe esa aula y si existe lanzamos la excepción
        var optional = repositorio.findById(aula.getId());
        if (optional.isPresent()){
            throw new BadRequestException(String.format("Ya existe el aula o laboratorio %s", aula.getId()));
        }

        repositorio.save(aula);
        return aula;
    }

    /**
     * Elimina un aula por ID
     * @param id del aula que queremos eliminar
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws NotFoundException si no se encuentra un aula con ese numero
     */
    public void eliminar(Integer id) throws JsonNotFoundException, NotFoundException {
        // Verificamos que existe una asignatura con ese ID y si no lanzamos la excepción
        var aula = validarAulaExistenteById(id);

        // Borramos esa aula por ID
        repositorio.deleteById(aula.getId());
    }

    /**
     * Obtiene un aula por ID
     * @param id del aula que queremos obtener
     * @return Aula con ese ID
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws NotFoundException si no se encuentra un aula con ese ID
     */
    public Aula obtener(Integer id) throws JsonNotFoundException, NotFoundException {
        // Validamos y retornamos el aula por ID
        return validarAulaExistenteById(id);
    }

    /**
     * Modifica un aula
     * @param aula modificada
     * @throws JsonNotFoundException si no encuentra el archivo JSON
     * @throws NotFoundException si no encuentra el aula
     */
    public void modificar(Aula aula) throws JsonNotFoundException, NotFoundException {
        //Verificamos que el aula con ese ID exista
        validarAulaExistenteById(aula.getId());

        //la modificamos
        repositorio.modify(aula);
    }

    //Filtros

    /**
     * Filtra los laboratorios
     * @return List<Laboratorio> laboratorios encontrados
     * @throws JsonNotFoundException si ocurre un problema con el archivo JSON
     */
    public List<Laboratorio> listarLaboratorios() throws JsonNotFoundException {
        return listar().stream()
                .filter(aula -> aula instanceof Laboratorio)
                .map(aula -> (Laboratorio) aula)// Filtra si es instancia de Laboratorio
                .collect(Collectors.toList());
    }

    /**
     * Filtra solo las Aulas estándar, excluyendo los Laboratorios.
     * @return List<Aula> lista de aulas estándar (sin incluir laboratorios)
     * @throws JsonNotFoundException sí existe un problema con el archivo JSON
     */
    public List<Aula> listarAulas() throws JsonNotFoundException {
        return listar().stream()
                .filter(aula -> !(aula instanceof Laboratorio)) // Filtra si no es instancia de Laboratorio
                .toList();
    }

    /**
     * Filtra para aplicar filtros en los espacios
     * @param espacios la lista a filtrar
     * @param capacidad mínima del espacio
     * @param tieneProyector si tiene o no proyector
     * @param tieneTV si tiene o no TV
     * @param computadoras mínima del espacio (si aplica)
     * @return <T extends Aula> List<T>  lista de espacios
     * @param <T> si queremos utilizar Aula o Laboratorio
     */
    private <T extends Aula> List<T> filtrarEspacios(List<T> espacios, Integer capacidad, Boolean tieneProyector, Boolean tieneTV, Integer computadoras) {
        return espacios.stream()
                .filter(aula -> capacidad == null || aula.getCapacidad() >= capacidad)
                .filter(aula -> tieneProyector == null || aula.isTieneProyector() == tieneProyector)
                .filter(aula -> tieneTV == null || aula.isTieneTV() == tieneTV)
                .filter(aula -> computadoras == null || !(aula instanceof Laboratorio) || ((Laboratorio) aula).getComputadoras() >= computadoras)
                .toList();
    }


    /**
     * Filtra Espacios por capacidad, proyector y TV.
     * Permite que cualquiera de los parámetros sea null, y solo aplicará el filtro correspondiente si no es null.
     * @param capacidad la capacidad mínima requerida (puede ser null).
     * @param tieneProyector sí debe tener o no proyector (puede ser null).
     * @param tieneTV sí debe tener o no TV (puede ser null).
     * @return List<Aula> lista de espacios que cumplan con las condiciones.
     * @throws JsonNotFoundException Sí existe un problema con el archivo JSON.
     */
    public List<Aula> filtrarEspaciosPorCondiciones(Integer capacidad, Boolean tieneProyector, Boolean tieneTV) throws JsonNotFoundException {
        return filtrarEspacios(listar(), capacidad, tieneProyector, tieneTV, null);
    }


    /**
     * Filtra Aulas estándar por capacidad, proyector y TV.
     * Permite que cualquiera de los parámetros sea null, y solo aplicará el filtro correspondiente si no es null.
     * @param capacidad la capacidad mínima requerida (puede ser null).
     * @param tieneProyector sí debe tener o no proyector (puede ser null).
     * @param tieneTV sí debe tener o no TV (puede ser null).
     * @return List<Aula> lista de aulas estándar que cumplan con las condiciones.
     * @throws JsonNotFoundException Sí existe un problema con el archivo JSON.
     */
    public List<Aula> filtrarAulasPorCondiciones(Integer capacidad, Boolean tieneProyector, Boolean tieneTV) throws JsonNotFoundException {
        return filtrarEspacios(listarAulas(), capacidad, tieneProyector, tieneTV, null);
    }


    /**
     * Filtra Laboratorios por capacidad, proyector y TV.
     * Permite que cualquiera de los parámetros sea null, y solo aplicará el filtro correspondiente si no es null.
     * @param capacidad la capacidad mínima requerida (puede ser null).
     * @param tieneProyector sí debe tener o no proyector (puede ser null).
     * @param tieneTV sí debe tener o no TV (puede ser null).
     * @param computadoras las computadoras mínimas requeridas (puede ser null).
     * @return List<Laboratorio> lista de laboratorios que cumplan con las condiciones.
     * @throws JsonNotFoundException Sí existe un problema con el archivo JSON.
     */
    public List<Laboratorio> filtrarLaboratoriosPorCondiciones(Integer capacidad, Boolean tieneProyector, Boolean tieneTV, Integer computadoras) throws JsonNotFoundException {
        return filtrarEspacios(listarLaboratorios(), capacidad, tieneProyector, tieneTV, computadoras)
                .stream()
                .toList();
    }

    /**
     * Obtiene los id de las aulas ocupadas en ese rango de fecha con esos días y bloques
     * @param fechaInicio del rango de fecha
     * @param fechaFin del rango de fecha
     * @param diasYBloques que queremos filtrar
     * @return List<Integer> ids de las aulas ocupadas
     * @throws JsonNotFoundException si ocurre un problema con el archivo JSON
     */
    private List<Integer> obtenerIdsEspaciosOcupados(LocalDate fechaInicio, LocalDate fechaFin, Map<DayOfWeek, Set<BloqueHorario>> diasYBloques) throws JsonNotFoundException {
        return reservaRepository.getAll()
                .stream()
                .filter(r -> Utils.seSolapanFechas(fechaInicio, fechaFin, r.fechaInicio(), r.fechaFin()))
                .filter(r -> Utils.tieneSolapamientoEnDiasYBloques(r.diasYBloques(), diasYBloques))
                .map(ReservaDTO::idAula)
                .toList();
    }
    /**
     * Filtra Aulas por fecha, período y días/bloques de la semana.
     * @param fechaInicio desde qué fecha debe estar disponible.
     * @param fechaFin hasta qué fecha debe estar disponible.
     * @param diasYBloques mapa con los días de la semana y sus respectivos bloques horarios.
     * @return List<Aula> lista de aulas que cumplen con las condiciones.
     * @throws JsonNotFoundException sí existe un problema con el archivo JSON.
     */
    public List<Aula> listarEspaciosDisponibles(LocalDate fechaInicio, LocalDate fechaFin,
                                             Map<DayOfWeek, Set<BloqueHorario>> diasYBloques)
            throws JsonNotFoundException {
        // Filtramos las reservas existentes que coinciden en los días y bloques horarios
        var idsAulasSolapadas = obtenerIdsEspaciosOcupados(fechaInicio,fechaFin,diasYBloques);

        // Filtramos las aulas disponibles que no están en las reservas solapadas
        return listar().stream()
                .filter(aula -> !idsAulasSolapadas.contains(aula.getId()))
                .toList();
    }


    /**
     * Filtra solo Aulas disponibles por fecha, período y días/bloques de la semana.
     * @param fechaInicio desde qué fecha debe estar disponible.
     * @param fechaFin hasta qué fecha debe estar disponible.
     * @param diasYBloques mapa con los días de la semana y sus respectivos bloques horarios.
     * @return List<Aula> lista de aulas disponibles que cumplen con las condiciones.
     * @throws JsonNotFoundException sí existe un problema con el archivo JSON.
     */
    public List<Aula> listarAulasDisponibles(LocalDate fechaInicio, LocalDate fechaFin,
                                             Map<DayOfWeek, Set<BloqueHorario>> diasYBloques) throws JsonNotFoundException {
        // Filtramos las reservas existentes que coinciden en los días y bloques horarios
        var idsAulasSolapadas = obtenerIdsEspaciosOcupados(fechaInicio,fechaFin,diasYBloques);

        // Filtramos las Aulas disponibles que no están en las reservas solapadas
        return listarAulas().stream()
                .filter(aula -> !idsAulasSolapadas.contains(aula.getId()))
                .toList();
    }


    /**
     * Filtra solo Laboratorios disponibles por fecha, período y días/bloques de la semana.
     * @param fechaInicio desde qué fecha debe estar disponible.
     * @param fechaFin hasta qué fecha debe estar disponible.
     * @param diasYBloques mapa con los días de la semana y sus respectivos bloques horarios.
     * @return List<Laboratorio> lista de laboratorios disponibles que cumplen con las condiciones.
     * @throws JsonNotFoundException sí existe un problema con el archivo JSON.
     */
    public List<Laboratorio> listarLaboratoriosDisponibles(LocalDate fechaInicio, LocalDate fechaFin,
                                                           Map<DayOfWeek, Set<BloqueHorario>> diasYBloques) throws JsonNotFoundException {
        // Filtramos las reservas existentes que coinciden en los días y bloques horarios
        var idsLaboratoriosSolapados = obtenerIdsEspaciosOcupados(fechaInicio,fechaFin,diasYBloques);

        // Filtramos los Laboratorios disponibles que no están en las reservas solapadas
        return listarLaboratorios().stream()
                .filter(laboratorio -> !idsLaboratoriosSolapados.contains(laboratorio.getId()))
                .toList();
    }

    /**
     * Filtra espacios disponibles con los filtros
     * @param capacidad minima que se necesita
     * @param tieneProyector si tiene o no proyector
     * @param tieneTV si tiene o no TV
     * @param fechaInicio inicio del rango de fecha
     * @param fechaFin fin del rango de fecha
     * @param diasYBloques en las cuales que deben estar disponibles
     * @return List<Aula> disponibles con esas condiciones en ese rango de fecha
     * @throws JsonNotFoundException si ocurre un problema con el archivo JSON
     */
    public List<Aula> listarEspaciosDisponiblesConCondiciones(Integer capacidad, Boolean tieneProyector, Boolean tieneTV, LocalDate fechaInicio, LocalDate fechaFin, Map<DayOfWeek, Set<BloqueHorario>> diasYBloques) throws JsonNotFoundException {
        var espaciosDisponibles = listarEspaciosDisponibles(fechaInicio, fechaFin, diasYBloques);
        return filtrarEspacios(espaciosDisponibles, capacidad, tieneProyector, tieneTV, null);
    }


    /**
     * Filtra aulas disponibles con los filtros
     * @param capacidad minima que se necesita
     * @param tieneProyector si tiene o no proyector
     * @param tieneTV si tiene o no TV
     * @param fechaInicio inicio del rango de fecha
     * @param fechaFin fin del rango de fecha
     * @param diasYBloques en las cuales que deben estar disponibles
     * @return List<Aula> disponibles con esas condiciones en ese rango de fecha
     * @throws JsonNotFoundException si ocurre un problema con el archivo JSON
     */
    public List<Aula> listarAulasDisponiblesConCondiciones(Integer capacidad, Boolean tieneProyector, Boolean tieneTV, LocalDate fechaInicio, LocalDate fechaFin, Map<DayOfWeek, Set<BloqueHorario>> diasYBloques) throws JsonNotFoundException {
        var aulasDisponibles = listarAulasDisponibles(fechaInicio, fechaFin, diasYBloques);
        return filtrarEspacios(aulasDisponibles, capacidad, tieneProyector, tieneTV, null);
    }


    /**
     * Filtra laboratorios disponibles con los filtros
     * @param capacidad minima que se necesita
     * @param tieneProyector si tiene o no proyector
     * @param tieneTV si tiene o no TV
     * @param fechaInicio inicio del rango de fecha
     * @param fechaFin fin del rango de fecha
     * @param diasYBloques en las cuales que deben estar disponibles
     * @return List<Laboratorio> disponibles con esas condiciones en ese rango de fecha
     * @throws JsonNotFoundException si ocurre un problema con el archivo JSON
     */
    public List<Laboratorio> listarLaboratoriosDisponiblesConCondiciones(Integer computadoras, Integer capacidad, Boolean tieneProyector, Boolean tieneTV, LocalDate fechaInicio, LocalDate fechaFin, Map<DayOfWeek, Set<BloqueHorario>> diasYBloques) throws JsonNotFoundException {
        var laboratoriosDisponibles = listarLaboratoriosDisponibles(fechaInicio, fechaFin, diasYBloques);
        return filtrarEspacios(laboratoriosDisponibles, capacidad, tieneProyector, tieneTV, computadoras)
                .stream()
                .toList();
    }




    // Validaciones
    /**
     * Valida la existencia de un Aula por ID
     * @param idAula del aula que se quiere verificar
     * @return Aula si existe
     * @throws NotFoundException Si no se encuentra el aula con ese ID
     * @throws JsonNotFoundException Sí ocurre un error con el archivo JSON
     */
    private Aula validarAulaExistenteById(Integer idAula) throws NotFoundException, JsonNotFoundException {
        return repositorio.findById(idAula)
                .orElseThrow(() -> new NotFoundException(String.format("No existe un aula con el id: %s", idAula)));
    }
}