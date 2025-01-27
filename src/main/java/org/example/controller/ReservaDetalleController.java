package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import lombok.extern.slf4j.Slf4j;
import org.example.enums.BloqueHorario;
import org.example.model.Reserva;
import org.example.model.SolicitudCambioAula;
import org.example.utils.TableUtils;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class ReservaDetalleController {
    @FXML
    private TableView<Reserva> tblReservas;
    @FXML
    private TableColumn<Reserva,Integer> colId;
    @FXML
    private TableColumn<Reserva, LocalDate> colFechaInicio;
    @FXML
    private TableColumn<Reserva,LocalDate> colFechaFin;
    @FXML
    private TableColumn<Reserva,String> colAula;
    @FXML
    private TableColumn<Reserva,String> colInscripcion;
    @FXML
    private TableColumn<SolicitudCambioAula, Map<DayOfWeek, Set<BloqueHorario>>> colDiaHorario;
    private Reserva reserva;


    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
        actualizarTabla();
    }

    private void actualizarTabla() {
        // Configurar la tabla si no se ha hecho antes
        if (tblReservas.getColumns().isEmpty()) {
            TableUtils.inicializarTablaReserva(colId,colFechaInicio,colFechaFin,colAula,colInscripcion,colDiaHorario);
        }

        ObservableList<Reserva> reservaObservableList = FXCollections.observableArrayList();
        reservaObservableList.add(reserva);
        tblReservas.setItems(reservaObservableList);
    }

    @FXML
    public void initialize() {
        TableUtils.inicializarTablaReserva(colId,colFechaInicio,colFechaFin,colAula,colInscripcion,colDiaHorario);
    }
}
