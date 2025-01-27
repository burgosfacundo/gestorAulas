package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Laboratorio;
import org.example.utils.TableUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LaboratorioDetalleController {
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

    private Laboratorio laboratorio;

    public void setLaboratorio(Laboratorio laboratorio) {
        this.laboratorio = laboratorio;
        actualizarTabla();
    }

    private void actualizarTabla() {
        // Configurar la tabla si no se ha hecho antes
        if (tblLaboratorios.getColumns().isEmpty()) {
            TableUtils.inicializarTablaLaboratorio(colId,colNum,colCapacidad,colTieneProyector,colTieneTV,colComputadoras);
        }

        ObservableList<Laboratorio> laboratorioObservableList = FXCollections.observableArrayList();
        laboratorioObservableList.add(laboratorio);
        tblLaboratorios.setItems(laboratorioObservableList);
    }

    @FXML
    public void initialize(){
        //Asocio columnas de la tabla con atributos del modelo
        TableUtils.inicializarTablaLaboratorio(colId,colNum,colCapacidad,colTieneProyector,colTieneTV,colComputadoras);
    }

}
