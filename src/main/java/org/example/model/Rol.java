package org.example.model;


import lombok.*;
import org.example.enums.Permisos;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor @RequiredArgsConstructor
public class Rol {
    @EqualsAndHashCode.Include @NonNull
    private Integer id;
    @EqualsAndHashCode.Include
    private String nombre;
    private List<Permisos> permisos;

    public boolean tienePermiso(Permisos permiso) {
        return this.permisos.contains(permiso);
    }
}
