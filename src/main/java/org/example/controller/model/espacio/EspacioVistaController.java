package org.example.controller.model.espacio;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Espacio;
import org.example.model.Laboratorio;
import org.example.utils.TableUtils;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Component
public class EspacioVistaController {
    @FXML
    private Pagination pagination;
    @FXML
    private TableView<Espacio> tblEspacios;
    @FXML
    private TableColumn<Espacio, Integer> colNum;
    @FXML
    private TableColumn<Espacio, Integer> colCapacidad;
    @FXML
    private TableColumn<Espacio, Boolean> colTieneProyector;
    @FXML
    private TableColumn<Espacio, Boolean> colTieneTV;
    @FXML
    private TableColumn<Laboratorio, Integer> colComputadoras;
    private static final int PAGE_SIZE = 10;
    private List<? extends Espacio> espacios;

    public void setEspacios(List<? extends Espacio> espacios) {
        this.espacios = espacios;

        int totalPages = (int) Math.ceil((double) espacios.size() / PAGE_SIZE);
        pagination.setPageCount(Math.max(totalPages, 1));

        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> cargarPagina(newIndex.intValue()));

        cargarPagina(0);
    }

    private void cargarPagina(int pageIndex) {
        int fromIndex = pageIndex * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, espacios.size());

        ObservableList<Espacio> espacioObservableList = FXCollections.observableArrayList();
        espacioObservableList.addAll(espacios.subList(fromIndex, toIndex));

        tblEspacios.setItems(espacioObservableList);
    }

    @FXML
    public void initialize() {
        TableUtils.inicializarTablaEspacio(colNum, colCapacidad, colTieneProyector, colTieneTV, colComputadoras);
    }
}
