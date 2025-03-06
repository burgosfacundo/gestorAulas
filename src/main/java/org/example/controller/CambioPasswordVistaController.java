package org.example.controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.JsonNotFoundException;
import org.example.exception.NotFoundException;
import org.example.model.Usuario;
import org.example.security.SesionActual;
import org.example.service.UsuarioService;
import org.example.utils.VistaUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
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

    @FXML
    public void initialize() {
        this.usuarioActual = sesionActual.getUsuario();
        if (usuarioActual == null) {
            vistaUtils.mostrarAlerta(
                    "No hay un usuario logueado. Cierra y vuelve a iniciar sesión.",
                    Alert.AlertType.ERROR
            );
            vistaUtils.cerrarVentana(btnCambiar);
            log.error("No hay un usuario logueado.");
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
                        String.join("\n", errores),
                        Alert.AlertType.ERROR);
                return;
            }

            user.setPassword(newPassword.getText());
            usuarioService.modificar(user);

            vistaUtils.mostrarAlerta(
                    "La contraseña fue modificada correctamente",
                    Alert.AlertType.INFORMATION);
            vistaUtils.cerrarVentana(btnCambiar);
        } catch (NotFoundException | JsonNotFoundException e) {
            vistaUtils.mostrarAlerta(
                    "Ocurrió un error al modificar la contraseña",
                    Alert.AlertType.ERROR);
            log.error(e.getMessage());
        }
    }
}

