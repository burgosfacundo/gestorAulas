package org.example.controller.model.solicitud.crear;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.enums.TipoSolicitud;
import org.example.exception.BadRequestException;
import org.example.exception.GlobalExceptionHandler;
import org.example.exception.JsonNotFoundException;
import org.example.exception.NotFoundException;
import org.example.model.Aula;
import org.example.model.Laboratorio;
import org.example.model.Reserva;
import org.example.model.SolicitudCambioAula;
import org.example.service.AulaService;
import org.example.service.SolicitudCambioAulaService;
import org.example.utils.TableUtils;
import org.example.utils.VistaUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
@Setter
public class SeleccionarTipoSolicitudVistaController {
    private final VistaUtils vistaUtils;
    private final AulaService aulaService;
    private final GlobalExceptionHandler globalExceptionHandler;
    private final SolicitudCambioAulaService solicitudCambioAulaService;
    private Reserva reserva;
    private ObservableList<Aula> aulasDisponibles = FXCollections.observableArrayList();
    @FXML
    private ComboBox<TipoSolicitud> tipoSolicitudComboBox;
    @FXML
    private DatePicker fechaInicioPicker;
    @FXML
    private DatePicker fechaFinPicker;
    @FXML
    private TableView<Aula> tblAulas;
    @FXML
    private TableColumn<Aula, Integer> colId;
    @FXML
    private TableColumn<Aula, Integer> colNum;
    @FXML
    private TableColumn<Aula, Integer> colCapacidad;
    @FXML
    private TableColumn<Aula, Boolean> colTieneProyector;
    @FXML
    private TableColumn<Aula, Boolean> colTieneTV;
    @FXML
    private TableColumn<Aula, Integer> colComputadoras;
    @FXML
    private TextArea comentarioProfesorTextArea;
    @FXML
    private Button btnEnviarSolicitudButton;
    @FXML
    private Button btnBuscar;

    @FXML
    public void initialize() {
        tipoSolicitudComboBox.getItems().setAll(TipoSolicitud.values());
        tipoSolicitudComboBox.setOnAction(event -> actualizarFechas());

        TableUtils.inicializarTablaAula(colId,colNum,colCapacidad,colTieneProyector,colTieneTV);

        colComputadoras.setCellValueFactory(cellData -> {
            if (cellData.getValue() instanceof Laboratorio lab) {
                return new SimpleIntegerProperty(lab.getComputadoras()).asObject();
            } else {
                colComputadoras.setVisible(false);
                return new SimpleIntegerProperty(0).asObject();
            }
        });

        tblAulas.setItems(aulasDisponibles);
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;

        if (reserva.getFechaInicio().isEqual(reserva.getFechaFin())) {
            tipoSolicitudComboBox.setValue(TipoSolicitud.TEMPORAL);
            tipoSolicitudComboBox.setDisable(true);
            fechaInicioPicker.setValue(reserva.getFechaInicio());
            fechaInicioPicker.setDisable(true);
            fechaFinPicker.setValue(reserva.getFechaFin());
            fechaFinPicker.setDisable(true);
        }
    }

    private void actualizarFechas() {
            Optional.ofNullable(tipoSolicitudComboBox.getValue())
                    .ifPresent(tipo -> {
                        if (tipo == TipoSolicitud.PERMANENTE) {
                            fechaInicioPicker.setValue(reserva.getFechaInicio());
                            fechaFinPicker.setValue(reserva.getFechaFin());
                            fechaInicioPicker.setDisable(true);
                            fechaFinPicker.setDisable(true);
                        } else {
                            fechaInicioPicker.setValue(LocalDate.now());
                            fechaFinPicker.setValue(LocalDate.now());
                            fechaInicioPicker.setDisable(false);
                            fechaFinPicker.setDisable(false);
                        }
                    });
    }

    @FXML
    private void actualizarListaAulas(){
        Optional.ofNullable(reserva)
                .ifPresent(r -> {
                    List<? extends Aula> disponibles;
                    try{
                        if (r.getAula() instanceof Laboratorio lab) {
                            disponibles = aulaService.listarLaboratoriosDisponiblesConCondiciones(
                                    lab.getComputadoras(),
                                    lab.getCapacidad(),
                                    lab.isTieneProyector(),lab.isTieneTV(),
                                    fechaInicioPicker.getValue(), fechaFinPicker.getValue(),
                                    r.getDiasYBloques()
                            );
                        } else {
                            disponibles = aulaService.listarAulasDisponiblesConCondiciones(
                                    r.getAula().getCapacidad(),
                                    r.getAula().isTieneProyector(),r.getAula().isTieneTV(),
                                    fechaInicioPicker.getValue(), fechaFinPicker.getValue(),
                                    r.getDiasYBloques()
                            );
                        }
                        aulasDisponibles.setAll(disponibles);
                        if (disponibles.isEmpty()) {
                            vistaUtils.mostrarAlerta("No hay aulas disponibles.", Alert.AlertType.INFORMATION);
                        }
                    }catch (JsonNotFoundException e){
                        globalExceptionHandler.handleJsonNotFoundException(e);
                    }
                });
    }

    @FXML
    private void enviarSolicitud() {
        if (reserva == null) {
            vistaUtils.mostrarAlerta("No se seleccionó una reserva.", Alert.AlertType.ERROR);
            return;
        }

        TipoSolicitud tipo = tipoSolicitudComboBox.getValue();
        LocalDate fechaInicio = fechaInicioPicker.getValue();
        LocalDate fechaFin = fechaFinPicker.getValue();
        Aula aulaSeleccionada = tblAulas.getSelectionModel().getSelectedItem();
        String comentarioProfesor = comentarioProfesorTextArea.getText();

        if (tipo == null || aulaSeleccionada == null || fechaInicio == null || fechaFin == null) {
            vistaUtils.mostrarAlerta("Debe completar todos los campos.", Alert.AlertType.ERROR);
            return;
        }

        SolicitudCambioAula solicitud = new SolicitudCambioAula(
                null, reserva.getInscripcion().getProfesor(), reserva, aulaSeleccionada,
                tipo, fechaInicio, fechaFin, reserva.getDiasYBloques(),
                comentarioProfesor
        );

        try{
            solicitudCambioAulaService.guardar(solicitud);
            vistaUtils.mostrarAlerta("Solicitud enviada correctamente.", Alert.AlertType.INFORMATION);
            vistaUtils.cerrarVentana(btnEnviarSolicitudButton);
        }catch (JsonNotFoundException e){
            globalExceptionHandler.handleJsonNotFoundException(e);
        }catch (NotFoundException e){
            globalExceptionHandler.handleNotFoundException(e);
        }catch (BadRequestException e){
            globalExceptionHandler.handleBadRequestException(e);
        }
    }
}