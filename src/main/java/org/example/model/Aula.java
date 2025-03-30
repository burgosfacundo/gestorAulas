package org.example.model;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Entity @DiscriminatorValue("Aula")
@Getter @Setter
public class Aula extends Espacio{
    public Aula(Integer i, int numero, int capacidad, boolean tieneProyector, boolean tieneTv) {
      super(i,numero,capacidad,tieneProyector,tieneTv);
    }

    public Aula() {

    }
}
