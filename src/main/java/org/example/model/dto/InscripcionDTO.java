package org.example.model.dto;

import java.time.LocalDate;

public record InscripcionDTO(Integer id, int cantidadAlumnos, int margenAlumnos, LocalDate fechaFinInscripcion,
                             int idAsignatura, int comision, int idProfesor) {
}
