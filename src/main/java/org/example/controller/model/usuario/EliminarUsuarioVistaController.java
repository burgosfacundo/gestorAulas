package org.example.controller.model.usuario;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.GlobalExceptionHandler;
import org.example.exception.JsonNotFoundException;
import org.example.exception.NotFoundException;
import org.example.model.*;
import org.example.service.UsuarioService;
import org.example.utils.TableUtils;
import org.example.utils.VistaUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class EliminarUsuarioVistaController {
    private final VistaUtils vistaUtils;
    private final UsuarioService usuarioService;
    private final GlobalExceptionHandler globalExceptionHandler;
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
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnCancelar;
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
        // Deshabilitar botón si no hay selección
        btnEliminar.disableProperty().bind(tblUsuarios.getSelectionModel().selectedItemProperty().isNull());
    }

    @FXML
    public void eliminar(ActionEvent actionEvent) {
        Usuario seleccionada = tblUsuarios.getSelectionModel().getSelectedItem();
        Optional
                .ofNullable(seleccionada)
                .ifPresent(usuario ->{
                    try {
                        vistaUtils.mostrarAlerta("Estas seguro?", Alert.AlertType.CONFIRMATION);
                        usuarioService.eliminar(usuario.getId());
                        vistaUtils.mostrarAlerta("Usuario eliminado correctamente", Alert.AlertType.INFORMATION);
                        vistaUtils.cerrarVentana(btnEliminar);
                    } catch (JsonNotFoundException e) {
                        globalExceptionHandler.handleJsonNotFoundException(e);
                    } catch (NotFoundException e) {
                        globalExceptionHandler.handleNotFoundException(e);
                    }
                });

    }

    @FXML
    public void cancelar(ActionEvent actionEvent) {
        vistaUtils.cerrarVentana(btnCancelar);
    }
}
