package org.example.model;
import lombok.*;


// Clase que representa la entidad Usuario
@Getter @Setter @EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor @RequiredArgsConstructor
public class Usuario {
    @EqualsAndHashCode.Include @NonNull
    private Integer id;
    @EqualsAndHashCode.Include
    private String username;
    private String password;
    private Rol rol;
    private Profesor profesor;

    @Override
    public String toString() {
        return username;
    }
}
