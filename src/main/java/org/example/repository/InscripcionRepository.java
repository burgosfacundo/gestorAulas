package org.example.repository;

import org.example.model.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio de inscripciones
 * Implementa JpaRepository
 * Su responsabilidad es interactuar con la base de datos
 */
@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Integer> {
    @Query("SELECT i FROM Inscripcion i WHERE i.asignatura.id = :idAsignatura AND i.profesor.id = :idProfesor AND i.comision = :comision")
    Optional<Inscripcion> find(@Param("idAsignatura") int idAsignatura, @Param("idProfesor") int idProfesor, @Param("comision") Integer comision);
}
