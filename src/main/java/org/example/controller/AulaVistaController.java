package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.example.exception.JsonNotFoundException;
import org.example.model.Aula;
import org.example.service.AulaService;
import org.example.utils.TableUtils;
import org.example.utils.VistaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class AulaVistaController implements Initializable {
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

    @Autowired
    public AulaVistaController(AulaService aulaService, VistaUtils vistaUtils, TableUtils tableUtils) {
        this.aulaService = aulaService;
        this.vistaUtils = vistaUtils;
        this.tableUtils = tableUtils;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb){
        try {
            var aulas = aulaService.listarAulas();
            //Asocio columnas de la tabla con atributos del modelo
            tableUtils.inicializarTablaAula(colId,colNum,colCapacidad,colTieneProyector,colTieneTV);
            //Agrego las aulas a la tabla de la vista
            ObservableList<Aula> aulaObservableList = FXCollections.observableArrayList();
            aulaObservableList.addAll(aulas);
            this.tblAulas.setItems(aulaObservableList);
        } catch (JsonNotFoundException e) {
            Logger.getLogger(AulaVistaController.class.getName()).log(Level.SEVERE, "Error al cargar aulas: " + e.getMessage());
            vistaUtils.mostrarAlerta("Error al cargar aulas", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

}
