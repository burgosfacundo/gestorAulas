package org.example.repository;

import org.example.model.Espacio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio base de Espacios
 * Implementa JpaRepository
 * Su responsabilidad es interactuar con la base de datos
 */
@Repository
public interface EspacioBaseRepository<T extends Espacio> extends JpaRepository<T, Integer> {
    Optional<T> findByNumero(Integer numero);
}

