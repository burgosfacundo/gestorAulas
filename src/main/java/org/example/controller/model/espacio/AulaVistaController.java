package org.example.controller.model.espacio;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Aula;
import org.example.utils.TableUtils;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Component
public class AulaVistaController{
    @FXML
    private TableView<Aula> tblAulas;
    @FXML
    private TableColumn<Aula,Integer> colId;
    @FXML
    private TableColumn<Aula,Integer> colNum;
    @FXML
    private TableColumn<Aula,Integer> colCapacidad;
    @FXML
    private TableColumn<Aula,Boolean> colTieneProyector;
    @FXML
    private TableColumn<Aula,Boolean> colTieneTV;
    private List<Aula> aulas;


    public void setAulas(List<Aula> aulas) {
        this.aulas = aulas;
        actualizarTabla();
    }

    private void actualizarTabla() {
        // Configurar la tabla si no se ha hecho antes
        if (tblAulas.getColumns().isEmpty()) {
            TableUtils.inicializarTablaAula(colId,colNum,colCapacidad,colTieneProyector,colTieneTV);
        }

        ObservableList<Aula> aulaObservableList = FXCollections.observableArrayList();
        aulaObservableList.addAll(aulas);
        tblAulas.setItems(aulaObservableList);
    }

    @FXML
    public void initialize(){
        TableUtils.inicializarTablaAula(colId,colNum,colCapacidad,colTieneProyector,colTieneTV);
    }

}
