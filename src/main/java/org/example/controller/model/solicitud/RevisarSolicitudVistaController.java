package org.example.controller.model.solicitud;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.enums.EstadoSolicitud;
import org.example.exception.*;
import org.example.model.DiaBloque;
import org.example.model.SolicitudCambioAula;
import org.example.service.SolicitudCambioAulaService;
import org.example.utils.TableUtils;
import org.example.utils.VistaUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class RevisarSolicitudVistaController {
    private final VistaUtils vistaUtils;
    private final SolicitudCambioAulaService solicitudCambioAulaService;
    private final GlobalExceptionHandler globalExceptionHandler;
    @FXML
    private TextArea comentarioAdmin;
    @FXML
    private Button btnAprobar;
    @FXML
    private Button btnRechazar;
    @FXML
    private TableView<SolicitudCambioAula> tblSolicitudes;
    @FXML
    private TableColumn<SolicitudCambioAula, String> colReserva;
    @FXML
    private TableColumn<SolicitudCambioAula, String> colAula;
    @FXML
    private TableColumn<SolicitudCambioAula, String> colEstado;
    @FXML
    private TableColumn<SolicitudCambioAula, String> colTipo;
    @FXML
    private TableColumn<SolicitudCambioAula, LocalDate> colInicio;
    @FXML
    private TableColumn<SolicitudCambioAula, LocalDate> colFin;
    @FXML
    private TableColumn<SolicitudCambioAula, Set<DiaBloque>> colDiaHorario;
    @FXML
    private TableColumn<SolicitudCambioAula, String> colComenProfe;
    @FXML
    private TableColumn<SolicitudCambioAula, String> colComenAdmin;
    @FXML
    private Pagination pagination;
    private List<SolicitudCambioAula> solicitudes;
    private static final int PAGE_SIZE = 10;

    @FXML
    public void initialize() {
        btnAprobar.disableProperty().bind(tblSolicitudes.getSelectionModel().selectedItemProperty().isNull());
        btnRechazar.disableProperty().bind(tblSolicitudes.getSelectionModel().selectedItemProperty().isNull());

        TableUtils.inicializarTablaSolicitudes(
                colReserva, colAula, colEstado, colTipo,
                colInicio, colFin, colComenProfe, colComenAdmin,colDiaHorario
        );

        tblSolicitudes.setRowFactory(tableView -> {
            TableRow<SolicitudCambioAula> row = new TableRow<>();
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
                    SolicitudCambioAula solicitud = getTableRow().getItem();
                    if (solicitud != null && solicitud.getDiasYBloques() != null) {
                        vistaUtils.mostrarVistaHorarios(solicitud.getDiasYBloques());
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

        try {
            solicitudes = solicitudCambioAulaService.listarSolicitudesPorEstado(EstadoSolicitud.PENDIENTE);
            int totalPages = (int) Math.ceil((double) solicitudes.size() / PAGE_SIZE);
            pagination.setPageCount(Math.max(totalPages, 1));

            pagination.currentPageIndexProperty().addListener(
                    (obs, oldIndex, newIndex) -> cargarPagina(newIndex.intValue()));

            cargarPagina(0);
        } catch (NotFoundException e) {
            globalExceptionHandler.handleNotFoundException(e);
        }

    }

    private void cargarPagina(int pageIndex) {
        int fromIndex = pageIndex * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, solicitudes.size());

        ObservableList<SolicitudCambioAula> solicitudesObservableList = FXCollections.observableArrayList();
        solicitudesObservableList.addAll(solicitudes.subList(fromIndex, toIndex));
        tblSolicitudes.setItems(solicitudesObservableList);
    }

    private void handleRowDoubleClick(SolicitudCambioAula solicitud, MouseEvent ignoredEvent) {
        // Determinar la columna del click
        var clickedColumn = tblSolicitudes.getFocusModel().getFocusedCell().getTableColumn();

        if (clickedColumn == colReserva) {
            vistaUtils.mostrarVistaReserva(solicitud.getReservaOriginal());
        } else if (clickedColumn == colAula) {
            vistaUtils.mostrarVistaEspacio(solicitud.getNuevoEspacio());

        }
    }

    @FXML
    public void aprobar(ActionEvent actionEvent) {
        SolicitudCambioAula seleccionada = tblSolicitudes.getSelectionModel().getSelectedItem();
        Optional
                .ofNullable(seleccionada)
                .ifPresent(solicitud -> {
                    try {
                        var result = vistaUtils.mostrarAlerta("Estas seguro de aprobar la solicitud?", Alert.AlertType.CONFIRMATION);
                        String comentario = comentarioAdmin.getText();
                        if (result == ButtonType.OK) {
                            solicitudCambioAulaService.aprobarSolicitud(solicitud.getId(), comentario);
                            vistaUtils.mostrarAlerta("Solicitud aprobada correctamente", Alert.AlertType.INFORMATION);
                            vistaUtils.cerrarVentana(btnAprobar);
                        }
                    } catch (NotFoundException e) {
                        globalExceptionHandler.handleNotFoundException(e);
                    } catch (ConflictException e) {
                        globalExceptionHandler.handleConflictException(e);
                    } catch (BadRequestException e) {
                        globalExceptionHandler.handleBadRequestException(e);
                    }
                });
    }

    @FXML
    public void rechazar(ActionEvent actionEvent) {
        SolicitudCambioAula seleccionada = tblSolicitudes.getSelectionModel().getSelectedItem();
        Optional
                .ofNullable(seleccionada)
                .ifPresent(solicitud -> {
                    try {
                        var result = vistaUtils.mostrarAlerta("Estas seguro de rechazar la solicitud?", Alert.AlertType.CONFIRMATION);

                        String comentario = comentarioAdmin.getText();
                        if (result == ButtonType.OK) {
                            solicitudCambioAulaService.rechazarSolicitud(solicitud.getId(), comentario);
                            vistaUtils.mostrarAlerta("Solicitud rechazada correctamente", Alert.AlertType.INFORMATION);
                            vistaUtils.cerrarVentana(btnRechazar);
                        }
                    } catch (NotFoundException e) {
                        globalExceptionHandler.handleNotFoundException(e);
                    } catch (BadRequestException e) {
                        globalExceptionHandler.handleBadRequestException(e);
                    }
                });
    }
}