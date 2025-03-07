package org.example.controller.model.solicitud.crearSolicitud;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.controller.model.espacio.AulaVistaController;
import org.example.controller.model.InscripcionVistaController;
import org.example.enums.BloqueHorario;
import org.example.exception.JsonNotFoundException;
import org.example.exception.NotFoundException;
import org.example.model.Aula;
import org.example.model.Inscripcion;
import org.example.model.Reserva;
import org.example.model.SolicitudCambioAula;
import org.example.security.SesionActual;
import org.example.service.ReservaService;
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
@Component
@RequiredArgsConstructor
public class SeleccionarReservaVistaController {
    private final ReservaService reservaService;
    private final VistaUtils vistaUtils;
    private final SesionActual sesionActual;
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
    private TableColumn<SolicitudCambioAula, Map<DayOfWeek, Set<BloqueHorario>>> colDiaHorario;
    @FXML
    private Button btnContinuar;
    @FXML
    private Button btnCancelar;

    @FXML
    public void initialize() {
        try {
            var reservas = reservaService.listarReservasPorProfesor(sesionActual.getUsuario().getProfesor().getId());
            ObservableList<Reserva> data = FXCollections.observableArrayList(reservas);
            tblReservas.setItems(data);
            TableUtils.inicializarTablaReserva(colId,colFechaInicio,colFechaFin,colAula,colInscripcion,colDiaHorario);
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
        }catch (JsonNotFoundException | NotFoundException e){
            log.error(e.getMessage());
        }


    }

    @FXML
    public void continuar(ActionEvent actionEvent) {
        Reserva seleccionada = tblReservas.getSelectionModel().getSelectedItem();
        Optional
                .ofNullable(seleccionada)
                .ifPresent(reserva -> {
                    try{
                        vistaUtils.cargarVista("/org/example/view/model/solicitud/crearSolicitud/seleccionar-tipo-solicitud-view.fxml",
                                (SeleccionarTipoSolicitudVistaController controller) ->
                                        controller.setReserva(reserva));
                        vistaUtils.cerrarVentana(btnContinuar);
                    }catch (IOException e) {
                        log.error(e.getMessage());
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
            mostrarVistaInscripcion(reserva.getInscripcion());
        }else if(clickedColumn == colAula) {
            mostrarVistaAula(reserva.getAula());
        }
    }

    private void mostrarVistaInscripcion(Inscripcion inscripcion) {
        try {
            vistaUtils.cargarVista("/org/example/view/model/inscripcion-view.fxml",
                    (InscripcionVistaController controller) -> controller.setInscripciones(List.of(inscripcion)));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void mostrarVistaAula(Aula aula) {
        try {
            vistaUtils.cargarVista("/org/example/view/model/espacio/aula-view.fxml",
                    (AulaVistaController controller) -> controller.setAulas(List.of(aula)));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
