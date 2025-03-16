package org.example.controller.model.espacio;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.GlobalExceptionHandler;
import org.example.exception.JsonNotFoundException;
import org.example.exception.NotFoundException;
import org.example.model.Aula;
import org.example.model.Laboratorio;
import org.example.service.AulaService;
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
    private final AulaService aulaService;
    private final GlobalExceptionHandler globalExceptionHandler;
    @FXML
    private TableView<Aula> tblEspacios;
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
    private TableColumn<Laboratorio, Integer> colComputadoras;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnCancelar;
    private List<Aula> espacios;

    public void setEspacios(List<Aula> espacios) {
        this.espacios = espacios;
        actualizarTabla();
    }

    private void actualizarTabla() {
        // Configurar la tabla si no se ha hecho antes
        if (tblEspacios.getColumns().isEmpty()) {
            TableUtils.inicializarTablaEspacio(colId, colNum, colCapacidad, colTieneProyector, colTieneTV, colComputadoras);
        }

        ObservableList<Aula> espacioObservableList = FXCollections.observableArrayList();
        espacioObservableList.addAll(espacios);
        tblEspacios.setItems(espacioObservableList);
    }

    @FXML
    public void initialize() {
        // Asocio columnas de la tabla con atributos del modelo
        TableUtils.inicializarTablaEspacio(colId, colNum, colCapacidad, colTieneProyector, colTieneTV, colComputadoras);
        // Deshabilitar botón si no hay selección
        btnEliminar.disableProperty().bind(tblEspacios.getSelectionModel().selectedItemProperty().isNull());
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
                            aulaService.eliminar(espacio.getId());
                            vistaUtils.mostrarAlerta("Espacio eliminado correctamente", Alert.AlertType.INFORMATION);
                            vistaUtils.cerrarVentana(btnEliminar);
                        }
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
}
