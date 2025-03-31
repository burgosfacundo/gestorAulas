package org.example.controller.model.reserva;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.DiaBloque;
import org.example.model.Reserva;
import org.example.utils.TableUtils;
import org.example.utils.VistaUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservaVistaController {
    private final VistaUtils vistaUtils;
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
    private TableColumn<Reserva,  Set<DiaBloque>> colDiaHorario;
    @FXML
    private Pagination pagination;
    private List<Reserva> reservas;
    private static final int PAGE_SIZE = 10;

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
        int totalPages = (int) Math.ceil((double) reservas.size() / PAGE_SIZE);
        pagination.setPageCount(Math.max(totalPages, 1));

        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> cargarPagina(newIndex.intValue()));

        cargarPagina(0);
    }

    private void cargarPagina(int pageIndex) {
        int fromIndex = pageIndex * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, reservas.size());

        ObservableList<Reserva> reservaObservableList = FXCollections.observableArrayList();
        reservaObservableList.addAll(reservas.subList(fromIndex, toIndex));

        tblReservas.setItems(reservaObservableList);
    }

    @FXML
    public void initialize() {
        TableUtils.inicializarTablaReserva(colFechaInicio,colFechaFin,
                colAula,colInscripcion,colDiaHorario);

        colDiaHorario.setCellFactory(column -> new TableCell<>() {
            private final Button btnVerHorarios = new Button("Ver");

            {
                btnVerHorarios.setOnAction(event -> {
                    Reserva reserva = getTableRow().getItem();
                    if (reserva != null && reserva.getDiasYBloques() != null) {
                        vistaUtils.mostrarVistaHorarios(reserva.getDiasYBloques());
                    }
                });
            }

            @Override
            protected void updateItem(Set<DiaBloque> item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    setGraphic(btnVerHorarios);
                    setText(null);
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
            vistaUtils.mostrarVistaEspacio(reserva.getEspacio());
        }
    }

}
