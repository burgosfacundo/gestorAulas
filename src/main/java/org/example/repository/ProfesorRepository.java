package org.example.repository;

import org.example.model.Profesor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repositorio de profesor
 * Implementa JpaRepository
 * Su responsabilidad es interactuar con la base de datos
 */
@Repository
public interface ProfesorRepository extends JpaRepository<Profesor, Integer> {
  Optional<Profesor> findByMatricula(String matricula);
}
