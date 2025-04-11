package org.example.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity @Table(name = "inscripciones")
@Getter @Setter @EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor @NoArgsConstructor
public class Inscripcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private int cantidadAlumnos;

    @Column(nullable = false)
    private int margenAlumnos;

    @Column(nullable = false)
    private LocalDate fechaFinInscripcion;

    @ManyToOne
    @JoinColumn(name = "asignatura_id", nullable = false)
    private Asignatura asignatura;

    @Column(nullable = false)
    private Integer comision;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer cuatrimestre;

    @ManyToOne
    @JoinColumn(name = "profesor_id", nullable = false)
    private Profesor profesor;

    @Override
    public String toString() {
        return comision.toString() + " - " + asignatura.getNombre();
    }
}
