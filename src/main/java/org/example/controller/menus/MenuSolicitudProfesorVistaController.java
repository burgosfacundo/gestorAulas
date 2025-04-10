package org.example.controller.menus;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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
public class MenuSolicitudProfesorVistaController {
    private final Seguridad seguridad;
    private final SesionActual sesionActual;
    private final VistaUtils vistaUtils;
    private final GlobalExceptionHandler globalExceptionHandler;
    @FXML
    private Button btnCrear;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnSolicitudesPendientes;
    @FXML
    private Button btnSolicitudesAprobadas;
    @FXML
    private Button btnSolicitudesRechazadas;
    @FXML
    private Button btnVolver;

    @FXML
    public void crearSolicitud(ActionEvent actionEvent) {
        if (seguridad.verificarPermiso(sesionActual.getUsuario(), Permisos.SOLICITAR_CAMBIO)) {
            try {
                vistaUtils.cargarVista("/org/example/view/model/solicitud/crear/seleccionar-reserva-view.fxml");
            } catch (IOException e) {
                globalExceptionHandler.handleIOException(e);
            }
        }else {
            vistaUtils.mostrarAlerta("No tienes permisos para crear una solicitud", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void eliminarSolicitud(ActionEvent actionEvent) {
        if (seguridad.verificarPermiso(sesionActual.getUsuario(), Permisos.VER_SOLICITUDES_CAMBIO)) {
            try{
                vistaUtils.cargarVista("/org/example/view/model/solicitud/eliminar-solicitud-view.fxml");
            }catch (IOException e) {
                globalExceptionHandler.handleIOException(e);
            }
        }else {
            vistaUtils.mostrarAlerta("No tienes permisos para eliminar una solicitud", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void listarMisSolicitudesPendientes(ActionEvent actionEvent) {
        if (seguridad.verificarPermiso(sesionActual.getUsuario(), Permisos.VER_SOLICITUDES_CAMBIO)) {
            try {
                vistaUtils.cargarVista("/org/example/view/model/solicitud/solicitud-view.fxml",
                        (SolicitudVistaController controller) -> controller.setEstadoSolicitud(EstadoSolicitud.PENDIENTE));
            } catch (IOException e) {
                globalExceptionHandler.handleIOException(e);
            }
        }else {
            vistaUtils.mostrarAlerta("No tienes permisos para ver las solicitudes", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void listarMisSolicitudesAprobadas(ActionEvent actionEvent) {
        if (seguridad.verificarPermiso(sesionActual.getUsuario(), Permisos.VER_SOLICITUDES_CAMBIO)) {
            try {
                vistaUtils.cargarVista("/org/example/view/model/solicitud/solicitud-view.fxml",
                        (SolicitudVistaController controller) -> controller.setEstadoSolicitud(EstadoSolicitud.APROBADA));
            } catch (IOException e) {
                globalExceptionHandler.handleIOException(e);
            }
        }else {
            vistaUtils.mostrarAlerta("No tienes permisos para ver las solicitudes", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void listarMisSolicitudesRechazadas(ActionEvent actionEvent) {
        if (seguridad.verificarPermiso(sesionActual.getUsuario(), Permisos.VER_SOLICITUDES_CAMBIO)) {
            try {
                vistaUtils.cargarVista("/org/example/view/model/solicitud/solicitud-view.fxml",
                        (SolicitudVistaController controller) -> controller.setEstadoSolicitud(EstadoSolicitud.RECHAZADA));
            } catch (IOException e) {
                globalExceptionHandler.handleIOException(e);
            }
        }else {
            vistaUtils.mostrarAlerta("No tienes permisos para ver las solicitudes", Alert.AlertType.ERROR);
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
            globalExceptionHandler.handleIOException(e);
        }
        vistaUtils.cerrarVentana(this.btnVolver);
    }
}
