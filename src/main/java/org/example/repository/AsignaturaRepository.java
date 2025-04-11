package org.example.repository;

import org.example.model.Asignatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio de asignatura
 * Implementa JpaRepository
 * Su responsabilidad es interactuar con la base de datos
 */
@Repository
public interface AsignaturaRepository extends JpaRepository<Asignatura, Integer> {
    Optional<Asignatura> findByCodigo(Integer codigo);
}
