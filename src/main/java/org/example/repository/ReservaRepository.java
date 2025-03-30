package org.example.repository;


import org.example.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de reservas
 * Implementa JpaRepository
 * Su responsabilidad es interactuar con la base de datos
 */
@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    @Query("SELECT r FROM Reserva r WHERE r.inscripcion.id= :idInscripcion")
    List<Reserva> findByIdInscripcion(@Param("idInscripcion")int idInscripcion);

    @Query("SELECT r FROM Reserva r WHERE r.inscripcion.profesor.id= :idProfesor")
    List<Reserva> findByIdProfesor(@Param("idProfesor")int idProfesor);
}
