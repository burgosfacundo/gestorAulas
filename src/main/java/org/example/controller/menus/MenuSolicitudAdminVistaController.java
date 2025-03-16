package org.example.controller.menus;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.controller.model.solicitud.SolicitudVistaController;
import org.example.enums.EstadoSolicitud;
import org.example.security.SesionActual;
import org.example.utils.VistaUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class MenuSolicitudAdminVistaController {
    private final SesionActual sesionActual;
    private final VistaUtils vistaUtils;
    @FXML
    private Button btnRevisar;
    @FXML
    private Button btnListarAprobadas;
    @FXML
    private Button btnListarRechazadas;
    @FXML
    private Button btnVolver;

    @FXML
    public void revisarSolicitudes(ActionEvent actionEvent) {
        try{
            vistaUtils.cargarVista("/org/example/view/model/solicitud/revisar-solicitud-view.fxml");
        }catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @FXML
    public void listarSolicitudesAprobadas(ActionEvent actionEvent) {
        try {
            vistaUtils.cargarVista("/org/example/view/model/solicitud/solicitud-view.fxml",
                    (SolicitudVistaController controller) -> controller.setEstadoSolicitud(EstadoSolicitud.APROBADA));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @FXML
    public void listarSolicitudesRechazadas(ActionEvent actionEvent) {
        try {
            vistaUtils.cargarVista("/org/example/view/model/solicitud/solicitud-view.fxml",
                    (SolicitudVistaController controller) -> controller.setEstadoSolicitud(EstadoSolicitud.RECHAZADA));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @FXML
    public void volverMenuPrincipal(ActionEvent actionEvent) {
        try {
            if (sesionActual.getUsuario().getRol().getNombre().equals("Administrador")) {
                vistaUtils.cargarVista("/org/example/view/menus/menu-admin-view.fxml");
            }else if (sesionActual.getUsuario().getRol().getNombre().equals("Profesor")) {
                vistaUtils.cargarVista("/org/example/view/menus/menu-profesor-view.fxml");
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
        vistaUtils.cerrarVentana(this.btnVolver);
    }
}
