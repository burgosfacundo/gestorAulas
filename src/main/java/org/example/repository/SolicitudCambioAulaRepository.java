package org.example.repository;

import org.example.enums.EstadoSolicitud;
import org.example.model.SolicitudCambioAula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


/**
 * Repositorio de solicitud de cambio de aula
 * Implementa JpaRepository
 * Su responsabilidad es interactuar con la base de datos
 */
@Repository
public interface SolicitudCambioAulaRepository extends JpaRepository<SolicitudCambioAula, Integer> {

    // Buscar solicitudes por estado con paginación
    List<SolicitudCambioAula> findByEstado(EstadoSolicitud estado);

    // Buscar solicitudes por estado y profesor con paginación
    List<SolicitudCambioAula> findByEstadoAndProfesor_Id(EstadoSolicitud estado, Integer profesorId);

    @Query(value = """
    SELECT DISTINCT s.* FROM solicitudes s
    JOIN solicitud_dia_bloque sd ON sd.solicitud_id = s.id
    JOIN dia_bloque db on db.id = sd.dia_bloque_id
    WHERE s.profesor_id = :idProfesor
    AND s.nuevo_espacio_id = :idEspacio
    AND s.reserva_original_id = :idReserva
    AND s.fecha_inicio = :fechaInicio
    AND s.fecha_fin = :fechaFin
    AND db.dia IN :dias
    AND db.bloque_horario IN :bloques
    """, nativeQuery = true)
    List<SolicitudCambioAula> findByCriteria(
            @Param("idProfesor") Integer idProfesor,
            @Param("idEspacio") Integer idEspacio,
            @Param("idReserva") Integer idReserva,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            @Param("dias") List<String> dias,
            @Param("bloques") List<String> bloques
    );

    @Query(value = """
    SELECT DISTINCT s.* FROM solicitudes s
    JOIN solicitud_dia_bloque sd ON sd.solicitud_id = s.id
    JOIN dia_bloque db on db.id = sd.dia_bloque_id
    WHERE s.nuevo_espacio_id = :idEspacio
    AND s.fecha_inicio = :fechaInicio
    AND s.fecha_fin = :fechaFin
    AND s.estado = :estadoSolicitud
    AND db.dia IN :dias
    AND db.bloque_horario IN :bloques
    """, nativeQuery = true)
    List<SolicitudCambioAula> find(@Param("idEspacio") Integer idEspacio,
                                   @Param("fechaInicio") LocalDate fechaInicio,
                                   @Param("fechaFin") LocalDate fechaFin,
                                   @Param("estadoSolicitud") EstadoSolicitud estadoSolicitud,
                                   @Param("dias") List<String> dias,
                                   @Param("bloques") List<String> bloques
    );
}
