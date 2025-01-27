package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.JsonNotFoundException;
import org.example.model.Laboratorio;
import org.example.service.AulaService;
import org.example.utils.TableUtils;
import org.example.utils.VistaUtils;
import org.springframework.stereotype.Component;


@Slf4j
@RequiredArgsConstructor
@Component
public class LaboratorioVistaController {
    private final VistaUtils vistaUtils;
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

    private final AulaService aulaService;



    @FXML
    public void initialize(){
        try {
            var laboratorios = aulaService.listarLaboratorios();
            TableUtils.inicializarTablaLaboratorio(colId,colNum,colCapacidad,colTieneProyector,colTieneTV,colComputadoras);

            //Agrego las aulas a la tabla de la vista
            ObservableList<Laboratorio> laboratorioObservableList = FXCollections.observableArrayList();
            laboratorioObservableList.addAll(laboratorios);
            this.tblLaboratorios.setItems(laboratorioObservableList);
        } catch (JsonNotFoundException e) {
            log.error(e.getMessage());
            vistaUtils.mostrarAlerta("Error al cargar laboratorios", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

}
