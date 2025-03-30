package org.example.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity @Table(name = "reservas")
@Getter @Setter @EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor @NoArgsConstructor
public class Reserva {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFin;

    @ManyToOne
    @JoinColumn(name = "espacio_id", nullable = false)
    private Espacio espacio;

    @ManyToOne
    @JoinColumn(name = "inscripcion_id", nullable = false)
    private Inscripcion inscripcion;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "reserva_dia_bloque",
            joinColumns = @JoinColumn(name = "reserva_id"),
            inverseJoinColumns = @JoinColumn(name = "dia_bloque_id")
    )
    private Set<DiaBloque> diasYBloques;


    public Set<DiaBloque> getDiasYBloques() {
        return diasYBloques == null ? new HashSet<>() : new HashSet<>(diasYBloques);
    }


    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
