package org.example.model;


import lombok.*;
import org.example.enums.BloqueHorario;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@RequiredArgsConstructor
public class Reserva {
    @EqualsAndHashCode.Include @NonNull
    private Integer id;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    @NonNull
    private Aula aula;
    @NonNull
    private Inscripcion inscripcion;
    private Map<DayOfWeek, Set<BloqueHorario>> diasYBloques;

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
