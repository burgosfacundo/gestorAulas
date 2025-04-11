package org.example.controller.menus;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.enums.Permisos;
import org.example.exception.GlobalExceptionHandler;
import org.example.security.Seguridad;
import org.example.security.SesionActual;
import org.example.utils.VistaUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class MenuEspacioVistaController {
    private final Seguridad seguridad;
    private final SesionActual sesionActual;
    private final VistaUtils vistaUtils;
    private final GlobalExceptionHandler globalExceptionHandler;
    @FXML
    private Button btnListar;
    @FXML
    private Button btnCrear;
    @FXML
    private Button btnEditar;
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
    public void menuListar(ActionEvent actionEvent) {
        if (seguridad.verificarPermiso(sesionActual.getUsuario(), Permisos.VER_ESPACIOS)){
            try{
                vistaUtils.cargarVista("/org/example/view/menus/menu-listar-espacios-view.fxml");
                vistaUtils.cerrarVentana(this.btnVolver);
            }catch (IOException e){
                globalExceptionHandler.handleIOException(e);
            }
        }else {
            vistaUtils.mostrarAlerta("No tienes permisos para ver los espacios", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void crearEspacio(ActionEvent actionEvent) {
        if (seguridad.verificarPermiso(sesionActual.getUsuario(), Permisos.CREAR_ESPACIO)){
            try{
                vistaUtils.cargarVista("/org/example/view/model/espacio/crear-espacio-view.fxml");
            }catch (IOException e){
                globalExceptionHandler.handleIOException(e);
            }
        }else{
            vistaUtils.mostrarAlerta("No tienes permisos para crear espacios", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void editarEspacio(ActionEvent actionEvent) {
        if (seguridad.verificarPermiso(sesionActual.getUsuario(), Permisos.MODIFICAR_ESPACIO)) {
            try {
                vistaUtils.cargarVista("/org/example/view/model/espacio/editar/seleccionar-espacio-view.fxml");
            } catch (IOException e) {
                globalExceptionHandler.handleIOException(e);
            }
        }else{
            vistaUtils.mostrarAlerta("No tienes permisos para editar espacios", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void eliminarEspacio(ActionEvent actionEvent) {
        if (seguridad.verificarPermiso(sesionActual.getUsuario(), Permisos.ELIMINAR_ESPACIO)) {
            try {
                vistaUtils.cargarVista("/org/example/view/model/espacio/eliminar-espacio-view.fxml");
            } catch (IOException e) {
                globalExceptionHandler.handleIOException(e);
            }
        }else {
            vistaUtils.mostrarAlerta("No tienes permisos para eliminar espacios", Alert.AlertType.ERROR);
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
