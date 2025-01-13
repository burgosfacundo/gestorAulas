package org.example.model;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@RequiredArgsConstructor
public class Aula {
    @EqualsAndHashCode.Include @NonNull
    private Integer id;
    private int numero;
    private int capacidad;
    private boolean tieneProyector;
    private boolean tieneTV;


    public void actualizar(Aula aula) {
        this.numero = aula.getNumero();
        this.capacidad = aula.getCapacidad();
        this.tieneProyector = aula.isTieneProyector();
        this.tieneTV = aula.isTieneTV();
    }
}
