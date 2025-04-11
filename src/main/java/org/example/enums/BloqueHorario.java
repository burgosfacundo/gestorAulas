package org.example.enums;

import lombok.Getter;

import java.time.LocalTime;

@Getter
public enum BloqueHorario {

    MANIANA_PRIMER_BLOQUE(LocalTime.of(8,0),LocalTime.of(10,0),"Mañana 1er bloque"),
    MANIANA_SEGUNDO_BLOQUE(LocalTime.of(10,30),LocalTime.of(12,30),"Mañana 2do bloque"),
    TARDE_PRIMER_BLOQUE(LocalTime.of(13,0),LocalTime.of(15,0),"Tarde 1er bloque"),
    TARDE_SEGUNDO_BLOQUE(LocalTime.of(15,30),LocalTime.of(17,30),"Tarde 2do bloque"),
    NOCHE_PRIMER_BLOQUE(LocalTime.of(18,0),LocalTime.of(20,0),"Noche 1er bloque"),
    NOCHE_SEGUNDO_BLOQUE(LocalTime.of(20,30),LocalTime.of(22,30),"Noche 2do bloque");

    private final LocalTime inicio;
    private final LocalTime fin;
    private final String name;

    BloqueHorario(LocalTime inicio, LocalTime fin,String name) {
        this.inicio = inicio;
        this.fin = fin;
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("%s hs - %s hs",
                inicio,
                fin);
    }
}

