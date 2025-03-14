package org.example.controller.model.solicitud;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.enums.BloqueHorario;
import org.example.enums.EstadoSolicitud;
import org.example.exception.JsonNotFoundException;
import org.example.exception.NotFoundException;
import org.example.model.Laboratorio;
import org.example.model.SolicitudCambioAula;
import org.example.security.SesionActual;
import org.example.service.SolicitudCambioAulaService;
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
public class SolicitudVistaController {
    private final SolicitudCambioAulaService solicitudCambioAulaService;
    private final SesionActual sesionActual;
    private final VistaUtils vistaUtils;
    private EstadoSolicitud estadoSolicitud;
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
    }

    public void setEstadoSolicitud(EstadoSolicitud estadoSolicitud) {
        this.estadoSolicitud = estadoSolicitud;
        actualizarTabla();
    }

    private void actualizarTabla() {
        try {
            var user = sesionActual.getUsuario();
            var solicitudes = solicitudCambioAulaService.listarSolicitudesPorEstadoYProfesor(
                    this.estadoSolicitud, user.getProfesor().getId()
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
}
