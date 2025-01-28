package org.example.controller.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Laboratorio;
import org.example.utils.TableUtils;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Component
public class LaboratorioVistaController {
    @FXML
    private TableView<Laboratorio> tblLaboratorios;
    @FXML
    private TableColumn<Laboratorio,Integer> colId;
    @FXML
    private TableColumn<Laboratorio,Integer> colNum;
    @FXML
    private TableColumn<Laboratorio,Integer> colCapacidad;
    @FXML
    private TableColumn<Laboratorio,Boolean> colTieneProyector;
    @FXML
    private TableColumn<Laboratorio,Boolean> colTieneTV;
    @FXML
    private TableColumn<Laboratorio,Integer> colComputadoras;
    private List<Laboratorio> laboratorios;

    public void setLaboratorios(List<Laboratorio> laboratorios) {
        this.laboratorios = laboratorios;
        actualizarTabla();
    }

    private void actualizarTabla() {
        // Configurar la tabla si no se ha hecho antes
        if (tblLaboratorios.getColumns().isEmpty()) {
            TableUtils.inicializarTablaLaboratorio(colId,colNum,colCapacidad,colTieneProyector,colTieneTV,colComputadoras);
        }

        ObservableList<Laboratorio> laboratorioObservableList = FXCollections.observableArrayList();
        laboratorioObservableList.addAll(laboratorios);
        tblLaboratorios.setItems(laboratorioObservableList);
    }

    @FXML
    public void initialize(){
        //Asocio columnas de la tabla con atributos del modelo
        TableUtils.inicializarTablaLaboratorio(colId,colNum,colCapacidad,colTieneProyector,colTieneTV,colComputadoras);
    }
}
