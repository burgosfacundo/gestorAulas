package org.example.utils;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.model.Aula;
import org.example.model.Laboratorio;
import org.springframework.stereotype.Component;

@Component
public class TableUtils {
    public void inicializarTablaAula(TableColumn<Aula, Integer> colId,
                                            TableColumn<Aula, Integer> colNum,
                                            TableColumn<Aula, Integer> colCapacidad,
                                            TableColumn<Aula, Boolean> colTieneProyector,
                                            TableColumn<Aula, Boolean> colTieneTV){
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNum.setCellValueFactory(new PropertyValueFactory<>("numero"));
        colCapacidad.setCellValueFactory(new PropertyValueFactory<>("capacidad"));
        colTieneProyector.setCellValueFactory(new PropertyValueFactory<>("tieneProyector"));
        colTieneTV.setCellValueFactory(new PropertyValueFactory<>("tieneTV"));

        // Personalizo las columnas booleanas (tieneProyector y tieneTV)
        colTieneProyector.setCellFactory(column -> new TableCell<>() {
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

        colTieneTV.setCellFactory(column -> new TableCell<>() {
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
    }

    public void inicializarTablaLaboratorio(TableColumn<Laboratorio, Integer> colId,
                                                   TableColumn<Laboratorio, Integer> colNum,
                                                   TableColumn<Laboratorio, Integer> colCapacidad,
                                                   TableColumn<Laboratorio, Boolean> colTieneProyector,
                                                   TableColumn<Laboratorio, Boolean> colTieneTV,
                                                   TableColumn<Laboratorio, Integer> colComputadoras){
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colComputadoras.setCellValueFactory(new PropertyValueFactory<>("computadoras"));
        colNum.setCellValueFactory(new PropertyValueFactory<>("numero"));
        colCapacidad.setCellValueFactory(new PropertyValueFactory<>("capacidad"));
        colTieneProyector.setCellValueFactory(new PropertyValueFactory<>("tieneProyector"));
        colTieneTV.setCellValueFactory(new PropertyValueFactory<>("tieneTV"));

        // Personalizo las columnas booleanas (tieneProyector y tieneTV)
        colTieneProyector.setCellFactory(column -> new TableCell<>() {
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

        colTieneTV.setCellFactory(column -> new TableCell<>() {
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
    }
}
