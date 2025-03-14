package org.example.controller.model.reserva;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.enums.BloqueHorario;
import org.example.exception.GlobalExceptionHandler;
import org.example.exception.JsonNotFoundException;
import org.example.exception.NotFoundException;
import org.example.model.Reserva;
import org.example.service.ReservaService;
import org.example.utils.TableUtils;
import org.example.utils.VistaUtils;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class EliminarReservaVistaController {
    private final VistaUtils vistaUtils;
    private final ReservaService reservaService;
    private final GlobalExceptionHandler globalExceptionHandler;
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
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnCancelar;

    @FXML
    public void initialize() {
        // Asocio columnas de la tabla con atributos del modelo
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

        try {
            var reservas = reservaService.listar();
            ObservableList<Reserva> reservaObservableList = FXCollections.observableArrayList();
            reservaObservableList.addAll(reservas);
            tblReservas.setItems(reservaObservableList);
        }catch (JsonNotFoundException e){
            globalExceptionHandler.handleJsonNotFoundException(e);
        }catch (NotFoundException e){
            globalExceptionHandler.handleNotFoundException(e);
        }

        btnEliminar.disableProperty().bind(tblReservas.getSelectionModel().selectedItemProperty().isNull());
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

    @FXML
    public void eliminar(ActionEvent actionEvent) {
        Reserva seleccionada = tblReservas.getSelectionModel().getSelectedItem();
        Optional
                .ofNullable(seleccionada)
                .ifPresent(usuario ->{
                    try {
                        var result = vistaUtils.mostrarAlerta("Estas seguro?", Alert.AlertType.INFORMATION);

                        if (result == ButtonType.OK) {
                            reservaService.eliminar(usuario.getId());
                            vistaUtils.mostrarAlerta("Reserva eliminada correctamente", Alert.AlertType.INFORMATION);
                            vistaUtils.cerrarVentana(btnEliminar);
                        }
                    } catch (JsonNotFoundException e) {
                        globalExceptionHandler.handleJsonNotFoundException(e);
                    } catch (NotFoundException e) {
                        globalExceptionHandler.handleNotFoundException(e);
                    }
                });

    }

    @FXML
    public void cancelar(ActionEvent actionEvent) {
        vistaUtils.cerrarVentana(btnCancelar);
    }
}
