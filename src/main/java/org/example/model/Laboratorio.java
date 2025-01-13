package org.example.model;

import lombok.*;

@Getter
@Setter
public class Laboratorio extends Aula {
    private int computadoras;

    public Laboratorio(Integer id, int numero, int capacidad, boolean tieneProyector, boolean tieneTV,int computadoras) {
        super(id, numero, capacidad, tieneProyector, tieneTV);
        this.computadoras = computadoras;
    }

    @Override
    public void actualizar(Aula aula){
        super.actualizar(aula);

        //Verifico que sea una instancia de Laboratorio
        if (aula instanceof Laboratorio){
            //Modifico la cantidad de computadoras
            this.computadoras = ((Laboratorio) aula ).getComputadoras();
        }
    }
}
