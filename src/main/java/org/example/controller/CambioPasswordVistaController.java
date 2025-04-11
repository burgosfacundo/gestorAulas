package org.example.controller;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.GlobalExceptionHandler;
import org.example.exception.NotFoundException;
import org.example.model.Usuario;
import org.example.model.dto.UsuarioDTO;
import org.example.security.SesionActual;
import org.example.service.UsuarioService;
import org.example.utils.VistaUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class CambioPasswordVistaController {
    private final GlobalExceptionHandler globalExceptionHandler;
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

        Platform.runLater(() -> {
            Stage stage = (Stage) btnCambiar.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                try {
                    if (sesionActual.getUsuario().getRol().getNombre().equals("Administrador")) {
                        vistaUtils.cargarVista("/org/example/view/menus/menu-administrador-view.fxml");
                    }else if (sesionActual.getUsuario().getRol().getNombre().equals("Profesor")) {
                        vistaUtils.cargarVista("/org/example/view/menus/menu-profesor-view.fxml");
                    }
                } catch (IOException e) {
                    globalExceptionHandler.handleIOException(e);
                }
                vistaUtils.cerrarVentana(this.btnCambiar);
            });
        });
    }

    @FXML
    public void cambiarPassword(ActionEvent actionEvent) {
        try{
            List<String> errores = new ArrayList<>();
            var user = usuarioService.obtener(this.usuarioActual.getId());

            if (!BCrypt.checkpw(actualPassword.getText(),user.password())) {
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

            UsuarioDTO modificado = new UsuarioDTO(usuarioActual.getId(),usuarioActual.getUsername(),
                    newPassword.getText(),usuarioActual.getRol().getId(),usuarioActual.getProfesor().getId());
            usuarioService.modificar(modificado);

            vistaUtils.mostrarAlerta(
                    "La contraseña fue modificada correctamente",
                    Alert.AlertType.INFORMATION);

            if (sesionActual.getUsuario().getRol().getNombre().equals("Administrador")) {
                vistaUtils.cargarVista("/org/example/view/menus/menu-administrador-view.fxml");
            }else if (sesionActual.getUsuario().getRol().getNombre().equals("Profesor")) {
                vistaUtils.cargarVista("/org/example/view/menus/menu-profesor-view.fxml");
            }

            vistaUtils.cerrarVentana(btnCambiar);
        } catch (NotFoundException e) {
            vistaUtils.mostrarAlerta(
                    "Ocurrió un error al modificar la contraseña",
                    Alert.AlertType.ERROR);
        }catch (IOException e){
            globalExceptionHandler.handleIOException(e);
        }
    }
}

