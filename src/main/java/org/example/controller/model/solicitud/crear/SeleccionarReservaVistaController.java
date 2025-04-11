package org.example.controller.model.solicitud.crear;

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
import org.example.security.SesionActual;
import org.example.service.ReservaService;
import org.example.utils.TableUtils;
import org.example.utils.VistaUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeleccionarReservaVistaController {
    private final ReservaService reservaService;
    private final VistaUtils vistaUtils;
    private final SesionActual sesionActual;
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
    private Button btnContinuar;
    @FXML
    private Button btnCancelar;
    @FXML
    private Pagination pagination;
    private List<Reserva> reservas;
    private static final int PAGE_SIZE = 10;

    @FXML
    public void initialize() {
        try {
            TableUtils.inicializarTablaReserva(colFechaInicio,colFechaFin,
                    colAula,colInscripcion,colDiaHorario);
            // Deshabilitar botón si no hay selección
            btnContinuar.disableProperty().bind(tblReservas.getSelectionModel().selectedItemProperty().isNull());

            tblReservas.setRowFactory(tableView -> {
                TableRow<Reserva> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && !row.isEmpty()) {
                        handleRowDoubleClick(row.getItem(), event);
                    }
                });
                return row;
            });

            colDiaHorario.setCellFactory(column -> new TableCell<>() {
                private final Button btnVerHorarios = new Button("Ver");

                {
                    btnVerHorarios.setOnAction(event -> {
                        Reserva reserva = getTableRow().getItem();
                        if (reserva != null && reserva.getDiasYBloques() != null) {
                            try {
                                vistaUtils.mostrarVistaHorarios(reserva.getDiasYBloques());
                            } catch (IOException e) {
                                globalExceptionHandler.handleIOException(e);
                            }
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

            reservas = reservaService.listarReservasPorProfesor(sesionActual.getUsuario().getProfesor().getId());
            int totalPages = (int) Math.ceil((double) reservas.size() / PAGE_SIZE);
            pagination.setPageCount(Math.max(totalPages, 1));

            pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> cargarPagina(newIndex.intValue()));

            cargarPagina(0);
        }catch (NotFoundException e){
            log.error(e.getMessage());
        }
    }

    private void cargarPagina(int pageIndex) {
        int fromIndex = pageIndex * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, reservas.size());

        ObservableList<Reserva> reservaObservableList = FXCollections.observableArrayList();
        reservaObservableList.addAll(reservas.subList(fromIndex, toIndex));

        tblReservas.setItems(reservaObservableList);
    }

    @FXML
    public void continuar(ActionEvent actionEvent) {
        Reserva seleccionada = tblReservas.getSelectionModel().getSelectedItem();
        Optional
                .ofNullable(seleccionada)
                .ifPresent(reserva -> {
                    try{
                        vistaUtils.cargarVista("/org/example/view/model/solicitud/crear/seleccionar-tipo-solicitud-view.fxml",
                                (SeleccionarTipoSolicitudVistaController controller) ->
                                        controller.setReserva(reserva));
                        vistaUtils.cerrarVentana(btnContinuar);
                    }catch (IOException e) {
                        globalExceptionHandler.handleIOException(e);
                    }
        });
    }

    @FXML
    public void cancelar(ActionEvent actionEvent) {
        vistaUtils.cerrarVentana(btnCancelar);
    }

    private void handleRowDoubleClick(Reserva reserva, MouseEvent ignoredEvent) {
        // Determinar la columna del click
        var clickedColumn = tblReservas.getFocusModel().getFocusedCell().getTableColumn();

        if (clickedColumn == colInscripcion) {
            try{
                vistaUtils.mostrarVistaInscripcion(reserva.getInscripcion());
            }catch (IOException e) {
                globalExceptionHandler.handleIOException(e);
            }

        }else if(clickedColumn == colAula) {
            try {
                vistaUtils.mostrarVistaEspacio(reserva.getEspacio());
            } catch (IOException e) {
                globalExceptionHandler.handleIOException(e);
            }
        }
    }
}
