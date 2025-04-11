package org.example.controller.menus;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.controller.model.reserva.ReservaVistaController;
import org.example.enums.Permisos;
import org.example.exception.GlobalExceptionHandler;
import org.example.exception.NotFoundException;
import org.example.security.Seguridad;
import org.example.security.SesionActual;
import org.example.service.ReservaService;
import org.example.utils.VistaUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Slf4j
@RequiredArgsConstructor
@Component
public class MenuProfesorVistaController {
    private final Seguridad seguridad;
    private final SesionActual sesionActual;
    private final VistaUtils vistaUtils;
    private final GlobalExceptionHandler globalExceptionHandler;
    @FXML
    private Button btnListarEspacios;
    @FXML
    private Button btnSolicitarCambio;
    @FXML
    private Button btnListarReservas;
    @FXML
    private Button btnCambiarcontrasenia;
    @FXML
    private Button btnCerrarSesion;
    private final ReservaService reservaService;

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                event.consume(); // Previene el cierre hasta que el usuario confirme
                vistaUtils.confirmarCierre();
            });
        });
    }

    @FXML
    public void menuListarEspacios(ActionEvent actionEvent) {
        if (seguridad.verificarPermiso(sesionActual.getUsuario(), Permisos.VER_ESPACIOS)){
            try{
                vistaUtils.cargarVista("/org/example/view/menus/menu-listar-espacios-view.fxml");
                vistaUtils.cerrarVentana(this.btnCerrarSesion);
            }catch (IOException e){
                globalExceptionHandler.handleIOException(e);
            }
        }else{
            vistaUtils.mostrarAlerta("No tienes permisos para ver los espacios", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void menuSolicitudes(ActionEvent actionEvent) {
        try{
            vistaUtils.cargarVista("/org/example/view/menus/menu-solicitudes-profesor-view.fxml");
            vistaUtils.cerrarVentana(this.btnCerrarSesion);
        }catch (IOException e){
            globalExceptionHandler.handleIOException(e);
        }
    }

    @FXML
    public void listarMisReservas(ActionEvent actionEvent) {
        if (seguridad.verificarPermiso(sesionActual.getUsuario(),Permisos.VER_RESERVAS)){
            try {
                var idProfesor = sesionActual.getUsuario().getProfesor().getId();
                var reservas = reservaService.listarReservasPorProfesor(idProfesor);
                vistaUtils.cargarVista("/org/example/view/model/reserva/reserva-view.fxml",
                        (ReservaVistaController controller) -> controller.setReservas(reservas));
            } catch (NotFoundException e) {
                globalExceptionHandler.handleNotFoundException(e);
            }catch (IOException e){
                globalExceptionHandler.handleIOException(e);
            }
        }else {
            vistaUtils.mostrarAlerta("No tienes permisos para ver las reservas", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void cambiarPassword(ActionEvent actionEvent) {
        if (seguridad.verificarPermiso(sesionActual.getUsuario(),Permisos.CAMBIAR_PASSWORD)){
            try {
                vistaUtils.cargarVista("/org/example/view/cambio-password-view.fxml");
                vistaUtils.cerrarVentana(this.btnCerrarSesion);
            } catch (IOException e) {
                globalExceptionHandler.handleIOException(e);
            }
        }else {
            vistaUtils.mostrarAlerta("No tienes permisos para cambiar la contraseña", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void cerrarWindow(ActionEvent actionEvent) {
        vistaUtils.confirmarCierre();
    }
}
