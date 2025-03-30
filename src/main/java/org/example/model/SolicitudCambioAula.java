package org.example.model;


import jakarta.persistence.*;
import lombok.*;
import org.example.enums.EstadoSolicitud;
import org.example.enums.TipoSolicitud;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity @Table(name = "solicitudes")
@Getter @Setter @EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class SolicitudCambioAula {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @EqualsAndHashCode.Include
    @JoinColumn(name = "profesor_id", nullable = false)
    private Profesor profesor;

    @ManyToOne @EqualsAndHashCode.Include
    @JoinColumn(name = "reserva_original_id", nullable = false)
    private Reserva reservaOriginal;

    @ManyToOne @EqualsAndHashCode.Include
    @JoinColumn(name = "nuevo_espacio_id", nullable = false)
    private Espacio nuevoEspacio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSolicitud estado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoSolicitud tipoSolicitud;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFin;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "solicitud_dia_bloque",
            joinColumns = @JoinColumn(name = "solicitud_id"),
            inverseJoinColumns = @JoinColumn(name = "dia_bloque_id")
    )
    private Set<DiaBloque> diasYBloques;

    private String comentarioEstado;
    private String comentarioProfesor;

    @Column(nullable = false)
    private LocalDateTime fechaHoraSolicitud;


    public SolicitudCambioAula(Integer id, Profesor profesor, Reserva reservaOriginal, Espacio nuevoEspacio,
                               TipoSolicitud tipoSolicitud, LocalDate fechaInicio, LocalDate fechaFin,
                               Set<DiaBloque> diasYBloques,
                               String comentarioProfesor) {
        this.id = id;
        this.profesor = profesor;
        this.reservaOriginal = reservaOriginal;
        this.nuevoEspacio = nuevoEspacio;
        this.estado = EstadoSolicitud.PENDIENTE;
        this.tipoSolicitud = tipoSolicitud;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.diasYBloques = diasYBloques;
        this.comentarioProfesor = comentarioProfesor;
        this.fechaHoraSolicitud = LocalDateTime.now();
    }

    public SolicitudCambioAula(Integer id, Profesor profesor, Reserva reservaOriginal, Espacio nuevoEspacio,
                               EstadoSolicitud estadoSolicitud, TipoSolicitud tipoSolicitud,
                               LocalDate fechaInicio, LocalDate fechaFin, Set<DiaBloque> diasYBloques,
                               String comentarioEstado, String comentarioProfesor, LocalDateTime fechaHoraSolicitud) {
        this.id = id;
        this.profesor = profesor;
        this.reservaOriginal = reservaOriginal;
        this.nuevoEspacio = nuevoEspacio;
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


