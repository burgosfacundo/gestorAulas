package org.example.controller.model.usuario;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
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
    private TableColumn<Usuario,Integer> colId;
    @FXML
    private TableColumn<Usuario,String> colUsername;
    @FXML
    private TableColumn<Usuario, String> colNombre;
    @FXML
    private TableColumn<Usuario, String> colApellido;
    @FXML
    private TableColumn<Usuario,String> colMatricula;
    private List<Usuario> usuarios;

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
        actualizarTabla();
    }

    private void actualizarTabla() {
        // Configurar la tabla si no se ha hecho antes
        if (tblUsuarios.getColumns().isEmpty()) {
            TableUtils.inicializarTablaUsuarios(colId, colUsername,colNombre,colApellido,colMatricula);
        }

        ObservableList<Usuario> usuarioObservableList = FXCollections.observableArrayList();
        usuarioObservableList.addAll(usuarios);
        tblUsuarios.setItems(usuarioObservableList);
    }

    @FXML
    public void initialize() {
        // Asocio columnas de la tabla con atributos del modelo
        TableUtils.inicializarTablaUsuarios(colId, colUsername,colNombre,colApellido,colMatricula);
    }
}
