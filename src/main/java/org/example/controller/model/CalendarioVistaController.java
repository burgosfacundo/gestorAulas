package org.example.controller.model;

import javafx.fxml.FXML;
import jfxtras.scene.control.agenda.Agenda;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.enums.BloqueHorario;
import org.example.model.DiaBloque;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Objects;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class CalendarioVistaController {
    @FXML
    private Agenda agenda;

    @FXML
    public void initialize() {
        agenda.setDisplayedLocalDateTime(LocalDateTime.now()); // Muestra la fecha actual
        agenda.setAllowDragging(false); // ❌ No permitir arrastrar eventos
        agenda.setAllowResize(false);   // ❌ No permitir cambiar tamaño
        agenda.setEditAppointmentCallback(param -> null); // ❌ No permitir editar con doble clic

        // Cargar el archivo CSS
        String css = Objects.requireNonNull(getClass().getResource("/org/example/styles/calendar.css")).toExternalForm();
        agenda.getStylesheets().add(css);
    }

    public void cargarHorarios(Set<DiaBloque> diasBloques) {
        agenda.appointments().clear();

        LocalDate semanaBase = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        for (DiaBloque diaBloque : diasBloques) {
            DayOfWeek dia = diaBloque.getDia(); // Obtener el día
            BloqueHorario bh = diaBloque.getBloqueHorario(); // Obtener el bloque horario

            LocalDate fechaReferencia = semanaBase.with(dia);
            LocalDateTime inicio = fechaReferencia.atTime(bh.getInicio());
            LocalDateTime fin = fechaReferencia.atTime(bh.getFin());

            Agenda.Appointment appointment = new Agenda.AppointmentImplLocal()
                    .withStartLocalDateTime(inicio)
                    .withEndLocalDateTime(fin)
                    .withDescription("Reserva programada")
                    .withAppointmentGroup(new Agenda.AppointmentGroupImpl().withStyleClass("reservado"));

            agenda.appointments().add(appointment);
        }
    }
}
