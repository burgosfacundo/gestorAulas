package org.example.model;


import jakarta.persistence.*;
import lombok.*;


@Entity @Table(name = "profesores")
@Getter @Setter @EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor @RequiredArgsConstructor @NoArgsConstructor
public class Profesor {
    @EqualsAndHashCode.Include @NonNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(nullable = false, length = 50)
    private String apellido;

    @Column(nullable = false, unique = true, length = 20)
    private String matricula;

    @Override
    public String toString() {
        return nombre + " " + apellido;
    }
}
