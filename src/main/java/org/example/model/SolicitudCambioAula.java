package org.example.model;


import lombok.*;
import org.example.enums.BloqueHorario;
import org.example.enums.EstadoSolicitud;
import org.example.enums.TipoSolicitud;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SolicitudCambioAula {
    @EqualsAndHashCode.Include
    private Integer id;
    @EqualsAndHashCode.Include
    private Profesor profesor;
    @EqualsAndHashCode.Include
    private Reserva reservaOriginal;
    @EqualsAndHashCode.Include
    private Aula nuevaAula;
    private EstadoSolicitud estado;
    private TipoSolicitud tipoSolicitud;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Map<DayOfWeek, Set<BloqueHorario>> diasYBloques;
    private String comentarioEstado;
    private String comentarioProfesor;
    private LocalDateTime fechaHoraSolicitud;


    public SolicitudCambioAula(Integer id, Profesor profesor, Reserva reservaOriginal, Aula nuevaAula,
                               TipoSolicitud tipoSolicitud, LocalDate fechaInicio, LocalDate fechaFin,
                               Map<DayOfWeek, Set<BloqueHorario>> diasYBloques,
                               String comentarioProfesor) {
        this.id = id;
        this.profesor = profesor;
        this.reservaOriginal = reservaOriginal;
        this.nuevaAula = nuevaAula;
        this.estado = EstadoSolicitud.PENDIENTE;
        this.tipoSolicitud = tipoSolicitud;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.diasYBloques = diasYBloques;
        this.comentarioProfesor = comentarioProfesor;
        this.fechaHoraSolicitud = LocalDateTime.now();
    }

    public SolicitudCambioAula(Integer id, Profesor profesor, Reserva reservaOriginal, Aula nuevaAula,
                               EstadoSolicitud estadoSolicitud, TipoSolicitud tipoSolicitud,
                               LocalDate fechaInicio, LocalDate fechaFin, Map<DayOfWeek, Set<BloqueHorario>> diasYBloques,
                               String comentarioEstado, String comentarioProfesor, LocalDateTime fechaHoraSolicitud) {
        this.id = id;
        this.profesor = profesor;
        this.reservaOriginal = reservaOriginal;
        this.nuevaAula = nuevaAula;
        this.estado = estadoSolicitud;
        this.tipoSolicitud = tipoSolicitud;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.diasYBloques = diasYBloques;
        this.comentarioEstado = comentarioEstado;
        this.comentarioProfesor = comentarioProfesor;
        this.fechaHoraSolicitud = fechaHoraSolicitud;
    }


    @Override
    public String toString() {
        return String.valueOf(id);
    }
}


