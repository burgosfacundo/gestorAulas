package org.example.model;
import jakarta.persistence.*;
import lombok.*;


@Entity @Table(name = "usuarios")
@Getter @Setter @EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor @RequiredArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(nullable = false, unique = true)
    @EqualsAndHashCode.Include
    private String username;

    @Column(nullable = false)
    private String password;

    @ManyToOne
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    @OneToOne
    @JoinColumn(name = "profesor_id")
    private Profesor profesor;

    @Override
    public String toString() {
        return username;
    }
}