package org.example.controller.menus;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.controller.model.solicitud.SolicitudVistaController;
import org.example.enums.EstadoSolicitud;
import org.example.enums.Permisos;
import org.example.exception.GlobalExceptionHandler;
import org.example.security.Seguridad;
import org.example.security.SesionActual;
import org.example.utils.VistaUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class MenuSolicitudAdminVistaController {
    private final Seguridad seguridad;
    private final SesionActual sesionActual;
    private final VistaUtils vistaUtils;
    private final GlobalExceptionHandler globalExceptionHandler;
    @FXML
    private Button btnRevisar;
    @FXML
    private Button btnListarAprobadas;
    @FXML
    private Button btnListarRechazadas;
    @FXML
    private Button btnVolver;

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            Stage stage = (Stage) btnVolver.getScene().getWindow();
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
                vistaUtils.cerrarVentana(this.btnVolver);
            });
        });
    }

    @FXML
    public void revisarSolicitudes(ActionEvent actionEvent) {
        if (seguridad.verificarPermiso(sesionActual.getUsuario(), Permisos.GESTIONAR_CAMBIOS)){
            try{
                vistaUtils.cargarVista("/org/example/view/model/solicitud/revisar-solicitud-view.fxml");
            }catch (IOException e) {
                globalExceptionHandler.handleIOException(e);
            }
        }else {
            vistaUtils.mostrarAlerta("No tienes permisos para gestionar solicitudes", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void listarSolicitudesAprobadas(ActionEvent actionEvent) {
        if (seguridad.verificarPermiso(sesionActual.getUsuario(), Permisos.VER_SOLICITUDES_CAMBIO)){
            try {
                vistaUtils.cargarVista("/org/example/view/model/solicitud/solicitud-view.fxml",
                        (SolicitudVistaController controller) -> controller.setEstadoSolicitud(EstadoSolicitud.APROBADA));
            } catch (IOException e) {
                globalExceptionHandler.handleIOException(e);
            }
        }else {
            vistaUtils.mostrarAlerta("No tienes permisos para ver solicitudes", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void listarSolicitudesRechazadas(ActionEvent actionEvent) {
        if (seguridad.verificarPermiso(sesionActual.getUsuario(), Permisos.VER_SOLICITUDES_CAMBIO)){
            try {
                vistaUtils.cargarVista("/org/example/view/model/solicitud/solicitud-view.fxml",
                        (SolicitudVistaController controller) -> controller.setEstadoSolicitud(EstadoSolicitud.RECHAZADA));
            } catch (IOException e) {
                globalExceptionHandler.handleIOException(e);
            }
        }else {
            vistaUtils.mostrarAlerta("No tienes permisos para ver solicitudes", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void volverMenuPrincipal(ActionEvent actionEvent) {
        try {
            if (sesionActual.getUsuario().getRol().getNombre().equals("Administrador")) {
                vistaUtils.cargarVista("/org/example/view/menus/menu-administrador-view.fxml");
            }else if (sesionActual.getUsuario().getRol().getNombre().equals("Profesor")) {
                vistaUtils.cargarVista("/org/example/view/menus/menu-profesor-view.fxml");
            }

        } catch (IOException e) {
            globalExceptionHandler.handleIOException(e);
        }
        vistaUtils.cerrarVentana(this.btnVolver);
    }
}
