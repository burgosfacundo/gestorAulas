package org.example.repository;

import org.example.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repositorio de usuario
 * Implementa JpaRepository
 * Su responsabilidad es interactuar con la base de datos
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
   Optional<Usuario> findByUsername(String username);
}

