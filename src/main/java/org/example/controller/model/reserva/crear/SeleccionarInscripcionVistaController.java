package org.example.controller.model.reserva.crear;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Inscripcion;
import org.example.service.InscripcionService;
import org.example.utils.TableUtils;
import org.example.utils.VistaUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class SeleccionarInscripcionVistaController {
    private final VistaUtils vistaUtils;
    private final InscripcionService inscripcionService;
    @FXML
    private Button btnContinuar;
    @FXML
    private Button btnCancelar;
    @FXML
    private TableView<Inscripcion> tblInscripciones;
    @FXML
    private TableColumn<Inscripcion,Integer> colAlumnos;
    @FXML
    private TableColumn<Inscripcion,Integer> colMargenAlumnos;
    @FXML
    private TableColumn<Inscripcion, LocalDate> colFechaFinInscripcion;
    @FXML
    private TableColumn<Inscripcion,String> colAsignatura;
    @FXML
    private TableColumn<Inscripcion,Integer> colComision;
    @FXML
    private TableColumn<Inscripcion,Integer> colYear;
    @FXML
    private TableColumn<Inscripcion,Integer> colCuatrimestre;
    @FXML
    private TableColumn<Inscripcion,String> colProfesor;
    @FXML
    private ComboBox<Integer> cmbYear;
    @FXML
    private ComboBox<Integer> cmbCuatrimestre;
    @FXML
    private Button btnFiltrar;
    @FXML
    private Pagination pagination;
    private static final int PAGE_SIZE = 10;

    @FXML
    public void initialize(){
        TableUtils.inicializarTablaInscripcion(colAlumnos,colMargenAlumnos,colFechaFinInscripcion,colAsignatura,
                colComision,colYear,colCuatrimestre,colProfesor);

        // Deshabilitar el botón de continuar al inicio
        btnContinuar.disableProperty().bind(tblInscripciones.getSelectionModel().selectedItemProperty().isNull());

        // Deshabilitar el botón de filtrado si ambos ComboBox están vacíos
        btnFiltrar.disableProperty().bind(
                cmbYear.valueProperty().isNull().and(cmbCuatrimestre.valueProperty().isNull())
        );

        // Cargar opciones en los ComboBox
        cmbYear.setItems(FXCollections.observableArrayList(1, 2));
        cmbCuatrimestre.setItems(FXCollections.observableArrayList(1, 2, 3, 4));

        // Cargar y paginar las inscripciones directamente
        var inscripciones = inscripcionService.listar();
        int totalPages = (int) Math.ceil((double) inscripciones.size() / PAGE_SIZE);
        pagination.setPageCount(Math.max(totalPages, 1));

        pagination.currentPageIndexProperty().addListener(
                (obs, oldIndex, newIndex) -> cargarPagina(inscripciones, newIndex.intValue()));
        cargarPagina(inscripciones, 0); // Cargar la primera página
    }

    private void cargarPagina(List<Inscripcion> inscripciones, int pageIndex) {
        int fromIndex = pageIndex * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, inscripciones.size());

        ObservableList<Inscripcion> reservaObservableList = FXCollections.observableArrayList(inscripciones.subList(fromIndex, toIndex));
        tblInscripciones.setItems(reservaObservableList);
        tblInscripciones.refresh();
    }

    @FXML
    private void filtrar() {
        Integer yearSeleccionado = cmbYear.getValue();
        Integer cuatrimestreSeleccionado = cmbCuatrimestre.getValue();

        var inscripciones = inscripcionService.listar();

        // Aplicar los filtros
        List<Inscripcion> filtradas = inscripciones.stream()
                .filter(i -> (yearSeleccionado == null || Objects.equals(i.getYear(), yearSeleccionado)))
                .filter(i -> (cuatrimestreSeleccionado == null || Objects.equals(i.getCuatrimestre(), cuatrimestreSeleccionado)))
                .toList();

        int totalPages = (int) Math.ceil((double) filtradas.size() / PAGE_SIZE);
        pagination.setPageCount(Math.max(totalPages, 1));

        pagination.currentPageIndexProperty().addListener(
                (obs, oldIndex, newIndex) -> cargarPagina(filtradas, newIndex.intValue()));
        cargarPagina(filtradas, 0);
    }

    @FXML
    public void continuar() {
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

    @FXML
    public void cancelar() {vistaUtils.cerrarVentana(btnCancelar);}
}
