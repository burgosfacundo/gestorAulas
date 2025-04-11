package org.example.model.dto;


import org.example.model.DiaBloque;

import java.time.LocalDate;
import java.util.Set;

public record ReservaDTO (Integer id, LocalDate fechaInicio, LocalDate fechaFin, int idEspacio,
                          int idInscripcion,  Set<DiaBloque> diasYBloques) {
}
