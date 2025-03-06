package org.example.controller.model.solicitud;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.controller.model.AulaVistaController;
import org.example.controller.model.LaboratorioVistaController;
import org.example.controller.model.ReservaVistaController;
import org.example.enums.BloqueHorario;
import org.example.enums.EstadoSolicitud;
import org.example.exception.GlobalExceptionHandler;
import org.example.exception.JsonNotFoundException;
import org.example.exception.NotFoundException;
import org.example.model.Aula;
import org.example.model.Laboratorio;
import org.example.model.Reserva;
import org.example.model.SolicitudCambioAula;
import org.example.security.SesionActual;
import org.example.service.SolicitudCambioAulaService;
import org.example.utils.TableUtils;
import org.example.utils.VistaUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class EliminarSolicitudVistaController {
    private final VistaUtils vistaUtils;
    private final SesionActual sesionActual;
    private final SolicitudCambioAulaService solicitudCambioAulaService;
    private final GlobalExceptionHandler globalExceptionHandler;
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
    private Button btnEliminar;
    @FXML
    private Button btnCancelar;

    @FXML
    public void initialize() {
        TableUtils.inicializarTablaSolicitudes(
                colId, colReserva, colAula, colEstado, colTipo,
                colInicio, colFin, colDiaHorario, colComenProfe, colComenAdmin
        );
        btnEliminar.disableProperty().bind(tblSolicitudes.getSelectionModel().selectedItemProperty().isNull());

        tblSolicitudes.setRowFactory(tableView -> {
            TableRow<SolicitudCambioAula> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    handleRowDoubleClick(row.getItem(), event);
                }
            });
            return row;
        });

        try {
            var user = sesionActual.getUsuario();
            var solicitudes = solicitudCambioAulaService.listarSolicitudesPorEstadoYProfesor(
                    EstadoSolicitud.PENDIENTE, user.getProfesor().getId()
            );

            // Configurar la tabla si no se ha hecho antes
            if (tblSolicitudes.getColumns().isEmpty()) {
                TableUtils.inicializarTablaSolicitudes(
                        colId, colReserva, colAula, colEstado, colTipo,
                        colInicio, colFin, colDiaHorario, colComenProfe, colComenAdmin
                );
            }

            ObservableList<SolicitudCambioAula> solicitudesObservableList = FXCollections.observableArrayList();
            solicitudesObservableList.addAll(solicitudes);
            tblSolicitudes.setItems(solicitudesObservableList);
        } catch (JsonNotFoundException | NotFoundException e) {
            log.error("Error al actualizar la tabla: {}", e.getMessage());
        }
    }

    @FXML
    public void eliminar(ActionEvent actionEvent) {
        SolicitudCambioAula seleccionada = tblSolicitudes.getSelectionModel().getSelectedItem();
        Optional
                .ofNullable(seleccionada)
                .ifPresent(solicitud ->{
                    try {
                        vistaUtils.mostrarAlerta("Estas seguro?", Alert.AlertType.CONFIRMATION);
                        solicitudCambioAulaService.eliminar(solicitud.getId());
                        vistaUtils.mostrarAlerta("Solicitud eliminada correctamente", Alert.AlertType.INFORMATION);
                        vistaUtils.cerrarVentana(btnEliminar);
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

    private void handleRowDoubleClick(SolicitudCambioAula solicitud, MouseEvent ignoredEvent) {
        // Determinar la columna del click
        var clickedColumn = tblSolicitudes.getFocusModel().getFocusedCell().getTableColumn();

        if (clickedColumn == colReserva) {
            mostrarVistaReserva(solicitud.getReservaOriginal());
        } else if (clickedColumn == colAula) {
            if (solicitud.getNuevaAula() instanceof Laboratorio laboratorio) {
                mostrarVistaLaboratorio(laboratorio);
            }else {
                mostrarVistaAula(solicitud.getNuevaAula());
            }
        }
    }

    private void mostrarVistaReserva(Reserva reserva) {
        try {
            vistaUtils.cargarVista("/org/example/view/model/reserva-view.fxml",
                    (ReservaVistaController controller) -> controller.setReservas(List.of(reserva)));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void mostrarVistaAula(Aula aula) {
        try {
            vistaUtils.cargarVista("/org/example/view/model/aula-view.fxml",
                    (AulaVistaController controller) -> controller.setAulas(List.of(aula)));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void mostrarVistaLaboratorio(Laboratorio laboratorio) {
        try {
            vistaUtils.cargarVista("/org/example/view/model/laboratorio-view.fxml",
                    (LaboratorioVistaController controller) -> controller.setLaboratorios(List.of(laboratorio)));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
