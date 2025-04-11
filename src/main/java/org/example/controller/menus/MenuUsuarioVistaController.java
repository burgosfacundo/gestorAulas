package org.example.controller.menus;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.controller.model.usuario.UsuarioVistaController;
import org.example.enums.Permisos;
import org.example.exception.GlobalExceptionHandler;
import org.example.security.Seguridad;
import org.example.security.SesionActual;
import org.example.service.UsuarioService;
import org.example.utils.VistaUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class MenuUsuarioVistaController {
    private final Seguridad seguridad;
    private final SesionActual sesionActual;
    private final VistaUtils vistaUtils;
    private final UsuarioService usuarioService;
    private final GlobalExceptionHandler globalExceptionHandler;
    @FXML
    private Button btnListar;
    @FXML
    private Button btnCrear;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnVolver;

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                try{
                    vistaUtils.cargarVista("/org/example/view/menus/menu-administrador-view.fxml");
                }catch (IOException e){
                    globalExceptionHandler.handleIOException(e);
                }
                vistaUtils.cerrarVentana(this.btnVolver);
            });
        });
    }

    @FXML
    public void listar(ActionEvent event) {
        if (seguridad.verificarPermiso(sesionActual.getUsuario(), Permisos.VER_USUARIOS)){
            try{
                vistaUtils.cargarVista("/org/example/view/model/usuario/usuario-view.fxml",
                        (UsuarioVistaController controller) -> controller.setUsuarios(usuarioService.listar()));
            }catch (IOException e) {
                globalExceptionHandler.handleIOException(e);
            }
        }else {
            vistaUtils.mostrarAlerta("No tienes permisos para ver los usuarios", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void crearUsuario(ActionEvent actionEvent) {
        if (seguridad.verificarPermiso(sesionActual.getUsuario(), Permisos.CREAR_USUARIO)){
            try{
                vistaUtils.cargarVista("/org/example/view/model/usuario/crear-usuario-view.fxml");
            }catch (IOException e){
                globalExceptionHandler.handleIOException(e);
            }
        }else {
            vistaUtils.mostrarAlerta("No tienes permisos para crear un usuario", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void eliminarUsuario(ActionEvent actionEvent) {
        if (seguridad.verificarPermiso(sesionActual.getUsuario(), Permisos.ELIMINAR_USUARIO)){
            try {
                vistaUtils.cargarVista("/org/example/view/model/usuario/eliminar-usuario-view.fxml");
            } catch (IOException e) {
                globalExceptionHandler.handleIOException(e);
            }
        }else {
            vistaUtils.mostrarAlerta("No tienes permisos para eliminar un usuario", Alert.AlertType.ERROR);
        }
    }
    @FXML
    public void volverMenuPrincipal(ActionEvent actionEvent) {
        try{
            vistaUtils.cargarVista("/org/example/view/menus/menu-administrador-view.fxml");
        }catch (IOException e){
            globalExceptionHandler.handleIOException(e);
        }
        vistaUtils.cerrarVentana(this.btnVolver);
    }
}
