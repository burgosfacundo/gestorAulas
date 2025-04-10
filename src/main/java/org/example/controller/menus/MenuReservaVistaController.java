package org.example.controller.menus;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.controller.model.reserva.ReservaVistaController;
import org.example.enums.Permisos;
import org.example.exception.GlobalExceptionHandler;
import org.example.security.Seguridad;
import org.example.security.SesionActual;
import org.example.service.ReservaService;
import org.example.utils.VistaUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class MenuReservaVistaController {
    private final Seguridad seguridad;
    private final SesionActual sesionActual;
    private final VistaUtils vistaUtils;
    private final ReservaService reservaService;
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
        if (seguridad.verificarPermiso(sesionActual.getUsuario(), Permisos.VER_RESERVAS)){
            try{
                vistaUtils.cargarVista("/org/example/view/model/reserva/reserva-view.fxml",
                        (ReservaVistaController controller) -> controller.setReservas(reservaService.listar()));
            }catch (IOException e){
                globalExceptionHandler.handleIOException(e);
            }
        }else {
            vistaUtils.mostrarAlerta("No tienes permisos para ver reservas", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void crear(ActionEvent actionEvent) {
        if (seguridad.verificarPermiso(sesionActual.getUsuario(), Permisos.CREAR_RESERVA)){
            try{
                vistaUtils.cargarVista("/org/example/view/model/reserva/crear/seleccionar-inscripcion-view.fxml");
            }catch (IOException e){
                log.error(e.getMessage());
            }
        }else {
            vistaUtils.mostrarAlerta("No tienes permisos para crear reservas", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void eliminar(ActionEvent actionEvent) {
        if (seguridad.verificarPermiso(sesionActual.getUsuario(), Permisos.ELIMINAR_RESERVA)){
            try {
                vistaUtils.cargarVista("/org/example/view/model/reserva/eliminar-reserva-view.fxml");
            } catch (IOException e) {
                globalExceptionHandler.handleIOException(e);
            }
        }else{
            vistaUtils.mostrarAlerta("No tienes permisos para eliminar reservas", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void volverMenuPrincipal(ActionEvent actionEvent) {
        try{
            vistaUtils.cargarVista("/org/example/view/menus/menu-administrador-view.fxml");
        }catch (IOException e){
            globalExceptionHandler.handleIOException(e);
        }
        vistaUtils.cerrarVentana(this.btnVolver);
    }
}
