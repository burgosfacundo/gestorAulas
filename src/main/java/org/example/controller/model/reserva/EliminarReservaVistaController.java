package org.example.controller.model.reserva;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.GlobalExceptionHandler;
import org.example.exception.NotFoundException;
import org.example.model.DiaBloque;
import org.example.model.Reserva;
import org.example.service.ReservaService;
import org.example.utils.TableUtils;
import org.example.utils.VistaUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
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
    private TableColumn<Reserva, LocalDate> colFechaInicio;
    @FXML
    private TableColumn<Reserva,LocalDate> colFechaFin;
    @FXML
    private TableColumn<Reserva,String> colAula;
    @FXML
    private TableColumn<Reserva,String> colInscripcion;
    @FXML
    private TableColumn<Reserva, Set<DiaBloque>> colDiaHorario;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnCancelar;
    @FXML
    private Pagination pagination;
    private static final int PAGE_SIZE = 10;

    @FXML
    public void initialize() {
        // Asocio columnas de la tabla con atributos del modelo
        TableUtils.inicializarTablaReserva(colFechaInicio,colFechaFin,colAula,colInscripcion,colDiaHorario);

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
            protected void updateItem(Set<DiaBloque> item, boolean empty) {
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

        // Cargar y paginar las reservas directamente
        var reservas = reservaService.listar();
        int totalPages = (int) Math.ceil((double) reservas.size() / PAGE_SIZE);
        pagination.setPageCount(Math.max(totalPages, 1));

        pagination.currentPageIndexProperty().addListener(
                (obs, oldIndex, newIndex) -> cargarPagina(reservas, newIndex.intValue()));
        cargarPagina(reservas, 0); // Cargar la primera página
    }

    private void cargarPagina(List<Reserva> reservas, int pageIndex) {
        int fromIndex = pageIndex * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, reservas.size());

        ObservableList<Reserva> reservaObservableList = FXCollections.observableArrayList(reservas.subList(fromIndex, toIndex));
        tblReservas.setItems(reservaObservableList);
    }

    private void handleRowDoubleClick(Reserva reserva, MouseEvent ignoredEvent) {
        // Determinar la columna del click
        var clickedColumn = tblReservas.getFocusModel().getFocusedCell().getTableColumn();

        if (clickedColumn == colInscripcion) {
            vistaUtils.mostrarVistaInscripcion(reserva.getInscripcion());
        }else if(clickedColumn == colAula) {
            vistaUtils.mostrarVistaEspacio(reserva.getEspacio());
        }
    }

    @FXML
    public void eliminar(ActionEvent actionEvent) {
        Reserva seleccionada = tblReservas.getSelectionModel().getSelectedItem();
        Optional
                .ofNullable(seleccionada)
                .ifPresent(usuario ->{
                    try {
                        var result = vistaUtils.mostrarAlerta("Estas seguro?", Alert.AlertType.CONFIRMATION);

                        if (result == ButtonType.OK) {
                            reservaService.eliminar(usuario.getId());
                            vistaUtils.mostrarAlerta("Reserva eliminada correctamente", Alert.AlertType.INFORMATION);
                            vistaUtils.cerrarVentana(btnEliminar);
                        }
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
