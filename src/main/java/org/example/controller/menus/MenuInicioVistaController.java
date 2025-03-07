package org.example.controller.menus;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.AutenticacionException;
import org.example.exception.JsonNotFoundException;
import org.example.exception.NotFoundException;
import org.example.model.Usuario;
import org.example.security.Seguridad;
import org.example.security.SesionActual;
import org.example.utils.VistaUtils;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MenuInicioVistaController {
    private final SesionActual sesionActual;
    private final VistaUtils vistaUtils;
    private final Seguridad seguridad;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Button btnLogin;

    /**
     * Maneja la autenticación de usuario
     * y derivar al menu que le corresponde
     */
    @FXML
    public void iniciarSesion(ActionEvent event) {
        Usuario usuario = autenticarUsuario();
        redireccionarSegunRol(usuario);
    }

    /**
     * Maneja la redirección al menú correspondiente según el rol del usuario
     * @param usuario Usuario autenticado
     */
    private void redireccionarSegunRol(Usuario usuario) {
        if (usuario == null || usuario.getRol() == null) {
            log.error("Usuario no encontrado");
            return;
        }
        String rol = usuario.getRol().getNombre().toLowerCase();
        switch (rol) {
            case "administrador" ->  {
                try{
                    vistaUtils.cargarVista("/org/example/view/menus/menu-administrador-view.fxml");
                }catch (IOException e){
                    log.error(e.getMessage());
                }
                vistaUtils.cerrarVentana(btnLogin);
            }
            case "profesor" -> {
                try{
                    vistaUtils.cargarVista("/org/example/view/menus/menu-profesor-view.fxml");
                }catch (IOException e){
                    log.error(e.getMessage());
                }
                vistaUtils.cerrarVentana(btnLogin);
            }
            default -> log.error("Rol no encontrado");
        }
    }


    /**
     * Autentica al Usuario
     * @return Usuario autenticado o null
     */
    private Usuario autenticarUsuario() {
        Usuario usuario = null;
        try {
            usuario = seguridad.autenticar(username.getText(), password.getText());
            sesionActual.setUsuario(usuario);
        } catch (AutenticacionException | JsonNotFoundException | NotFoundException e) {
            vistaUtils.mostrarAlerta(e.getMessage(), Alert.AlertType.ERROR);
        }
        return usuario;
    }
}

