package org.example.controller.menus;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.controller.model.espacio.EliminarEspacioVistaController;
import org.example.controller.model.espacio.editar.SeleccionarEspacioVistaController;
import org.example.exception.GlobalExceptionHandler;
import org.example.exception.JsonNotFoundException;
import org.example.service.AulaService;
import org.example.utils.VistaUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class MenuEspacioVistaController {
    private final VistaUtils vistaUtils;
    private final AulaService aulaService;
    private final GlobalExceptionHandler globalExceptionHandler;
    @FXML
    private Button btnListar;
    @FXML
    private Button btnCrear;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnVolver;

    @FXML
    public void menuListar(ActionEvent actionEvent) {
        try{
            vistaUtils.cargarVista("/org/example/view/menus/menu-listar-espacios-view.fxml");
            vistaUtils.cerrarVentana(this.btnVolver);
        }catch (IOException e){
            log.error(e.getMessage());
        }
    }

    @FXML
    public void crearEspacio(ActionEvent actionEvent) {
        try{
            vistaUtils.cargarVista("/org/example/view/model/espacio/crear-espacio-view.fxml");
        }catch (IOException e){
            log.error(e.getMessage());
        }
    }

    @FXML
    public void editarEspacio(ActionEvent actionEvent) {
        try {
            var espacios = aulaService.listar();
            vistaUtils.cargarVista("/org/example/view/model/espacio/editar/seleccionar-espacio-view.fxml",
                    (SeleccionarEspacioVistaController controller) ->
                            controller.setEspacios(espacios));
        }catch (IOException e){
            log.error(e.getMessage());
        } catch (JsonNotFoundException e){
            globalExceptionHandler.handleJsonNotFoundException(e);
        }
    }

    @FXML
    public void eliminarEspacio(ActionEvent actionEvent) {
        try {
            var espacios = aulaService.listar();
            vistaUtils.cargarVista("/org/example/view/model/espacio/eliminar-espacio-view.fxml",
                    (EliminarEspacioVistaController controller) ->
                            controller.setEspacios(espacios));
        } catch (IOException e) {
            log.error(e.getMessage());
        } catch (JsonNotFoundException e) {
            globalExceptionHandler.handleJsonNotFoundException(e);
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
