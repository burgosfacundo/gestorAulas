package org.example.model;


import jakarta.persistence.*;
import lombok.*;
import org.example.enums.Permisos;
import java.util.List;

@Entity @Table(name = "roles")
@Getter @Setter @EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor @NoArgsConstructor
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(nullable = false, unique = true)
    @EqualsAndHashCode.Include
    private String nombre;

    @ElementCollection(targetClass = Permisos.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "rol_permisos", joinColumns = @JoinColumn(name = "rol_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "permiso")
    private List<Permisos> permisos;

    public boolean tienePermiso(Permisos permiso) {
        return this.permisos.contains(permiso);
    }

    @Override
    public String toString() {
        return nombre;
    }
}
