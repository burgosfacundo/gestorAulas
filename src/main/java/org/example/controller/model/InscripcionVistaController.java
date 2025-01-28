package org.example.controller.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Inscripcion;
import org.example.utils.TableUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class InscripcionVistaController {
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
    private List<Inscripcion> inscripciones;


    public void setInscripciones(List<Inscripcion> inscripciones) {
        this.inscripciones = inscripciones;
        actualizarTabla();
    }

    private void actualizarTabla() {
        // Configurar la tabla si no se ha hecho antes
        if (tblInscripciones.getColumns().isEmpty()) {
            TableUtils.inicializarTablaInscripcion(colId,colAlumnos,colMargenAlumnos,colFechaFinInscripcion,colAsignatura,
                    colComision,colProfesor);
        }

        ObservableList<Inscripcion> inscripcionObservableList = FXCollections.observableArrayList();
        inscripcionObservableList.addAll(inscripciones);
        tblInscripciones.setItems(inscripcionObservableList);
    }

    @FXML
    public void initialize(){
        TableUtils.inicializarTablaInscripcion(colId,colAlumnos,colMargenAlumnos,colFechaFinInscripcion,colAsignatura,
                colComision,colProfesor);
    }
}
