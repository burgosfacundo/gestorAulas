package org.example.controller.menus;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.controller.model.reserva.ReservaVistaController;
import org.example.service.ReservaService;
import org.example.utils.VistaUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class MenuReservaVistaController {
    private final VistaUtils vistaUtils;
    private final ReservaService reservaService;
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
           vistaUtils.cargarVista("/org/example/view/model/reserva/reserva-view.fxml",
                   (ReservaVistaController controller) -> controller.setReservas(reservaService.listar()));
       }catch (IOException e){
           log.error(e.getMessage());
       }
    }
    @FXML
    public void crear(ActionEvent actionEvent) {
        try{
            vistaUtils.cargarVista("/org/example/view/model/reserva/crear/seleccionar-inscripcion-view.fxml");
        }catch (IOException e){
            log.error(e.getMessage());
        }
    }
    @FXML
    public void eliminar(ActionEvent actionEvent) {
        try {
            vistaUtils.cargarVista("/org/example/view/model/reserva/eliminar-reserva-view.fxml");
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
