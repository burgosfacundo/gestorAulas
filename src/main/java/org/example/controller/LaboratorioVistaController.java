package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.exception.JsonNotFoundException;
import org.example.model.Laboratorio;
import org.example.service.AulaService;
import org.example.utils.VistaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class LaboratorioVistaController implements Initializable {
    private final VistaUtils vistaUtils;
    @FXML
    public TableView<Laboratorio> tblLaboratorios;
    @FXML
    public TableColumn<Laboratorio,Integer> colId;
    @FXML
    public TableColumn<Laboratorio,Integer> colNum;
    @FXML
    public TableColumn<Laboratorio,Integer> colCapacidad;
    @FXML
    public TableColumn<Laboratorio,Boolean> colTieneProyector;
    @FXML
    public TableColumn<Laboratorio,Boolean> colTieneTV;
    @FXML
    public TableColumn<Laboratorio,Integer> colComputadoras;

    private final AulaService aulaService;

    @Autowired
    public LaboratorioVistaController(AulaService aulaService, VistaUtils vistaUtils) {
        this.aulaService = aulaService;
        this.vistaUtils = vistaUtils;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb){
        try {
            var laboratorios = aulaService.listarLaboratorios();
            //Asocio columnas de la tabla con atributos del modelo
            this.colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            this.colNum.setCellValueFactory(new PropertyValueFactory<>("numero"));
            this.colCapacidad.setCellValueFactory(new PropertyValueFactory<>("capacidad"));
            this.colTieneProyector.setCellValueFactory(new PropertyValueFactory<>("tieneProyector"));
            this.colTieneTV.setCellValueFactory(new PropertyValueFactory<>("tieneTV"));
            this.colComputadoras.setCellValueFactory(new PropertyValueFactory<>("computadoras"));

            // Personalizo las columnas booleanas (tieneProyector y tieneTV)
            this.colTieneProyector.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null); // Si no hay datos, la celda estará vacía
                    } else {
                        setText(item ? "✓" : "✗"); // Mostrar tick o cruz
                        if (item) {
                            setStyle("-fx-text-fill: green; -fx-font-weight: bold;"); // Color verde y texto en negrita
                        } else {
                            setStyle("-fx-text-fill: red; -fx-font-weight: bold;"); // Color rojo y texto en negrita
                        }
                    }
                }
            });

            this.colTieneTV.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null); // Si no hay datos, la celda estará vacía
                    } else {
                        setText(item ? "✓" : "✗"); // Mostrar tick o cruz
                        if (item) {
                            setStyle("-fx-text-fill: green; -fx-font-weight: bold;"); // Color verde y texto en negrita
                        } else {
                            setStyle("-fx-text-fill: red; -fx-font-weight: bold;"); // Color rojo y texto en negrita
                        }
                    }
                }
            });

            //Agrego las aulas a la tabla de la vista
            ObservableList<Laboratorio> laboratorioObservableList = FXCollections.observableArrayList();
            laboratorioObservableList.addAll(laboratorios);
            this.tblLaboratorios.setItems(laboratorioObservableList);
        } catch (JsonNotFoundException e) {
            Logger.getLogger(AulaVistaController.class.getName()).log(Level.SEVERE, "Error al cargar laboratorios: " + e.getMessage());
            vistaUtils.mostrarAlerta("Error al cargar laboratorios", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

}
