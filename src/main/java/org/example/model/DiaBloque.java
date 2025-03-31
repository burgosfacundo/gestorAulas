package org.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.enums.BloqueHorario;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;

@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class DiaBloque {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia", nullable = false)
    private DayOfWeek dia;

    @Enumerated(EnumType.STRING)
    @Column(name = "bloque_horario", nullable = false)
    private BloqueHorario bloqueHorario;

    @ManyToMany(mappedBy = "diasYBloques")
    List<Reserva> reservas;

    @ManyToMany(mappedBy = "diasYBloques")
    Set<SolicitudCambioAula> solicitudes;

    public DiaBloque(BloqueHorario bloqueHorario, DayOfWeek dia) {
        this.bloqueHorario = bloqueHorario;
        this.dia = dia;
    }

    @Override
    public String toString() {
        return bloqueHorario + " " + dia;
    }
}

