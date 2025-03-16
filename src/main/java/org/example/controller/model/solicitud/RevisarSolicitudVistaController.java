package org.example.controller.model.solicitud;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.enums.BloqueHorario;
import org.example.enums.EstadoSolicitud;
import org.example.exception.*;
import org.example.model.Laboratorio;
import org.example.model.SolicitudCambioAula;
import org.example.service.SolicitudCambioAulaService;
import org.example.utils.TableUtils;
import org.example.utils.VistaUtils;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
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
    private TableColumn<SolicitudCambioAula,Integer> colId;
    @FXML
    private TableColumn<SolicitudCambioAula,String> colReserva;
    @FXML
    private TableColumn<SolicitudCambioAula,String> colAula;
    @FXML
    private TableColumn<SolicitudCambioAula, String> colEstado;
    @FXML
    private TableColumn<SolicitudCambioAula, String> colTipo;
    @FXML
    private TableColumn<SolicitudCambioAula, LocalDate> colInicio;
    @FXML
    private TableColumn<SolicitudCambioAula, LocalDate> colFin;
    @FXML
    private TableColumn<SolicitudCambioAula, Map<DayOfWeek, Set<BloqueHorario>>> colDiaHorario;
    @FXML
    private TableColumn<SolicitudCambioAula,String> colComenProfe;
    @FXML
    private TableColumn<SolicitudCambioAula,String> colComenAdmin;

    @FXML
    public void initialize() {
        btnAprobar.disableProperty().bind(tblSolicitudes.getSelectionModel().selectedItemProperty().isNull());
        btnRechazar.disableProperty().bind(tblSolicitudes.getSelectionModel().selectedItemProperty().isNull());

        TableUtils.inicializarTablaSolicitudes(
                colId, colReserva, colAula, colEstado, colTipo,
                colInicio, colFin, colDiaHorario, colComenProfe, colComenAdmin
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
            private final Button btnVerHorarios;

            {
                btnVerHorarios = new Button("Ver");
                btnVerHorarios.setOnAction(event -> {
                    var solicitud = getTableView().getItems().get(getIndex());
                    Optional
                            .ofNullable(solicitud)
                            .ifPresent(r ->  vistaUtils.mostrarVistaHorarios(solicitud.getDiasYBloques()));
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

        try {
            var solicitudes = solicitudCambioAulaService.listarSolicitudesPorEstado(EstadoSolicitud.PENDIENTE);
            ObservableList<SolicitudCambioAula> solicitudesObservableList = FXCollections.observableArrayList();
            solicitudesObservableList.addAll(solicitudes);
            tblSolicitudes.setItems(solicitudesObservableList);
        } catch (JsonNotFoundException | NotFoundException e) {
            log.error("Error al actualizar la tabla: {}", e.getMessage());
        }
    }

    private void handleRowDoubleClick(SolicitudCambioAula solicitud, MouseEvent ignoredEvent) {
        // Determinar la columna del click
        var clickedColumn = tblSolicitudes.getFocusModel().getFocusedCell().getTableColumn();

        if (clickedColumn == colReserva) {
            vistaUtils.mostrarVistaReserva(solicitud.getReservaOriginal());
        } else if (clickedColumn == colAula) {
            if (solicitud.getNuevaAula() instanceof Laboratorio laboratorio) {
                vistaUtils.mostrarVistaLaboratorio(laboratorio);
            }else {
                vistaUtils.mostrarVistaAula(solicitud.getNuevaAula());
            }
        }
    }

    @FXML
    public void aprobar(ActionEvent actionEvent) {
        SolicitudCambioAula seleccionada = tblSolicitudes.getSelectionModel().getSelectedItem();
        Optional
                .ofNullable(seleccionada)
                .ifPresent(solicitud ->{
                    try {
                        var errores = validarCampos();
                        if (errores.isPresent()) {
                            vistaUtils.mostrarAlerta(String.join("\n", errores.get()), Alert.AlertType.ERROR);
                            return;
                        }

                        String comentario = comentarioAdmin.getText();
                        var result = vistaUtils.mostrarAlerta("Estas seguro de aprobar la solicitud?", Alert.AlertType.INFORMATION);
                        if (result == ButtonType.OK) {
                            solicitudCambioAulaService.aprobarSolicitud(solicitud.getId(), comentario);
                            vistaUtils.mostrarAlerta("Solicitud aprobada correctamente", Alert.AlertType.INFORMATION);
                            vistaUtils.cerrarVentana(btnAprobar);
                        }
                    } catch (JsonNotFoundException e) {
                        globalExceptionHandler.handleJsonNotFoundException(e);
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
                .ifPresent(solicitud ->{
                    try {
                        var result = vistaUtils.mostrarAlerta("Estas seguro de rechazar la solicitud?", Alert.AlertType.INFORMATION);

                        String comentario = comentarioAdmin.getText();
                        if (result == ButtonType.OK) {
                            solicitudCambioAulaService.rechazarSolicitud(solicitud.getId(), comentario);
                            vistaUtils.mostrarAlerta("Solicitud rechazada correctamente", Alert.AlertType.INFORMATION);
                            vistaUtils.cerrarVentana(btnRechazar);
                        }
                    } catch (JsonNotFoundException e) {
                        globalExceptionHandler.handleJsonNotFoundException(e);
                    } catch (NotFoundException e) {
                        globalExceptionHandler.handleNotFoundException(e);
                    } catch (BadRequestException e) {
                        globalExceptionHandler.handleBadRequestException(e);
                    }
                });
    }

    private Optional<List<String>> validarCampos() {
        List<String> errores = new ArrayList<>();

        vistaUtils.validarTextoArea(comentarioAdmin, "Debe ingresar un comentario.", errores);

        return errores.isEmpty() ? Optional.empty() : Optional.of(errores);
    }
}
