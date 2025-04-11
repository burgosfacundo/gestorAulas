package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.enums.BloqueHorario;
import org.example.exception.NotFoundException;
import org.example.model.DiaBloque;
import org.example.repository.DiaBloqueRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;

@Service
@RequiredArgsConstructor
public class DiaBloqueService {
    private final DiaBloqueRepository diaBloqueRepository;

    /**
     * Busca un diaBloque por Dia y bloqueHorario
     * @param bloqueHorario bloqueHorario
     * @param dia dia
     * @return Optional<DiaBloque>
     */
    public DiaBloque buscarPorDiaYBloque(BloqueHorario bloqueHorario, DayOfWeek dia) throws NotFoundException {
        return diaBloqueRepository.findByBloqueHorarioAndDia(bloqueHorario, dia)
                .orElseThrow(() -> new NotFoundException("No se encontró el día y bloque horario"));
    }
}
