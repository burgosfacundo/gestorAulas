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
import org.example.model.Aula;
import org.example.service.AulaService;
import org.example.utils.TableUtils;
import org.example.utils.VistaUtils;
import org.springframework.stereotype.Component;


@Slf4j
@RequiredArgsConstructor
@Component
public class AulaVistaController{
    private final VistaUtils vistaUtils;
    private final TableUtils tableUtils;
    @FXML
    public TableView<Aula> tblAulas;
    @FXML
    public TableColumn<Aula,Integer> colId;
    @FXML
    public TableColumn<Aula,Integer> colNum;
    @FXML
    public TableColumn<Aula,Integer> colCapacidad;
    @FXML
    public TableColumn<Aula,Boolean> colTieneProyector;
    @FXML
    public TableColumn<Aula,Boolean> colTieneTV;

    private final AulaService aulaService;


    @FXML
    public void initialize(){
        try {
            var aulas = aulaService.listarAulas();
            //Asocio columnas de la tabla con atributos del modelo
            tableUtils.inicializarTablaAula(colId,colNum,colCapacidad,colTieneProyector,colTieneTV);
            //Agrego las aulas a la tabla de la vista
            ObservableList<Aula> aulaObservableList = FXCollections.observableArrayList();
            aulaObservableList.addAll(aulas);
            this.tblAulas.setItems(aulaObservableList);
        } catch (JsonNotFoundException e) {
           log.error(e.getMessage());
            vistaUtils.mostrarAlerta("Error al cargar aulas", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

}
