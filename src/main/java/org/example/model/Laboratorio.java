package org.example.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Entity @DiscriminatorValue("Laboratorio")
@Getter @Setter
@NoArgsConstructor
public class Laboratorio extends Espacio {
    private int computadoras;

    public Laboratorio(Integer id, int numero, int capacidad, boolean tieneProyector, boolean tieneTV,int computadoras) {
        super(id, numero, capacidad, tieneProyector, tieneTV);
        this.computadoras = computadoras;
    }

    @Override
    public void actualizar(Espacio espacio) {
        super.actualizar(espacio);

        if (espacio instanceof Laboratorio laboratorio) {
            this.computadoras = laboratorio.getComputadoras();
        }
    }
}
