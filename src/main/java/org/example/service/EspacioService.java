package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.exception.BadRequestException;
import org.example.exception.NotFoundException;
import org.example.model.*;
import org.example.repository.AulaRepository;
import org.example.repository.EspacioBaseRepository;
import org.example.repository.LaboratorioRepository;
import org.example.repository.ReservaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;


/**
 * Clase que se encarga de comunicarse con el repositorio
 * y aplicar la lógica de negocio para manipular espacios
 */
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
     * @throws BadRequestException sí existe un espacio con ese código
     */
    public void guardar(Espacio espacio) throws BadRequestException {
        try {
            espacioBaseRepository.save(espacio);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException(String.format("Ya existe un aula o laboratorio con el número %s", espacio.getNumero()));
        }
    }

    /**
     * Elimina un espacio por ID
     * @param id del espacio que queremos eliminar
     * @throws NotFoundException si no se encuentra un espacio con ese número
     * @throws BadRequestException si el espacio tiene reservas no se puede eliminar
     */
    public void eliminar(Integer id) throws NotFoundException, BadRequestException {
        var espacio = espacioBaseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe el espacio"));

        if (!reservaRepository.findReservaByEspacio(espacio).isEmpty()) {
            throw new BadRequestException("No se puede eliminar el espacio porque tiene reservas");
        }

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
        return espacioBaseRepository.findById(id).orElseThrow(() ->
                new NotFoundException("No se encontró el espacio"));
    }

    /**
     * Modifica un espacio
     * @param espacio modificada
     * @throws BadRequestException si ocurriera un error con la información
     */
    public void modificar(Espacio espacio) throws BadRequestException {
        if (!espacioBaseRepository.existsById(espacio.getId())) {
            throw new BadRequestException("No se encontró el espacio");
        }

        try {
            espacioBaseRepository.save(espacio);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Ya existe un espacio con ese número");
        }
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
        return reservaRepository.findEspaciosOcupadas(fechaInicio,fechaFin,diasYBloques);
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
        return espacioBaseRepository.findAll().stream()
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
     */
    public List<Laboratorio> listarLaboratoriosDisponibles(LocalDate fechaInicio, LocalDate fechaFin,
                                                           Set<DiaBloque> diasYBloques) {
        // Filtramos las reservas existentes que coinciden en los días y bloques horarios
        var idsLaboratoriosSolapados = obtenerIdsEspaciosOcupados(fechaInicio,fechaFin,diasYBloques);

        // Filtramos los Laboratorios disponibles que no están en las reservas solapadas
        return listarLaboratorios().stream()
                .filter(laboratorio -> !idsLaboratoriosSolapados.contains(laboratorio.getId()))
                .toList();
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

}