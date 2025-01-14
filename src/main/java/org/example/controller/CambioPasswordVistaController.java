package org.example.controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.exception.JsonNotFoundException;
import org.example.exception.NotFoundException;
import org.example.model.Usuario;
import org.example.security.SesionActual;
import org.example.service.UsuarioService;
import org.example.utils.VistaUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class CambioPasswordVistaController {
    private Usuario usuarioActual;
    private final SesionActual sesionActual;
    private final VistaUtils vistaUtils;
    private final UsuarioService usuarioService;
    @FXML
    private Button btnCambiar;
    @FXML
    private TextField actualPassword;
    @FXML
    private PasswordField newPassword;
    @FXML
    private PasswordField repeatPassword;

    @Autowired
    public CambioPasswordVistaController(SesionActual sesionActual, VistaUtils vistaUtils, UsuarioService usuarioService) {
        this.sesionActual = sesionActual;
        this.vistaUtils = vistaUtils;
        this.usuarioService = usuarioService;
    }

    @FXML
    public void initialize() {
        this.usuarioActual = sesionActual.getUsuarioActual();
        if (usuarioActual == null) {
            vistaUtils.mostrarAlerta(
                    "Error",
                    "No hay un usuario logueado. Cierra y vuelve a iniciar sesión.",
                    Alert.AlertType.ERROR
            );
            vistaUtils.cerrarVentana(btnCambiar);
        }
    }

    @FXML
    public void cambiarPassword(ActionEvent actionEvent) {
        try{
            List<String> errores = new ArrayList<>();
            var user = usuarioService.obtener(this.usuarioActual.getId());

            if (!BCrypt.checkpw(actualPassword.getText(),user.getPassword())) {
                actualPassword.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                errores.add("La contraseña actual es incorrecta.");
            }else {
                actualPassword.setStyle("-fx-border-color: transparent;");
                if (newPassword.getText().isEmpty()) {
                    newPassword.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                    errores.add("La contraseña no puede estar vacía.");
                }else {
                    if (!newPassword.getText().equals(repeatPassword.getText())) {
                        newPassword.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                        repeatPassword.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                        errores.add("Las contraseñas no coinciden.");
                    }else {
                        newPassword.setStyle("-fx-border-color: transparent;");
                        repeatPassword.setStyle("-fx-border-color: transparent;");
                    }
                }
            }

            if (!errores.isEmpty()) {
                vistaUtils.mostrarAlerta(
                        "Error en el formulario",
                        String.join("\n", errores),
                        Alert.AlertType.ERROR);
                return;
            }

            user.setPassword(newPassword.getText());
            usuarioService.modificar(user);

            vistaUtils.mostrarAlerta(
                    "Éxito",
                    "La contraseña fue modificada correctamente",
                    Alert.AlertType.INFORMATION);
            vistaUtils.cerrarVentana(btnCambiar);
        } catch (NotFoundException | JsonNotFoundException e) {
            vistaUtils.mostrarAlerta(
                    "Error",
                    "Ocurrió un error al modificar la contraseña",
                    Alert.AlertType.ERROR);
        }
    }
}

