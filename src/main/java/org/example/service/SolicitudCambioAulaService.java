package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.enums.BloqueHorario;
import org.example.enums.EstadoSolicitud;
import org.example.enums.TipoSolicitud;
import org.example.exception.BadRequestException;
import org.example.exception.ConflictException;
import org.example.exception.NotFoundException;
import org.example.model.*;
import org.example.model.dto.SolicitudCambioAulaDTO;
import org.example.repository.*;
import org.example.utils.Mapper;
import org.example.utils.Utils;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SolicitudCambioAulaService{
    private final SolicitudCambioAulaRepository repositorio;
    private final EspacioBaseRepository<Espacio> espacioBaseRepository;
    private final EspacioService espacioService;
    private final ReservaService reservaService;
    private final ProfesorRepository profesorRepository;
    private final ReservaRepository reservaRepository;


    /**
     * Lista todas las solicitudes
     * @return List<SolicitudCambioAula>*/
    public List<SolicitudCambioAula> listar() {
        return repositorio.findAll();
    }

    /**
     * Guarda una solicitud
     *
     * @param dto que queremos guardar
     * @throws BadRequestException  si existe una solicitud, o no se encuentra las clases que contiene
     */
    public void guardar(SolicitudCambioAulaDTO dto) throws NotFoundException, BadRequestException {
        var idAula = dto.idEspacio();
        var idProfesor = dto.idProfesor();
        var idReserva = dto.idReserva();
        var set = dto.diasYBloques();

        var listDia = set.stream()
                .map(DiaBloque::getDia)
                .map(DayOfWeek::toString)
                .toList();
        var listBloque = set.stream()
                .map(DiaBloque::getBloqueHorario)
                .map(BloqueHorario::toString)
                .toList();

        // Validamos que no exista una solicitud con esas características
        var solicitudExistente = repositorio.findByCriteria(idProfesor, idAula, idReserva, dto.fechaInicio(),
                dto.fechaFin(), listDia,listBloque);

        if (solicitudExistente.isEmpty()) {
            var aula =validarAulaExistente(idAula);
            validarDisponibilidadEspacio(dto);

            var reserva = reservaRepository.findById(idReserva)
                    .orElseThrow(()-> new NotFoundException("Reserva no encontrada"));

            Utils.validarCapacidadEspacio(aula,reserva.getInscripcion());
            validarProfesorExistente(idProfesor);
            validarReservaExistente(idReserva);

            repositorio.save(Mapper.toSolicitud(dto));
        }else {
            throw new BadRequestException("Ya existe una solicitud con esas características");
        }
    }



    /**
     * Elimina una solicitud por ID
     * @param id de la solicitud que queremos eliminar
     * @throws NotFoundException si no se encuentra una solicitud con ese ID
     */
    public void eliminar(Integer id) throws NotFoundException {
        // Validamos que existe una solicitud con ese ID, si no lanzamos excepción
        validarSolicitudExistente(id);
        repositorio.deleteById(id);
    }


    /**
     * Obtiene una solicitud por ID
     * @param id de la solicitud
     * @return SolicitudCambioAula con ese ID
     * @throws NotFoundException Si no se encuentra la solicitud con ese ID
     */
    public SolicitudCambioAula obtener(Integer id) throws NotFoundException {
        // Validamos que existe una solicitud con ese ID, si no lanzamos excepción
        return validarSolicitudExistente(id);
    }


    /**
     * Aprueba una solicitud pendiente
     * @param id de la solicitud a aprobar
     * @throws NotFoundException si no encuentra la solicitud o reserva
     * @throws BadRequestException si la solicitud no esta pendiente
     * @throws ConflictException si ocurre un problema de conflictos al guardar la nueva reserva
     */
    public void aprobarSolicitud(Integer id,String comentario) throws NotFoundException, BadRequestException, ConflictException {
        // Validamos que existe una solicitud con ese ID
        var solicitud = validarSolicitudExistente(id);

        //Validamos que la solicitud esté pendiente
        if (!solicitud.getEstado().equals(EstadoSolicitud.PENDIENTE)){
            throw new BadRequestException(String.format("La solicitud %d no esta pendiente", id));
        }

        var reserva = reservaService.obtener(solicitud.getReservaOriginal().getId());
        var aula = espacioService.obtener(solicitud.getNuevoEspacio().getId());

        SolicitudCambioAula solicitudCambioAula = new SolicitudCambioAula(solicitud.getId(),solicitud.getProfesor(),
                solicitud.getReservaOriginal(), aula,EstadoSolicitud.APROBADA, solicitud.getTipoSolicitud(),
                solicitud.getFechaInicio(),solicitud.getFechaFin(),
                solicitud.getDiasYBloques(),(comentario.isBlank()) ? "Aprobada" : comentario,
                solicitud.getComentarioProfesor(), solicitud.getFechaHoraSolicitud());

        //La modificamos
        repositorio.save(solicitudCambioAula);

        // Si la solicitud es temporal creamos una nueva reserva
        if (solicitud.getTipoSolicitud().equals(TipoSolicitud.TEMPORAL)){
            reservaService.guardar(Mapper.reservaToDTO(
                    new Reserva(null,solicitud.getFechaInicio(),solicitud.getFechaFin(),aula,
                            reserva.getInscripcion(),
                            reserva.getDiasYBloques()
                    )
            ));

            //Si no la reserva es Permanente y modificamos la original
        }else if(solicitud.getTipoSolicitud().equals(TipoSolicitud.PERMANENTE)){
            reservaService.modificar(Mapper.reservaToDTO(
                    new Reserva(reserva.getId(),solicitud.getFechaInicio(),
                            solicitud.getFechaFin(),aula, reserva.getInscripcion(), reserva.getDiasYBloques()
                    )
            ));
        }
    }

    /**
     * Rechaza una solicitud pendiente
     * @param id de la solicitud a rechazar
     * @throws NotFoundException si no encuentra la solicitud o reserva
     * @throws BadRequestException si la solicitud no esta pendiente
     */
    public void rechazarSolicitud(Integer id,String comentario) throws NotFoundException, BadRequestException {
        // Validamos que existe una solicitud con ese ID
        var solicitud = validarSolicitudExistente(id);

        //Validamos que la solicitud esté pendiente
        if (!solicitud.getEstado().equals(EstadoSolicitud.PENDIENTE)) {
            throw new BadRequestException(String.format("La solicitud %d no esta pendiente", id));
        }

        SolicitudCambioAula nuevaSolicitud = new SolicitudCambioAula(solicitud.getId(),solicitud.getProfesor()
                ,solicitud.getReservaOriginal(), solicitud.getNuevoEspacio(),
                EstadoSolicitud.RECHAZADA, solicitud.getTipoSolicitud(),solicitud.getFechaInicio(),solicitud.getFechaFin(),
               solicitud.getDiasYBloques(),(comentario.isBlank()) ? "Rechazada" : comentario
                ,solicitud.getComentarioProfesor(), solicitud.getFechaHoraSolicitud());

        //La modificamos
        repositorio.save(nuevaSolicitud);
    }




    // Validaciones
    /**
     * Valida la existencia de una solicitud
     * @param id de la solicitud que se quiere verificar
     * @return SolicitudCambioAula si existe
     * @throws NotFoundException Si no se encuentra la solicitud con ese ID
     */
    private SolicitudCambioAula validarSolicitudExistente(Integer id) throws NotFoundException {
        return repositorio.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("No existe una solicitud con el id: %d", id)));
    }

    /**
     * Valida la existencia de un Aula
     * @param idAula del aula que se quiere verificar
     * @return Aula si existe
     * @throws NotFoundException Si no se encuentra el aula con ese ID
     */
    private Espacio validarAulaExistente(Integer idAula) throws NotFoundException {
        return espacioBaseRepository.findById(idAula)
                .orElseThrow(() -> new NotFoundException(String.format("No existe un aula con el id: %d", idAula)));
    }

    /**
     * Método para validar la existencia de un Profesor
     *
     * @param idProfesor del profesor que se quiere verificar
     * @throws NotFoundException Si no se encuentra el profesor con ese ID
     */
    private void validarProfesorExistente(Integer idProfesor) throws NotFoundException {
        profesorRepository.findById(idProfesor)
                .orElseThrow(() -> new NotFoundException(String.format("No existe un profesor con el id: %d", idProfesor)));
    }


    /**
     * Valída la existencia de una Reserva
     *
     * @param idReserva de la reserva que se quiere verificar
     * @throws NotFoundException Si no se encuentra la reserva con ese ID
     */
    private void validarReservaExistente(Integer idReserva) throws NotFoundException{
        reservaService.obtener(idReserva);
    }

    /**
     * Valida la disponibilidad de un espacio
     * @param dto la solicitud que contiene el espacio y el período que se valida
     * @throws BadRequestException si no está disponible el espacio en ese período
     */
    private void validarDisponibilidadEspacio(SolicitudCambioAulaDTO dto) throws BadRequestException {
        //Traemos todas las aulas disponibles en ese período
        var aulasDisponibles = espacioService.listarEspaciosDisponibles(dto.fechaInicio(),dto.fechaFin(),
                dto.diasYBloques());
        //Si no está dentro de las disponibles lanzamos excepción
        aulasDisponibles.stream()
                .filter(a -> a.getId().equals(dto.idEspacio()))
                .findAny()
                .orElseThrow(()->
                        new BadRequestException(String.format("El aula %d no está disponible.", dto.idEspacio())));
    }


    //Filtros

    /**
     * Listar solicitudes por estado
     * @param estado de las solicitudes a listar
     * @return List<SolicitudCambioAula> con ese estado
     * @throws NotFoundException si no encuentra los datos internos de solicitud
     */
    public List<SolicitudCambioAula> listarSolicitudesPorEstado(EstadoSolicitud estado)
            throws NotFoundException {
        return repositorio.findByEstado(estado);
    }


    /**
     * Listar solicitudes por estado y profesor
     * @param estado de las solicitudes a listar
     * @param idProfesor del profesor a filtrar
     * @return List<SolicitudCambioAulaDTO> de ese profesor y con ese estado
     * @throws NotFoundException si no encuentra al profesor con ese ID
     */
    public List<SolicitudCambioAula> listarSolicitudesPorEstadoYProfesor(EstadoSolicitud estado, Integer idProfesor)
            throws NotFoundException {

        var optionalProfesor = profesorRepository.findById(idProfesor);

        if(optionalProfesor.isEmpty()){
            throw new NotFoundException(String.format("El profesor con el id: %d no existe", idProfesor));
        }

        return repositorio.findByEstadoAndProfesor_Id(estado,idProfesor);
    }
}
