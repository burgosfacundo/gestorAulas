package org.example.controller.model.reserva;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.enums.BloqueHorario;
import org.example.model.Reserva;
import org.example.utils.TableUtils;
import org.example.utils.VistaUtils;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservaVistaController {
    private final VistaUtils vistaUtils;
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
    private TableColumn<Reserva, Map<DayOfWeek, Set<BloqueHorario>>> colDiaHorario;
    private List<Reserva> reservas;

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
        actualizarTabla();
    }

    private void actualizarTabla() {
        // Configurar la tabla si no se ha hecho antes
        if (tblReservas.getColumns().isEmpty()) {
            TableUtils.inicializarTablaReserva(colId,colFechaInicio,colFechaFin,colAula,colInscripcion,colDiaHorario);
        }

        ObservableList<Reserva> reservaObservableList = FXCollections.observableArrayList();
        reservaObservableList.addAll(reservas);
        tblReservas.setItems(reservaObservableList);
    }

    @FXML
    public void initialize() {
        TableUtils.inicializarTablaReserva(colId,colFechaInicio,colFechaFin,colAula,colInscripcion,colDiaHorario);

        colDiaHorario.setCellFactory(column -> new TableCell<>() {
            private final Button btnVerHorarios;

            {
                btnVerHorarios = new Button("Ver");
                btnVerHorarios.setOnAction(event -> {
                    var reserva = getTableView().getItems().get(getIndex());
                    Optional
                            .ofNullable(reserva)
                            .ifPresent(r ->  vistaUtils.mostrarVistaHorarios(reserva.getDiasYBloques()));
                });
            }

            @Override
            protected void updateItem(Map<DayOfWeek, Set<BloqueHorario>> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setGraphic(btnVerHorarios);
                }
            }
        });

        tblReservas.setRowFactory(tableView -> {
            TableRow<Reserva> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    handleRowDoubleClick(row.getItem(), event);
                }
            });
            return row;
        });
    }

    private void handleRowDoubleClick(Reserva reserva, MouseEvent ignoredEvent) {
        // Determinar la columna del click
        var clickedColumn = tblReservas.getFocusModel().getFocusedCell().getTableColumn();

        if (clickedColumn == colInscripcion) {
            vistaUtils.mostrarVistaInscripcion(reserva.getInscripcion());
        }else if(clickedColumn == colAula) {
            vistaUtils.mostrarVistaAula(reserva.getAula());
        }
    }

}
