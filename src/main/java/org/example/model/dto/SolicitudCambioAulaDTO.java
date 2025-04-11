package org.example.model.dto;


import org.example.enums.EstadoSolicitud;
import org.example.enums.TipoSolicitud;
import org.example.model.DiaBloque;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public record SolicitudCambioAulaDTO(Integer id, int idProfesor, int idReserva, int idEspacio, EstadoSolicitud estadoSolicitud,
                                     TipoSolicitud tipoSolicitud, LocalDate fechaInicio, LocalDate fechaFin,
                                     Set<DiaBloque> diasYBloques, String comentarioEstado,
                                     String comentarioProfesor, LocalDateTime fechaHoraSolicitud) {
}
