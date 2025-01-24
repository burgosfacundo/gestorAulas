package org.example.model;


import lombok.*;


@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@RequiredArgsConstructor
public class Profesor {
    @EqualsAndHashCode.Include @NonNull
    private Integer id;
    private String nombre;
    private String apellido;
    @NonNull
    private String matricula;

    @Override
    public String toString() {
        return nombre + " " + apellido;
    }
}
