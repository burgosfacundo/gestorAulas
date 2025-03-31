package org.example.controller.menus;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.Button;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.controller.model.reserva.ReservaVistaController;
import org.example.exception.GlobalExceptionHandler;
import org.example.exception.NotFoundException;
import org.example.security.SesionActual;
import org.example.service.ReservaService;
import org.example.utils.VistaUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Slf4j
@RequiredArgsConstructor
@Component
public class MenuProfesorVistaController {
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
    private final SesionActual sesionActual;

    @FXML
    public void menuListarEspacios(ActionEvent actionEvent) {
        try{
            vistaUtils.cargarVista("/org/example/view/menus/menu-listar-espacios-view.fxml");
            cerrarWindow(actionEvent);
        }catch (IOException e){
            log.error(e.getMessage());
        }
    }

    @FXML
    public void menuSolicitudes(ActionEvent actionEvent) {
        try{
            vistaUtils.cargarVista("/org/example/view/menus/menu-solicitudes-profesor-view.fxml");
            cerrarWindow(actionEvent);
        }catch (IOException e){
            log.error(e.getMessage());
        }
    }

    @FXML
    public void listarMisReservas(ActionEvent actionEvent) {
        try {
            var idProfesor = sesionActual.getUsuario().getProfesor().getId();
            var reservas = reservaService.listarReservasPorProfesor(idProfesor);
            vistaUtils.cargarVista("/org/example/view/model/reserva/reserva-view.fxml",
                    (ReservaVistaController controller) -> controller.setReservas(reservas));
        } catch (NotFoundException e) {
            globalExceptionHandler.handleNotFoundException(e);
        }catch (IOException e){
            log.error(e.getMessage());
        }
    }

    @FXML
    public void cambiarPassword(ActionEvent actionEvent) {
        try {
            vistaUtils.cargarVista("/org/example/view/cambio-password-view.fxml");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @FXML
    public void cerrarWindow(ActionEvent actionEvent) {
        vistaUtils.cerrarVentana(this.btnCerrarSesion);
    }
}
