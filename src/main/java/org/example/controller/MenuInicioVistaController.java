package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.exception.AutenticacionException;
import org.example.exception.JsonNotFoundException;
import org.example.exception.NotFoundException;
import org.example.model.Usuario;
import org.example.security.Seguridad;
import org.example.utils.VistaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.logging.Logger;

@Controller
public class MenuInicioVistaController {
    private static final Logger logger = Logger.getLogger(MenuInicioVistaController.class.getName());
    private final VistaUtils vistaUtils;
    private final Seguridad seguridad;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Button btnLogin;

    @Autowired
    public MenuInicioVistaController(VistaUtils vistaUtils, Seguridad seguridad) {
        this.vistaUtils = vistaUtils;
        this.seguridad = seguridad;
    }

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
            logger.severe("Usuario no encontrado");
            return;
        }
        String rol = usuario.getRol().getNombre().toLowerCase();
        switch (rol) {
            case "administrador" -> System.out.println("Menu admin");
            case "profesor" -> {
                try{
                    vistaUtils.cargarVista("/org/example/view/profesor/menu-profesor-view.fxml",usuario);
                }catch (IOException e){
                    logger.severe(e.getMessage());
                }
                vistaUtils.cerrarVentana(btnLogin);
            }
            default -> logger.severe("Rol no encontrado");
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
        } catch (AutenticacionException | JsonNotFoundException | NotFoundException e) {
            vistaUtils.mostrarAlerta("Error:",e.getMessage(), Alert.AlertType.ERROR);
        }
        return usuario;
    }

}

