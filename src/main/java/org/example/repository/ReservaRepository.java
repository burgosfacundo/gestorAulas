package org.example.repository;


import org.example.model.DiaBloque;
import org.example.model.Espacio;
import org.example.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Repositorio de reservas
 * Implementa JpaRepository
 * Su responsabilidad es interactuar con la base de datos
 */
@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    @Query("SELECT r FROM Reserva r WHERE r.inscripcion.profesor.id= :idProfesor")
    List<Reserva> findByIdProfesor(@Param("idProfesor")int idProfesor);

    @Query("SELECT r.espacio.id FROM Reserva r WHERE " +
            "(r.fechaInicio <= :fechaFin AND r.fechaFin >= :fechaInicio) AND " +
            "EXISTS (SELECT 1 FROM r.diasYBloques db WHERE db IN :diasYBloques)")
    List<Integer> findEspaciosOcupadas(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            @Param("diasYBloques") Set<DiaBloque> diasYBloques);

    List<Reserva> findReservaByEspacio(Espacio espacio);

}
