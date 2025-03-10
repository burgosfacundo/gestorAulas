package org.example.controller.model.espacio.editar;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Aula;
import org.example.model.Laboratorio;
import org.example.utils.TableUtils;
import org.example.utils.VistaUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class SeleccionarEspacioVistaController {
    private final VistaUtils vistaUtils;
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
    private Button btnContinuar;
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
        btnContinuar.disableProperty().bind(tblEspacios.getSelectionModel().selectedItemProperty().isNull());
    }

    @FXML
    public void continuar(ActionEvent actionEvent) {
        var seleccionada = tblEspacios.getSelectionModel().getSelectedItem();
        Optional
                .ofNullable(seleccionada)
                .ifPresent(espacio -> {
                    try{
                        vistaUtils.cargarVista("/org/example/view/model/espacio/editar/editar-espacio-view.fxml",
                                (EditarEspacioVistaController controller) ->
                                        controller.setEspacio(espacio));
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
}
