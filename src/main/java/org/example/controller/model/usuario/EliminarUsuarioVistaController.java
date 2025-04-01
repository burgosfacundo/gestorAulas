package org.example.controller.model.usuario;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.GlobalExceptionHandler;
import org.example.exception.NotFoundException;
import org.example.model.*;
import org.example.service.ProfesorService;
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
    private final ProfesorService profesorService;
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
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnCancelar;
    @FXML
    private Pagination pagination;
    private List<Usuario> usuarios;
    private static final int PAGE_SIZE = 10;

    @FXML
    public void initialize() {
        // Asocio columnas de la tabla con atributos del modelo
        TableUtils.inicializarTablaUsuarios(colUsername,colNombre,colApellido,colMatricula);
        btnEliminar.disableProperty().bind(tblUsuarios.getSelectionModel().selectedItemProperty().isNull());

        usuarios = usuarioService.listar();
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
    public void eliminar(ActionEvent actionEvent) {
        Usuario seleccionada = tblUsuarios.getSelectionModel().getSelectedItem();
        Optional
                .ofNullable(seleccionada)
                .ifPresent(usuario ->{
                    try {
                        var result = vistaUtils.mostrarAlerta("Estas seguro?", Alert.AlertType.CONFIRMATION);

                        if (result == ButtonType.OK) {
                            usuarioService.eliminar(usuario.getId());
                            vistaUtils.mostrarAlerta("Usuario eliminado correctamente", Alert.AlertType.INFORMATION);
                            vistaUtils.cerrarVentana(btnEliminar);
                        }
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
