package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.example.enums.BloqueHorario;
import org.example.enums.EstadoSolicitud;
import org.example.exception.JsonNotFoundException;
import org.example.exception.NotFoundException;
import org.example.model.Aula;
import org.example.model.Reserva;
import org.example.model.SolicitudCambioAula;
import org.example.security.SesionActual;
import org.example.service.SolicitudCambioAulaService;
import org.example.utils.TableUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

@RequiredArgsConstructor
@Component
public class SolicitudesVistaController{
    private static final Logger logger = Logger.getLogger(SolicitudesVistaController.class.getName());
    private final TableUtils tableUtils;
    private final SolicitudCambioAulaService solicitudCambioAulaService;
    private final SesionActual sesionActual;
    private EstadoSolicitud estadoSolicitud;
    @FXML
    public TableView<SolicitudCambioAula> tblSolicitudes;
    @FXML
    public TableColumn<SolicitudCambioAula,Integer> colId;
    @FXML
    public TableColumn<SolicitudCambioAula,String> colReserva;
    @FXML
    public TableColumn<SolicitudCambioAula,String> colAula;
    @FXML
    public TableColumn<SolicitudCambioAula, String> colEstado;
    @FXML
    public TableColumn<SolicitudCambioAula, String> colTipo;
    @FXML
    public TableColumn<SolicitudCambioAula, LocalDate> colInicio;
    @FXML
    public TableColumn<SolicitudCambioAula, LocalDate> colFin;
    @FXML
    public TableColumn<SolicitudCambioAula, Map<DayOfWeek, Set<BloqueHorario>>> colDiaHorario;
    @FXML
    public TableColumn<SolicitudCambioAula,String> colComenProfe;
    @FXML
    public TableColumn<SolicitudCambioAula,String> colComenAdmin;


    @FXML
    public void initialize() {
        tableUtils.inicializarTablaSolicitudes(
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
                tableUtils.inicializarTablaSolicitudes(
                        colId, colReserva, colAula, colEstado, colTipo,
                        colInicio, colFin, colDiaHorario, colComenProfe, colComenAdmin
                );
            }

            ObservableList<SolicitudCambioAula> solicitudesObservableList = FXCollections.observableArrayList();
            solicitudesObservableList.addAll(solicitudes);
            tblSolicitudes.setItems(solicitudesObservableList);
        } catch (JsonNotFoundException | NotFoundException e) {
            logger.severe("Error al actualizar la tabla: " + e.getMessage());
        }
    }


    private void handleRowDoubleClick(SolicitudCambioAula solicitud, MouseEvent event) {
        // Determinar la columna del click
        TableColumn clickedColumn = tblSolicitudes.getFocusModel().getFocusedCell().getTableColumn();

        if (clickedColumn == colReserva) {
            mostrarVistaReserva(solicitud.getReservaOriginal());
        } else if (clickedColumn == colAula) {
            mostrarVistaAula(solicitud.getNuevaAula());
        }
    }

    private void mostrarVistaReserva(Reserva reserva) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/view/profesor/reserva-detalle-view.fxml"));
            Parent root = loader.load();

            // Pasar los datos al controlador
            ReservaDetalleController controller = loader.getController();
            controller.setReserva(reserva);

            // Mostrar la vista en un nuevo Stage
            Stage stage = new Stage();
            stage.setTitle("Detalle de Reserva");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
    }

    private void mostrarVistaAula(Aula aula) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/view/profesor/aula-detalle-view.fxml"));
            Parent root = loader.load();

            // Pasar los datos al controlador
            AulaDetalleController controller = loader.getController();
            controller.setAula(aula);

            // Mostrar la vista en un nuevo Stage
            Stage stage = new Stage();
            stage.setTitle("Detalle de Aula");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
    }

}
