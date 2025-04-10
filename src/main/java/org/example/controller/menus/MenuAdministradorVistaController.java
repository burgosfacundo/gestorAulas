package org.example.controller.menus;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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
public class MenuAdministradorVistaController {
    private final Seguridad seguridad;
    private final SesionActual sesionActual;
    private final VistaUtils vistaUtils;
    private final GlobalExceptionHandler globalExceptionHandler;
    @FXML
    private Button btnMenuEspacios;
    @FXML
    private Button btnSolicitarCambio;
    @FXML
    private Button btnMenuReservas;
    @FXML
    private Button btnMenuUsuarios;
    @FXML
    private Button btnPassword;
    @FXML
    private Button btnCerrarSesion;

    @FXML
    public void menuEspacios(ActionEvent actionEvent) {
        try{
            vistaUtils.cargarVista("/org/example/view/menus/menu-espacios-view.fxml");
            cerrarWindow(actionEvent);
        }catch (IOException e){
            globalExceptionHandler.handleIOException(e);
        }
    }

    @FXML
    public void menuSolicitudes(ActionEvent actionEvent) {
        try{
            vistaUtils.cargarVista("/org/example/view/menus/menu-solicitudes-admin-view.fxml");
            cerrarWindow(actionEvent);
        }catch (IOException e){
            globalExceptionHandler.handleIOException(e);
        }
    }

    @FXML
    public void menuReservas(ActionEvent actionEvent) {
        try{
            vistaUtils.cargarVista("/org/example/view/menus/menu-reservas-view.fxml");
            cerrarWindow(actionEvent);
        }catch (IOException e){
            globalExceptionHandler.handleIOException(e);
        }
    }

    @FXML
    public void menuUsuarios(ActionEvent actionEvent) {
        try{
            vistaUtils.cargarVista("/org/example/view/menus/menu-usuarios-view.fxml");
            cerrarWindow(actionEvent);
        }catch (IOException e){
            globalExceptionHandler.handleIOException(e);
        }
    }

    @FXML
    public void cambiarPassword(ActionEvent actionEvent) {
        if (seguridad.verificarPermiso(sesionActual.getUsuario(),Permisos.CAMBIAR_PASSWORD)){
            try {
                vistaUtils.cargarVista("/org/example/view/cambio-password-view.fxml");
            } catch (IOException e) {
                globalExceptionHandler.handleIOException(e);
            }
        }else {
            vistaUtils.mostrarAlerta("No tienes permisos para cambiar la contraseña", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void cerrarWindow(ActionEvent actionEvent) {
        vistaUtils.cerrarVentana(this.btnCerrarSesion);
    }

}
