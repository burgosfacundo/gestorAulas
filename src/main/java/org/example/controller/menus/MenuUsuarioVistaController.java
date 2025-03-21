package org.example.controller.menus;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.controller.model.usuario.UsuarioVistaController;
import org.example.exception.GlobalExceptionHandler;
import org.example.exception.JsonNotFoundException;
import org.example.exception.NotFoundException;
import org.example.service.UsuarioService;
import org.example.utils.VistaUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class MenuUsuarioVistaController {
    private final VistaUtils vistaUtils;
    private final UsuarioService usuarioService;
    private final GlobalExceptionHandler globalExceptionHandler;
    @FXML
    private Button btnListar;
    @FXML
    private Button btnCrear;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnVolver;

    @FXML
    public void listar(ActionEvent event) {
       try{
           var usuarios = usuarioService.listar();
           vistaUtils.cargarVista("/org/example/view/model/usuario/usuario-view.fxml",
                   (UsuarioVistaController controller) -> controller.setUsuarios(usuarios));
       }catch (IOException e){
           log.error(e.getMessage());
       } catch (NotFoundException e) {
           globalExceptionHandler.handleNotFoundException(e);
       } catch (JsonNotFoundException e) {
           globalExceptionHandler.handleJsonNotFoundException(e);
       }
    }
    @FXML
    public void crearUsuario(ActionEvent actionEvent) {
        try{
            vistaUtils.cargarVista("/org/example/view/model/usuario/crear-usuario-view.fxml");
        }catch (IOException e){
            log.error(e.getMessage());
        }
    }
    @FXML
    public void eliminarUsuario(ActionEvent actionEvent) {
        try {
            vistaUtils.cargarVista("/org/example/view/model/usuario/eliminar-usuario-view.fxml");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    @FXML
    public void volverMenuPrincipal(ActionEvent actionEvent) {
        try{
            vistaUtils.cargarVista("/org/example/view/menus/menu-administrador-view.fxml");
        }catch (IOException e){
            log.error(e.getMessage());
        }
        vistaUtils.cerrarVentana(this.btnVolver);
    }
}
