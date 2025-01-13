package org.example.model.dto;


import org.example.enums.BloqueHorario;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

public record ReservaDTO (Integer id, LocalDate fechaInicio, LocalDate fechaFin, int idAula,
                          int idInscripcion, Map<DayOfWeek, Set<BloqueHorario>> diasYBloques) {
}
