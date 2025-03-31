package org.example.repository;

import org.example.enums.BloqueHorario;
import org.example.model.DiaBloque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.Optional;


public interface DiaBloqueRepository extends JpaRepository<DiaBloque, Long> {
    Optional<DiaBloque> findByBloqueHorarioAndDia(BloqueHorario bloqueHorario, DayOfWeek dia);
}
