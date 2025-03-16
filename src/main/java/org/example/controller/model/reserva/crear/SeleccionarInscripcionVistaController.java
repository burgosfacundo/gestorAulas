package org.example.controller.model.reserva.crear;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.GlobalExceptionHandler;
import org.example.exception.JsonNotFoundException;
import org.example.exception.NotFoundException;
import org.example.model.Inscripcion;
import org.example.service.InscripcionService;
import org.example.utils.TableUtils;
import org.example.utils.VistaUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class SeleccionarInscripcionVistaController {
    private final VistaUtils vistaUtils;
    private final InscripcionService inscripcionService;
    private final GlobalExceptionHandler globalExceptionHandler;
    @FXML
    private Button btnContinuar;
    @FXML
    private Button btnCancelar;
    @FXML
    private TableView<Inscripcion> tblInscripciones;
    @FXML
    private TableColumn<Inscripcion,Integer> colId;
    @FXML
    private TableColumn<Inscripcion,Integer> colAlumnos;
    @FXML
    private TableColumn<Inscripcion,Integer> colMargenAlumnos;
    @FXML
    private TableColumn<Inscripcion, LocalDate> colFechaFinInscripcion;
    @FXML
    private TableColumn<Inscripcion,String> colAsignatura;
    @FXML
    private TableColumn<Inscripcion,String> colComision;
    @FXML
    private TableColumn<Inscripcion,String> colProfesor;

    @FXML
    public void initialize(){
        TableUtils.inicializarTablaInscripcion(colId,colAlumnos,colMargenAlumnos,colFechaFinInscripcion,colAsignatura,
                colComision,colProfesor);

        btnContinuar.disableProperty().bind(tblInscripciones.getSelectionModel().selectedItemProperty().isNull());

        try {
            var inscripciones = inscripcionService.listar();

            if (tblInscripciones.getColumns().isEmpty()) {
                TableUtils.inicializarTablaInscripcion(colId,colAlumnos,colMargenAlumnos,colFechaFinInscripcion,colAsignatura,
                        colComision,colProfesor);
            }

            ObservableList<Inscripcion> inscripcionesObservableList = FXCollections.observableArrayList();
            inscripcionesObservableList.addAll(inscripciones);
            tblInscripciones.setItems(inscripcionesObservableList);
        } catch (NotFoundException e) {
           globalExceptionHandler.handleNotFoundException(e);
        }catch (JsonNotFoundException e) {
            globalExceptionHandler.handleJsonNotFoundException(e);
        }
    }

    public void continuar(ActionEvent actionEvent) {
        Inscripcion seleccionada = tblInscripciones.getSelectionModel().getSelectedItem();
        Optional
                .ofNullable(seleccionada)
                .ifPresent(inscripcion ->{
                    try {
                        vistaUtils.cargarVista("/org/example/view/model/reserva/crear/crear-reserva-view.fxml",
                                (CrearReservaVistaController controller) -> controller.setInscripcion(inscripcion));
                    } catch (IOException e) {
                        log.error(e.getMessage());
                    }
                });
    }

    public void cancelar(ActionEvent actionEvent) {vistaUtils.cerrarVentana(btnCancelar);}
}
