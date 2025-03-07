package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.enums.EstadoSolicitud;
import org.example.enums.TipoSolicitud;
import org.example.exception.BadRequestException;
import org.example.exception.ConflictException;
import org.example.exception.JsonNotFoundException;
import org.example.exception.NotFoundException;
import org.example.model.*;
import org.example.model.dto.SolicitudCambioAulaDTO;
import org.example.repository.AulaRepository;
import org.example.repository.ProfesorRepository;
import org.example.repository.SolicitudCambioAulaRepository;
import org.example.utils.Mapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SolicitudCambioAulaService{
    private final SolicitudCambioAulaRepository repositorio;
    private final AulaRepository aulaRepository;
    private final AulaService aulaService;
    private final ReservaService reservaService;
    private final ProfesorRepository profesorRepository;


    /**
     * Lista todas las solicitudes
     * @return List<SolicitudCambioAula>
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    public List<SolicitudCambioAula> listar() throws JsonNotFoundException, NotFoundException {
        List<SolicitudCambioAula> solicitudes = new ArrayList<>();
        var dtoList = repositorio.getAll();
        for (SolicitudCambioAulaDTO dto : dtoList){
            // Validamos que el aula exista
            var aula = validarAulaExistente(dto.idAula());
            // Validamos que el profesor exista
            var profesor = validarProfesorExistente(dto.idProfesor());
            // Validamos que la reserva exista
            var reserva = validarReservaExistente(dto.idReserva());

            // Mapeamos y guardamos en solicitudes
            solicitudes.add(Mapper.toSolicitud(dto,profesor,aula,reserva));
        }
        return solicitudes;
    }

    /**
     * Guarda una solicitud
     *
     * @param solicitud que queremos guardar
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws BadRequestException   si existe una solicitud, o no se encuentra las clases que contiene
     */
    public void guardar(SolicitudCambioAula solicitud) throws JsonNotFoundException, NotFoundException, BadRequestException {
        var idAula = solicitud.getNuevaAula().getId();
        var idProfesor = solicitud.getProfesor().getId();
        var idReserva = solicitud.getReservaOriginal().getId();

        // Validamos que no exista una solicitud con esas características
        var solicitudExistente = repositorio.find(idProfesor, idAula, idReserva, solicitud.getFechaInicio(),
                solicitud.getFechaFin(), solicitud.getDiasYBloques());
        if (solicitudExistente.isPresent()) {
            throw new BadRequestException("Ya existe una solicitud con esas características");
        }

        validarAulaExistente(idAula);
        validarDisponibilidadAula(solicitud);
        validarCapacidadAula(solicitud.getNuevaAula(),solicitud.getReservaOriginal().getInscripcion());
        validarProfesorExistente(idProfesor);
        validarReservaExistente(idReserva);

        repositorio.save(Mapper.solicitudToDTO(solicitud));
    }



    /**
     * Elimina una solicitud por ID
     * @param id de la solicitud que queremos eliminar
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws NotFoundException si no se encuentra una solicitud con ese ID
     */
    public void eliminar(Integer id) throws JsonNotFoundException, NotFoundException {
        // Validamos que existe una solicitud con ese ID, si no lanzamos excepción
        validarSolicitudExistente(id);
        repositorio.deleteById(id);
    }


    /**
     * Obtiene una solicitud por ID
     * @param id de la solicitud
     * @return SolicitudCambioAula con ese ID
     * @throws JsonNotFoundException Sí ocurre un error con el archivo JSON
     * @throws NotFoundException Si no se encuentra la solicitud con ese ID
     */
    public SolicitudCambioAula obtener(Integer id) throws JsonNotFoundException, NotFoundException {
        // Validamos que existe una solicitud con ese ID
        var dto = validarSolicitudExistente(id);

        // Validamos que existe el aula, si no lanzamos excepción
        var aula = validarAulaExistente(dto.idAula());

        // Validamos que existe el profesor, si no lanzamos excepción
        var profesor = validarProfesorExistente(dto.idProfesor());

        // Validamos que existe, si no lanzamos excepción
        var reserva = validarReservaExistente(dto.idReserva());

        //Devuelvo la solicitud, luego de mapearla de DTO a SolicitudCambioAula
        return Mapper.toSolicitud(dto,profesor,aula,reserva);
    }


    /**
     * Modifica una solicitud
     * @param solicitud que se va a modificar
     * @throws JsonNotFoundException si ocurre un error con el archivo JSON
     * @throws NotFoundException Si no encuentra solicitud o las clases que contiene
     */
    public void modificar(SolicitudCambioAula solicitud) throws JsonNotFoundException, NotFoundException, BadRequestException {
        // Validamos y obtenemos la solicitud por ID
        var dto = validarSolicitudExistente(solicitud.getId());

        // Validamos que existe el aula, si no lanzamos excepción
        validarAulaExistente(dto.idAula());

        // Validamos que el aula esté disponible
        validarDisponibilidadAula(solicitud);

        // Validamos que existe el profesor, si no lanzamos excepción
        validarProfesorExistente(dto.idProfesor());

        // Validamos que existe la reserva, si no lanzamos excepción
        reservaService.obtener(dto.idReserva());

        repositorio.modify(Mapper.solicitudToDTO(solicitud));
    }

    /**
     * Aprueba una solicitud pendiente
     * @param id de la solicitud a aprobar
     * @throws NotFoundException si no encuentra la solicitud o reserva
     * @throws JsonNotFoundException si ocurre un problema con el archivo JSON
     * @throws BadRequestException si la solicitud no esta pendiente
     * @throws ConflictException si ocurre un problema de conflictos al guardar la nueva reserva
     */
    public void aprobarSolicitud(Integer id,String comentario) throws NotFoundException, JsonNotFoundException, BadRequestException, ConflictException {
        // Validamos que existe una solicitud con ese ID
        var dto = validarSolicitudExistente(id);

        //Validamos que la solicitud esté pendiente
        if (!dto.estadoSolicitud().equals(EstadoSolicitud.PENDIENTE)){
            throw new BadRequestException(String.format("La solicitud %d no esta pendiente", id));
        }

        var reserva = reservaService.obtener(dto.idReserva());
        var aula = aulaService.obtener(dto.idAula());

        //Creamos un DTO con la solicitud aprobada
        SolicitudCambioAulaDTO nuevoDTO = new SolicitudCambioAulaDTO(dto.id(),dto.idProfesor(),dto.idReserva(),
                dto.idAula(),EstadoSolicitud.APROBADA, dto.tipoSolicitud(),dto.fechaInicio(),dto.fechaFin(),
                dto.diasYBloques(),(comentario.isBlank()) ? "Aprobada" : comentario,
                dto.comentarioProfesor(), dto.fechaHoraSolicitud());

        //La modificamos
        repositorio.modify(nuevoDTO);

        // Si la solicitud es temporal creamos una nueva reserva
        if (dto.tipoSolicitud().equals(TipoSolicitud.TEMPORAL)){
            reservaService.guardar(new Reserva(0,dto.fechaInicio(),dto.fechaFin(),aula,
                    reserva.getInscripcion(),dto.diasYBloques()));

            //Si no la reserva es Permanente y modificamos la original
        }else if(dto.tipoSolicitud().equals(TipoSolicitud.PERMANENTE)){
            reservaService.modificar(new Reserva(reserva.getId(),dto.fechaInicio(),dto.fechaFin(),aula,
                    reserva.getInscripcion(),dto.diasYBloques()));
        }
    }

    /**
     * Rechaza una solicitud pendiente
     * @param id de la solicitud a rechazar
     * @throws NotFoundException si no encuentra la solicitud o reserva
     * @throws JsonNotFoundException si ocurre un problema con el archivo JSON
     * @throws BadRequestException si la solicitud no esta pendiente
     */
    public void rechazarSolicitud(Integer id,String comentario) throws NotFoundException, JsonNotFoundException, BadRequestException {
        // Validamos que existe una solicitud con ese ID
        var dto = validarSolicitudExistente(id);

        //Validamos que la solicitud esté pendiente
        if (!dto.estadoSolicitud().equals(EstadoSolicitud.PENDIENTE)) {
            throw new BadRequestException(String.format("La solicitud %d no esta pendiente", id));
        }

        SolicitudCambioAulaDTO nuevoDTO = new SolicitudCambioAulaDTO(dto.id(),dto.idProfesor(),dto.idReserva(),
                dto.idAula(),EstadoSolicitud.RECHAZADA, dto.tipoSolicitud(),dto.fechaInicio(),dto.fechaFin(),
                dto.diasYBloques(),(comentario.isBlank()) ? "Rechazada" : comentario
                ,dto.comentarioProfesor(), dto.fechaHoraSolicitud());

        //La modificamos
        repositorio.modify(nuevoDTO);
    }




    // Validaciones
    /**
     * Valida la existencia de una solicitud
     * @param id de la solicitud que se quiere verificar
     * @return SolicitudCambioAulaDTO si existe
     * @throws NotFoundException Si no se encuentra la solicitud con ese ID
     * @throws JsonNotFoundException Sí ocurre un error con el archivo JSON
     */
    private SolicitudCambioAulaDTO validarSolicitudExistente(Integer id) throws NotFoundException, JsonNotFoundException {
        return repositorio.find(id)
                .orElseThrow(() -> new NotFoundException(String.format("No existe una solicitud con el id: %d", id)));
    }

    /**
     * Valida la existencia de un Aula
     * @param idAula del aula que se quiere verificar
     * @return Aula si existe
     * @throws NotFoundException Si no se encuentra el aula con ese ID
     * @throws JsonNotFoundException Sí ocurre un error con el archivo JSON
     */
    private Aula validarAulaExistente(Integer idAula) throws NotFoundException, JsonNotFoundException {
        return aulaRepository.find(idAula)
                .orElseThrow(() -> new NotFoundException(String.format("No existe un aula con el id: %d", idAula)));
    }

    /**
     * Método para validar la existencia de un Profesor
     * @param idProfesor del profesor que se quiere verificar
     * @return Profesor si existe
     * @throws NotFoundException Si no se encuentra el profesor con ese ID
     * @throws JsonNotFoundException Sí ocurre un error con el archivo JSON
     */
    private Profesor validarProfesorExistente(Integer idProfesor) throws NotFoundException, JsonNotFoundException {
        return profesorRepository.find(idProfesor)
                .orElseThrow(() -> new NotFoundException(String.format("No existe un profesor con el id: %d", idProfesor)));
    }


    /**
     * Valida la existencia de una Reserva
     * @param idReserva de la reserva que se quiere verificar
     * @return Reserva si existe
     * @throws NotFoundException Si no se encuentra la reserva con ese ID
     * @throws JsonNotFoundException Sí ocurre un error con el archivo JSON
     */
    private Reserva validarReservaExistente(Integer idReserva) throws NotFoundException, JsonNotFoundException{
        return reservaService.obtener(idReserva);
    }

    /**
     * Valida la disponibilidad de un aula
     * @param solicitud la solicitud que contiene el aula y el período que se valida
     * @throws BadRequestException si no está disponible el aula en ese período
     * @throws JsonNotFoundException sí ocurre un problema con el archivo JSON de aulas
     */
    private void validarDisponibilidadAula(SolicitudCambioAula solicitud) throws JsonNotFoundException, BadRequestException {
        //Traemos todas las aulas disponibles en ese período
        var aulasDisponibles = aulaService.listarEspaciosDisponibles(solicitud.getFechaInicio(),solicitud.getFechaFin(),solicitud.getDiasYBloques());
        //Si no está dentro de las disponibles lanzamos excepción
        if (!aulasDisponibles.contains(solicitud.getNuevaAula())) {
            throw new BadRequestException(String.format("El aula %d no está disponible.", solicitud.getNuevaAula().getId()));
        }
    }

    /**
     * Valida la capacidad de un aula con respecto a la cantidad de alumnos de una Inscripción
     * @param aula que se quiere validar
     * @param inscripcion que se quiere validar
     * @throws BadRequestException si no alcanza la capacidad del aula para la cantidad de alumnos de la inscripción
     */
    private void validarCapacidadAula(Aula aula, Inscripcion inscripcion) throws BadRequestException {
        int alumnosRequeridos = inscripcion.getCantidadAlumnos() +
                (inscripcion.getFechaFinInscripcion().isAfter(LocalDate.now()) ? inscripcion.getMargenAlumnos() : 0);
        if (aula.getCapacidad() < alumnosRequeridos) {
            throw new BadRequestException(String.format("El aula %d tiene capacidad para %d alumnos, pero se requieren %d.", aula.getId(), aula.getCapacidad(), alumnosRequeridos));
        }
    }

    //Filtros

    /**
     * Lista solicitudes por estado
     * @param estado de las solicitudes a listar
     * @return List<SolicitudCambioAula> con ese estado
     * @throws JsonNotFoundException si ocurre un problema con el archivo JSON
     * @throws NotFoundException si no encuentra los datos internos de solicitud
     */
    public List<SolicitudCambioAula> listarSolicitudesPorEstado(EstadoSolicitud estado)
            throws JsonNotFoundException, NotFoundException {
        return listar().stream()
                .filter(s -> s.getEstado().equals(estado))
                .toList();
    }


    /**
     * Lista solicitudes por estado y profesor
     * @param estado de las solicitudes a listar
     * @param idProfesor del profesor a filtrar
     * @return List<SolicitudCambioAula> de ese profesor y con ese estado
     * @throws JsonNotFoundException si ocurre un problema con el archivo JSON
     * @throws NotFoundException si no encuentra al profesor con ese ID
     */
    public List<SolicitudCambioAula> listarSolicitudesPorEstadoYProfesor(EstadoSolicitud estado, Integer idProfesor)
            throws JsonNotFoundException, NotFoundException {

        var optionalProfesor = profesorRepository.find(idProfesor);

        if(optionalProfesor.isEmpty()){
            throw new NotFoundException(String.format("El profesor con el id: %d no existe", idProfesor));
        }

        return listar().stream()
                .filter(s -> s.getEstado().equals(estado))
                .filter(s -> s.getProfesor().getId().equals(idProfesor))
                .toList();
    }
}
