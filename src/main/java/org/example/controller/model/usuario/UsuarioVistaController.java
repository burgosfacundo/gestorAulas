package org.example.controller.model.usuario;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Usuario;
import org.example.utils.TableUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class UsuarioVistaController {
    @FXML
    private TableView<Usuario> tblUsuarios;
    @FXML
    private TableColumn<Usuario,String> colUsername;
    @FXML
    private TableColumn<Usuario, String> colNombre;
    @FXML
    private TableColumn<Usuario, String> colApellido;
    @FXML
    private TableColumn<Usuario,String> colMatricula;
    private List<Usuario> usuarios;
    @FXML
    private Pagination pagination;
    private static final int PAGE_SIZE = 10;

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
        int totalPages = (int) Math.ceil((double) usuarios.size() / PAGE_SIZE);
        pagination.setPageCount(Math.max(totalPages, 1));

        pagination.currentPageIndexProperty().addListener(
                (obs, oldIndex, newIndex) -> cargarPagina(newIndex.intValue()));
        cargarPagina(0);
    }

    private void cargarPagina(int pageIndex) {
        int fromIndex = pageIndex * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, usuarios.size());

        ObservableList<Usuario> usuariosObservableList = FXCollections.observableArrayList();
        usuariosObservableList.addAll(usuarios.subList(fromIndex, toIndex));
        tblUsuarios.setItems(usuariosObservableList);
    }

    @FXML
    public void initialize() {
        TableUtils.inicializarTablaUsuarios(colUsername,colNombre,colApellido,colMatricula);
    }
}
