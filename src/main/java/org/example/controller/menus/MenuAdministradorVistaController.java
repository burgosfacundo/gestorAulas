package org.example.controller.menus;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.utils.VistaUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class MenuAdministradorVistaController {
    private final VistaUtils vistaUtils;
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
            log.error(e.getMessage());
        }
    }

    @FXML
    public void menuSolicitudes(ActionEvent actionEvent) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @FXML
    public void menuReservas(ActionEvent actionEvent) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @FXML
    public void menuUsuarios(ActionEvent actionEvent) {
        throw new UnsupportedOperationException("Not supported yet.");
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
