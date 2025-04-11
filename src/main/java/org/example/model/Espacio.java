package org.example.model;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor @NoArgsConstructor @Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity(name="espacios")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "espacio_type")
public abstract class Espacio {
    @EqualsAndHashCode.Include
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private int numero;

    @Column(nullable = false)
    private int capacidad;

    private boolean tieneProyector;
    private boolean tieneTV;

    public void actualizar(Espacio espacio) {
        this.numero = espacio.getNumero();
        this.capacidad = espacio.getCapacidad();
        this.tieneProyector = espacio.isTieneProyector();
        this.tieneTV = espacio.isTieneTV();
    }

    @Override
    public String toString() {
        return String.valueOf(this.numero);
    }
}
