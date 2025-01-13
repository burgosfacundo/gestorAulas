package org.example.utils;

import org.example.model.*;
import org.example.model.dto.InscripcionDTO;
import org.example.model.dto.ReservaDTO;
import org.example.model.dto.SolicitudCambioAulaDTO;
import org.example.model.dto.UsuarioDTO;

public class Mapper {

    //Usuario

    /**
     * Mapea un dto a Usuario
     * @param dto que queremos mapear
     * @return Usuario mapeado desde dto
     */
    public static Usuario toUsuario(UsuarioDTO dto, Rol rol, Profesor profesor){
        //Retornamos el usuario mapeado desde DTO, incluyendo su rol y el profesor que representa
        return new Usuario(
                dto.id(),
                dto.username(),
                dto.password(),
                rol,
                profesor
        );
    }

    /**
     * Mapea un Usuario a dto
     * @param usuario que queremos mapear
     * @return UsuarioDTO mapeado desde Usuario
     */
    public static UsuarioDTO usuarioToDto(Usuario usuario) {
        //Retornamos el dto mapeado desde Usuario
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.getRol().getId(),
                usuario.getProfesor().getId()
        );
    }

    //Inscripción

    /**
     * Mapea un dto a Inscripción
     * @param dto que queremos mapear
     * @return Inscripción mapeado desde dto
     */
    public static Inscripcion toInscripcion(InscripcionDTO dto, Asignatura asignatura, Profesor profesor){
        //Retornamos la inscripción mapeando desde DTO, incluyendo su asignatura y profesor
        return new Inscripcion(
                dto.id(),
                dto.cantidadAlumnos(),
                dto.margenAlumnos(),
                dto.fechaFinInscripcion(),
                asignatura,
                dto.comision(),
                profesor
        );
    }

    /**
     * Mapea una Inscripción a dto
     * @param inscripcion que queremos mapear
     * @return InscripciónDTO mapeado desde Inscripción
     */
    public static InscripcionDTO inscripcionToDTO(Inscripcion inscripcion) {
        //Retornamos el DTO mapeado desde InscripciÓn
        return new InscripcionDTO(
                inscripcion.getId(),
                inscripcion.getCantidadAlumnos(),
                inscripcion.getMargenAlumnos(),
                inscripcion.getFechaFinInscripcion(),
                inscripcion.getAsignatura().getId(),
                inscripcion.getComision(),
                inscripcion.getProfesor().getId()
        );
    }


    //Reserva

    /**
     * Mapea un dto a Inscripción
     * @param dto que queremos mapear
     * @return Inscripción mapeado desde dto
     */
    public static Reserva toReserva(ReservaDTO dto, Aula aula, Inscripcion inscripcion){
        //Retornamos la inscripción mapeando desde DTO, incluyendo su asignatura y profesor
        return new Reserva(
                dto.id(),
                dto.fechaInicio(),
                dto.fechaFin(),
                aula,
                inscripcion,
                dto.diasYBloques()
        );
    }

    /**
     * Mapea una Reserva a dto
     * @param reserva que queremos mapear
     * @return ReservaDTO mapeado desde Reserva
     */
    public static ReservaDTO reservaToDTO(Reserva reserva) {
        //Retornamos el DTO mapeado desde Reserva
        return new ReservaDTO(
                reserva.getId(),
                reserva.getFechaInicio(),
                reserva.getFechaFin(),
                reserva.getAula().getId(),
                reserva.getInscripcion().getId(),
                reserva.getDiasYBloques()
        );
    }



    //Solicitud

    /**
     * Mapea un DTO a SolicitudCambioAula
     * @param dto que queremos mapear
     * @return SolicitudCambioAula mapeado desde DTO
     */
    public static SolicitudCambioAula toSolicitud(SolicitudCambioAulaDTO dto, Profesor profesor, Aula aula,
                                            Reserva reserva){
        //Retornamos la solicitud mapeando desde DTO, incluyendo su aula, profesor y reserva original
        return new SolicitudCambioAula(
                dto.id(), profesor, reserva, aula, dto.estadoSolicitud(), dto.tipoSolicitud(),
                dto.fechaInicio(), dto.fechaFin(), dto.diasYBloques(),
                dto.comentarioEstado(), dto.comentarioProfesor(), dto.fechaHoraSolicitud()
        );
    }

    /**
     * Mapea una SolicitudCambioAula a DTO
     * @param solicitud que queremos mapear
     * @return SolicitudCambioAulaDTO mapeado desde SolicitudCambioAula
     */
    public static SolicitudCambioAulaDTO solicitudToDTO(SolicitudCambioAula solicitud) {
        //Retornamos el DTO mapeado desde SolicitudCambioAula
        return new SolicitudCambioAulaDTO(
                solicitud.getId(), solicitud.getProfesor().getId(), solicitud.getReservaOriginal().getId(),
                solicitud.getNuevaAula().getId(), solicitud.getEstado(), solicitud.getTipoSolicitud(),
                solicitud.getFechaInicio(), solicitud.getFechaFin(), solicitud.getDiasYBloques(),
                solicitud.getComentarioEstado(), solicitud.getComentarioProfesor(), solicitud.getFechaHoraSolicitud()
        );
    }
}

