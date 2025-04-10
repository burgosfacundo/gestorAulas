package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.enums.BloqueHorario;
import org.example.enums.EstadoSolicitud;
import org.example.exception.BadRequestException;
import org.example.exception.ConflictException;
import org.example.exception.NotFoundException;
import org.example.model.*;
import org.example.model.dto.ReservaDTO;
import org.example.repository.*;
import org.example.utils.Mapper;
import org.example.utils.Utils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Clase que se encarga de comunicarse con el repositorio
 * y aplicar la lógica de negocio para manipular inscripciones
 */
@Service
@RequiredArgsConstructor
public class ReservaService{
    private final EspacioService espacioService;
    private final ReservaRepository repositorio;
    private final EspacioBaseRepository<Espacio> espacioBaseRepository;
    private final InscripcionRepository inscripcionRepository;
    private final ProfesorRepository profesorRepository;
    private final AsignaturaRepository asignaturaRepository;
    private final SolicitudCambioAulaRepository solicitudRepository;
    private final ReservaRepository reservaRepository;


    /**
     * Lista todas las reservas
     * @return List<Reserva>
     */
    public List<Reserva> listar() {
       return repositorio.findAll();
    }

    /**
     * Guarda una reserva
     *
     * @param dto que queremos guardar
     * @throws BadRequestException sí existe un problema con los datos de reserva
     */
    public void guardar(ReservaDTO dto) throws BadRequestException, ConflictException, NotFoundException {
        var idAula = dto.idEspacio();
        var idInscripcion = dto.idInscripcion();

        //Validamos y obtenemos la inscripción de la reserva
        var inscripcion = validarInscripcionExistente(idInscripcion);

        //Validamos y obtenemos el aula de la reserva
        var aula = validarAulaExistenteById(idAula);

        var reserva = Mapper.toReserva(dto);

        // Validamos si el aula está disponible en el rango y bloque horario especificado
        validarDisponibilidadAula(reserva);

        // Validamos la capacidad del aula y la cantidad de alumnos en la inscripción
        Utils.validarCapacidadEspacio(aula,inscripcion);

        // Validación de que el aula sea un laboratorio si la asignatura lo requiere
        validarRequiereLaboratorio(aula,inscripcion.getAsignatura().getId());

        // Validamos si existen solicitudes de cambio pendientes que generen conflicto con la reserva
        validarSolicitudesPendientes(reserva);

        // Guardamos la reserva si pasa todas las validaciones
        repositorio.save(reserva);
    }


    /**
     * Elimina una reserva por ID
     * @param id de la reserva que queremos eliminar
     * @throws NotFoundException si no se encuentra una reserva con ese ID
     */
    public void eliminar(Integer id) throws NotFoundException {
        try{
            repositorio.deleteById(id);
        }catch (EmptyResultDataAccessException e){
            throw new NotFoundException("No existe la reserva");
        }
    }

    /**
     * Obtiene una reserva por ID
     * @param id de la reserva
     * @return Reserva con ese ID
     * @throws NotFoundException Si no se encuentra la reserva con ese ID
     */
    public Reserva obtener(Integer id) throws NotFoundException {
        return repositorio.findById(id)
                .orElseThrow(()-> new NotFoundException("No existe la reserva"));
    }

    /**
     * Modifica una reserva
     * @param dto que se va a modificar
     * @throws NotFoundException Si no encuentra reserva o aula o inscripción
     */
    public void modificar(ReservaDTO dto) throws NotFoundException, BadRequestException, ConflictException {
        if (!reservaRepository.existsById(dto.id())) {
            throw new BadRequestException("No se encontró la reserva");
        }
        //Validamos y obtenemos la inscripción de la reserva
        var inscripcion = validarInscripcionExistente(dto.idInscripcion());

        //Validamos y obtenemos el aula de la reserva
        var aula = validarAulaExistenteById(dto.idEspacio());

        var reserva = Mapper.toReserva(dto);

        // Validamos si el aula está disponible en el rango y bloque horario especificado
        validarDisponibilidadAula(reserva);

        // Validamos la capacidad del aula y la cantidad de alumnos en la inscripción
        Utils.validarCapacidadEspacio(aula,inscripcion);

        // Validación de que el aula sea un laboratorio si la asignatura lo requiere
        validarRequiereLaboratorio(aula,inscripcion.getAsignatura().getId());

        // Validamos si existen solicitudes de cambio pendientes que generen conflicto con la reserva
        validarSolicitudesPendientes(reserva);

        // Modificamos la reserva si pasa todas las validaciones
        repositorio.save(reserva);
    }

    // Validaciones
    /**
     * Valída la existencia de un Espacio por ID
     * @param idEspacio del espacio que se quiere verificar
     * @return Espacio si existe
     * @throws NotFoundException Si no se encuentra el espacio con ese ID
     */
    private Espacio validarAulaExistenteById(Integer idEspacio) throws NotFoundException {
        return espacioBaseRepository.findById(idEspacio)
                .orElseThrow(() -> new NotFoundException("No existe el aula"));
    }

