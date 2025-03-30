package org.example.model;


import jakarta.persistence.*;
import lombok.*;


@Entity @Table(name = "asignaturas")
@Getter @Setter @EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor @NoArgsConstructor
public class Asignatura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true)
    private Integer codigo;

    private boolean requiereLaboratorio;
    @Override
    public String toString() {
        return nombre;
    }
}

