package org.example.model;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor @RequiredArgsConstructor
public class Inscripcion {
    @EqualsAndHashCode.Include @NonNull
    private Integer id;
    private int cantidadAlumnos;
    private int margenAlumnos;
    private LocalDate fechaFinInscripcion;
    @EqualsAndHashCode.Include
    private Asignatura asignatura;
    @EqualsAndHashCode.Include
    private String comision;
    @EqualsAndHashCode.Include
    private Profesor profesor;
}
