package org.example.controller.model.espacio;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.GlobalExceptionHandler;
import org.example.exception.NotFoundException;
import org.example.model.Espacio;
import org.example.model.Laboratorio;
import org.example.service.EspacioService;
import org.example.utils.TableUtils;
import org.example.utils.VistaUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class EliminarEspacioVistaController {
    private final VistaUtils vistaUtils;
    private final EspacioService espacioService;
    private final GlobalExceptionHandler globalExceptionHandler;
    @FXML
    Pagination pagination;
    @FXML
    private TableView<Espacio> tblEspacios;
    @FXML
    private TableColumn<Espacio, Integer> colNum;
    @FXML
    private TableColumn<Espacio, Integer> colCapacidad;
    @FXML
    private TableColumn<Espacio, Boolean> colTieneProyector;
    @FXML
    private TableColumn<Espacio, Boolean> colTieneTV;
    @FXML
    private TableColumn<Laboratorio, Integer> colComputadoras;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnCancelar;
    private static final int PAGE_SIZE = 10; // Tamaño de página

    @FXML
    public void initialize() {
        // Inicializar tabla
        TableUtils.inicializarTablaEspacio(colNum, colCapacidad, colTieneProyector, colTieneTV, colComputadoras);
        // Deshabilitar botón si no hay selección
        btnEliminar.disableProperty().bind(tblEspacios.getSelectionModel().selectedItemProperty().isNull());

        // Cargar y paginar los espacios directamente
        var espacios = espacioService.listar();
        int totalPages = (int) Math.ceil((double) espacios.size() / PAGE_SIZE);
        pagination.setPageCount(Math.max(totalPages, 1));

        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> cargarPagina(espacios, newIndex.intValue()));
        cargarPagina(espacios, 0); // Cargar la primera página
    }

    private void cargarPagina(List<Espacio> espacios, int pageIndex) {
        int fromIndex = pageIndex * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, espacios.size());

        ObservableList<Espacio> espacioObservableList = FXCollections.observableArrayList(espacios.subList(fromIndex, toIndex));
        tblEspacios.setItems(espacioObservableList);
    }


    @FXML
    public void eliminar(ActionEvent actionEvent) {
        var seleccionada = tblEspacios.getSelectionModel().getSelectedItem();
        Optional
                .ofNullable(seleccionada)
                .ifPresent(espacio -> {
                    try {
                        var result = vistaUtils.mostrarAlerta("Estas seguro?", Alert.AlertType.CONFIRMATION);

                        if (result == ButtonType.OK) {
                            espacioService.eliminar(espacio.getId());
                            vistaUtils.mostrarAlerta("Espacio eliminado correctamente", Alert.AlertType.INFORMATION);
                            vistaUtils.cerrarVentana(btnEliminar);
                        }
                    } catch (NotFoundException e) {
                        globalExceptionHandler.handleNotFoundException(e);
                    }
                });
    }

    @FXML
    public void cancelar(ActionEvent actionEvent) {
        vistaUtils.cerrarVentana(btnCancelar);
    }
}