    /**
     * Valída la existencia de una Asignatura por ID
     * @param idAsignatura de la asignatura que se quiere verificar
     * @return Asignatura si existe
     * @throws NotFoundException Si no se encuentra la asignatura con ese ID*/
    private Asignatura validarAsignaturaExistente(Integer idAsignatura) throws NotFoundException {
        return asignaturaRepository.findById(idAsignatura)
                .orElseThrow(()-> new NotFoundException("No existe la asignatura"));
    }


    /**
     * Valida la existencia de una inscripción
     * @param idInscripcion de la inscripción que se quiere verificar
     * @return InscripcionDTO si existe
     * @throws NotFoundException Si no se encuentra la inscripción con ese ID
     */
    private Inscripcion validarInscripcionExistente(Integer idInscripcion) throws NotFoundException {
        return inscripcionRepository.findById(idInscripcion)
                .orElseThrow(()-> new NotFoundException("No existe la inscripción"));
    }


    /**
     * Valida la disponibilidad de un espacio
     * @param reserva la reserva que contiene el espacio y el período que se valida
     * @throws BadRequestException si no está disponible el aula en ese período
     */
    private void validarDisponibilidadAula(Reserva reserva) throws BadRequestException {
        var aulasDisponibles = espacioService.listarEspaciosDisponibles(reserva.getFechaInicio(), reserva.getFechaFin()
                , reserva.getDiasYBloques());
        if (!aulasDisponibles.contains(reserva.getEspacio())) {
            throw new BadRequestException(String.format("El aula %d no está disponible.", reserva.getEspacio().getId()));
        }
    }

    /**
     * Valída las solicitudes pendientes
     * @param reserva que contiene la información de la reserva
     * @throws ConflictException si existe alguna solicitud pendiente con los mismos parámetros que la reserva
     */
    private void validarSolicitudesPendientes(Reserva reserva) throws ConflictException {
        var set = reserva.getDiasYBloques();

        var listDia = set.stream()
                .map(DiaBloque::getDia)
                .map(DayOfWeek::toString)
                .toList();
        var listBloque = set.stream()
                .map(DiaBloque::getBloqueHorario)
                .map(BloqueHorario::toString)
                .toList();

        var solicitudesPendientes = solicitudRepository.find(
                reserva.getEspacio().getId(),
                reserva.getFechaInicio(),
                reserva.getFechaFin(),
                EstadoSolicitud.PENDIENTE,
                listDia,
                listBloque);

        if (!solicitudesPendientes.isEmpty()) {
            // Creamos un StringBuilder para armar el mensaje de conflicto
            StringBuilder mensajeConflicto = new StringBuilder();
            mensajeConflicto.append(String.format("Hay solicitudes pendientes en el Aula %d.%n", reserva.getEspacio().getNumero()));

            for (var solicitud : solicitudesPendientes) {

                mensajeConflicto.append("Solicitud ID: ").append(solicitud.getId())
                        .append(" - Días y bloques en conflicto:\n");

                // Agrupamos los DiaBloque por día para mostrarlos organizados
                Map<DayOfWeek, List<BloqueHorario>> diasAgrupados = set.stream()
                        .collect(Collectors.groupingBy(
                                DiaBloque::getDia,
                                Collectors.mapping(DiaBloque::getBloqueHorario, Collectors.toList())
                        ));

                // Iteramos sobre los días agrupados para mostrar los conflictos
                diasAgrupados.forEach((dia, bloques) -> {
                    String bloquesStr = bloques.stream()
                            .map(BloqueHorario::name)
                            .collect(Collectors.joining(", "));

                    mensajeConflicto.append("  Día: ").append(Utils.traducirDiasSemana(Set.of(dia)))
                            .append(" - Bloques: ").append(bloquesStr).append("\n");
                });
            }

            throw new ConflictException(mensajeConflicto.toString());
        }
    }

    private void validarRequiereLaboratorio(Espacio espacio,Integer idAsignatura) throws NotFoundException, BadRequestException {
        var asignatura = validarAsignaturaExistente(idAsignatura);
        if (asignatura.isRequiereLaboratorio() && !(espacio instanceof Laboratorio)){
            throw new BadRequestException(String.format("El aula %d no es un laboratorio, no sirve para %s", espacio.getNumero(), asignatura.getNombre()));
        }
    }

    //filtros

    /**
     * Lista las reservas de un profesor
     * @param idProfe del profesor
     * @return List<Reserva> las reservas que tiene el profesor
     * @throws NotFoundException si no existe el profesor con ese ID
     */
    public List<Reserva> listarReservasPorProfesor(int idProfe) throws NotFoundException {
        if (!profesorRepository.existsById(idProfe)) {
            throw new NotFoundException("El profesor no existe");
        }

        return repositorio.findByIdProfesor(idProfe);
    }
}
