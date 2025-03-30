package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.BadRequestException;
import org.example.exception.NotFoundException;
import org.example.model.*;
import org.example.repository.AulaRepository;
import org.example.repository.EspacioBaseRepository;
import org.example.repository.LaboratorioRepository;
import org.example.repository.ReservaRepository;
import org.example.utils.Utils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;


/**
 * Clase que se encarga de comunicarse con el repositorio
 * y aplicar la lógica de negocio para manipular espacios
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EspacioService {
    private final EspacioBaseRepository<Espacio> espacioBaseRepository;
    private final AulaRepository aulaRepository;
    private final LaboratorioRepository laboratorioRepository;
    private final ReservaRepository reservaRepository;


    /**
     * Lista todas las aulas
     * @return List<Aula>
     */
    public List<Espacio> listar() {
        return espacioBaseRepository.findAll();
    }

    /**
     * Guarda un espacio
     * @param espacio que queremos guarde
     * @return espacio que se guarda
     * @throws BadRequestException sí existe un espacio con ese código
     */
    public Espacio guardar(Espacio espacio) throws BadRequestException {
        // Verificamos que no existe esa aula y si existe lanzamos la excepción
        if (espacioBaseRepository.findByNumero(espacio.getNumero()).isPresent()){
            throw new BadRequestException(String.format("Ya existe el aula o laboratorio %s", espacio.getNumero()));
        }

        espacioBaseRepository.save(espacio);
        return espacio;
    }

    /**
     * Elimina un espacio por ID
     * @param id del espacio que queremos eliminar
     * @throws NotFoundException si no se encuentra un espacio con ese numero
     */
    public void eliminar(Integer id) throws NotFoundException {
        // Verificamos que existe una asignatura con ese ID y si no lanzamos la excepción
        validarEspacioExistenteById(id);

        // Borramos esa aula por ID
        espacioBaseRepository.deleteById(id);
    }

    /**
     * Obtiene un espacio por ID
     * @param id del espacio que queremos obtener
     * @return espacio con ese ID
     * @throws NotFoundException si no se encuentra un aula con ese ID
     */
    public Espacio obtener(Integer id) throws NotFoundException {
        // Validamos y retornamos el aula por ID
        return validarEspacioExistenteById(id);
    }

    /**
     * Modifica un espacio
     * @param espacio modificada
     * @throws NotFoundException si no encuentra el espacio
     */
    public void modificar(Espacio espacio) throws NotFoundException, BadRequestException {
        //Verificamos que el espacio con ese ID exista
        validarEspacioExistenteById(espacio.getId());

        if (espacioBaseRepository.findByNumero(espacio.getNumero()).isPresent()){
            throw new BadRequestException("Ya existe un espacio con ese número");
        }

        //la modificamos
        espacioBaseRepository.save(espacio);
    }

    //Filtros

    /**
     * Filtra los laboratorios
     * @return List<Laboratorio> laboratorios encontrados
     */
    public List<Laboratorio> listarLaboratorios() {
        return laboratorioRepository.findAll();
    }

    /**
     * Filtra solo las Aulas estándar, excluyendo los Laboratorios.
     * @return List<Aula> paginación de aulas estándar (sin incluir laboratorios)
     */
    public List<Aula> listarAulas() {
        return aulaRepository.findAll();
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
    private <T extends Espacio> List<T> filtrarEspacios(List<T> espacios, Integer capacidad, Boolean tieneProyector, Boolean tieneTV, Integer computadoras) {
        return espacios.stream()
                .filter(e -> capacidad == null || e.getCapacidad() >= capacidad)
                .filter(e -> tieneProyector == null || e.isTieneProyector() == tieneProyector)
                .filter(e -> tieneTV == null || e.isTieneTV() == tieneTV)
                .filter(e -> computadoras == null || !(e instanceof Laboratorio) || ((Laboratorio) e).getComputadoras() >= computadoras)
                .toList();
    }

    /**
     * Obtiene los ID de las aulas ocupadas en ese rango de fecha con esos días y bloques
     * @param fechaInicio del rango de fecha
     * @param fechaFin del rango de fecha
     * @param diasYBloques que queremos filtrar
     * @return List<Integer> ids de las aulas ocupadas
     */
    private List<Integer> obtenerIdsEspaciosOcupados(LocalDate fechaInicio, LocalDate fechaFin,
                                                     Set<DiaBloque> diasYBloques){
        return reservaRepository.findAll()
                .parallelStream()
                .filter(r -> Utils.seSolapanFechas(fechaInicio, fechaFin, r.getFechaInicio(), r.getFechaFin()))
                .filter(r -> Utils.tieneSolapamientoEnDiasYBloques(r.getDiasYBloques() , diasYBloques))
                .map(Reserva::getId)
                .toList();
    }
    /**
     * Filtra Espacios por fecha, período y días/bloques de la semana.
     * @param fechaInicio desde qué fecha debe estar disponible.
     * @param fechaFin hasta qué fecha debe estar disponible.
     * @param diasYBloques mapa con los días de la semana y sus respectivos bloques horarios.
     * @return List<Espacio> lista de espacios que cumplen con las condiciones.
     */
    public List<Espacio> listarEspaciosDisponibles(LocalDate fechaInicio, LocalDate fechaFin,
                                                Set<DiaBloque> diasYBloques) {
        // Filtramos las reservas existentes que coinciden en los días y bloques horarios
        var idsAulasSolapadas = obtenerIdsEspaciosOcupados(fechaInicio,fechaFin,diasYBloques);

        // Filtramos los espacios disponibles que no están en las reservas solapadas
        return espacioBaseRepository.findAll().parallelStream()
                .filter(e -> !idsAulasSolapadas.contains(e.getId()))
                .toList();
    }

    /**
     * Filtra solo Aulas disponibles por fecha, período y días/bloques de la semana.
     * @param fechaInicio desde qué fecha debe estar disponible.
     * @param fechaFin hasta qué fecha debe estar disponible.
     * @param diasYBloques mapa con los días de la semana y sus respectivos bloques horarios.
     * @return List<Aula> lista de aulas disponibles que cumplen con las condiciones.
     */
    public List<Aula> listarAulasDisponibles(LocalDate fechaInicio, LocalDate fechaFin,
                                             Set<DiaBloque> diasYBloques) {
        // Filtramos las reservas existentes que coinciden en los días y bloques horarios
        var idsAulasSolapadas = obtenerIdsEspaciosOcupados(fechaInicio,fechaFin,diasYBloques);

        // Filtramos las Aulas disponibles que no están en las reservas solapadas
        return listarAulas().parallelStream()
                .filter(aula -> !idsAulasSolapadas.contains(aula.getId()))
                .toList();
    }


    /**
     * Filtra solo Laboratorios disponibles por fecha, período y días/bloques de la semana.
     * @param fechaInicio desde qué fecha debe estar disponible.
     * @param fechaFin hasta qué fecha debe estar disponible.
     * @param diasYBloques mapa con los días de la semana y sus respectivos bloques horarios.
     * @return List<Laboratorio> lista de laboratorios disponibles que cumplen con las condiciones.
     */
    public List<Laboratorio> listarLaboratoriosDisponibles(LocalDate fechaInicio, LocalDate fechaFin,
                                                           Set<DiaBloque> diasYBloques) {
        // Filtramos las reservas existentes que coinciden en los días y bloques horarios
        var idsLaboratoriosSolapados = obtenerIdsEspaciosOcupados(fechaInicio,fechaFin,diasYBloques);

        // Filtramos los Laboratorios disponibles que no están en las reservas solapadas
        return listarLaboratorios().parallelStream()
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
     * @return List<Espacio> disponibles con esas condiciones en ese rango de fecha
     */
    public List<Espacio> listarEspaciosDisponiblesConCondiciones(Integer capacidad, Boolean tieneProyector,
                                                              Boolean tieneTV, LocalDate fechaInicio,
                                                              LocalDate fechaFin,   Set<DiaBloque> diasYBloques) {
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
     */
    public List<Aula> listarAulasDisponiblesConCondiciones(Integer capacidad, Boolean tieneProyector, Boolean tieneTV,
                                                           LocalDate fechaInicio, LocalDate fechaFin,
                                                           Set<DiaBloque> diasYBloques) {
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
     */
    public List<Laboratorio> listarLaboratoriosDisponiblesConCondiciones(Integer computadoras, Integer capacidad,
                                                                         Boolean tieneProyector, Boolean tieneTV,
                                                                         LocalDate fechaInicio, LocalDate fechaFin,
                                                                         Set<DiaBloque> diasYBloques) {
        var laboratoriosDisponibles = listarLaboratoriosDisponibles(fechaInicio, fechaFin, diasYBloques);
        return filtrarEspacios(laboratoriosDisponibles, capacidad, tieneProyector, tieneTV, computadoras);
    }




    // Validaciones
    /**
     * Valída la existencia de un Espacio por ID
     * @param idEspacio del espacio que se quiere verificar
     * @return Espacio si existe
     * @throws NotFoundException Si no se encuentra el aula con ese ID*/
    private Espacio validarEspacioExistenteById(Integer idEspacio) throws NotFoundException {
        return espacioBaseRepository.findById(idEspacio)
                .orElseThrow(() -> new NotFoundException(String.format("No existe un aula con el id: %s", idEspacio)));
    }
}