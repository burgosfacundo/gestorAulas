package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.Button;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.enums.EstadoSolicitud;
import org.example.utils.VistaUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Slf4j
@RequiredArgsConstructor
@Component
public class MenuProfesorVistaController {
    private final VistaUtils vistaUtils;
    @FXML
    public Button btnListarEspacios;
    @FXML
    public Button btnSolicitarCambio;
    @FXML
    public Button btnListarReservas;
    @FXML
    public Button btnCambiarcontrasenia;
    @FXML
    public Button btnSolicitudesPendientes;
    @FXML
    public Button btnSolicitudesAprobadas;
    @FXML
    public Button btnSolicitudesRechazadas;
    @FXML
    public Button btnCerrarSesion;
    @FXML
    public Button btnListarLaboratorios ;
    @FXML
    public Button btnFiltrarAulas;
    @FXML
    public Button btnListarAulas;
    @FXML
    public Button btnFiltrarLaboratorios;
    @FXML
    public Button btnVolver;


    @FXML
    public void cerrarWindow(ActionEvent actionEvent) {
        vistaUtils.cerrarVentana(this.btnCerrarSesion);
    }

    @FXML
    public void menuListarEspacios(ActionEvent actionEvent) {
        try{
            vistaUtils.cargarVista("/org/example/view/profesor/menu-profesor-espacios-view.fxml");
            cerrarWindow(actionEvent);
        }catch (IOException e){
            log.error(e.getMessage());
        }
    }

    @FXML
    public void listarAulas(ActionEvent actionEvent) {
        try {
            vistaUtils.cargarYEsperarVista("/org/example/view/profesor/aulas-view.fxml");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    @FXML
    public void listarLaboratorios(ActionEvent actionEvent) {
        try {
            vistaUtils.cargarYEsperarVista("/org/example/view/profesor/laboratorios-view.fxml");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @FXML
    public void filtrarAulasDisponibles(ActionEvent actionEvent) {
        try {
            vistaUtils.cargarVista("/org/example/view/aulas-disponibles-view.fxml");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @FXML
    public void filtrarLaboratoriosDisponibles(ActionEvent actionEvent) {
        try {
            vistaUtils.cargarVista("/org/example/view/laboratorios-disponibles-view.fxml");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @FXML
    public void listarMisSolicitudesPendientes(ActionEvent actionEvent) {
        try {
            vistaUtils.cargarVista("/org/example/view/profesor/solicitudes-view.fxml",
                    (SolicitudesVistaController controller) -> controller.setEstadoSolicitud(EstadoSolicitud.PENDIENTE));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @FXML
    public void listarMisSolicitudesAprobadas(ActionEvent actionEvent) {
        try {
            vistaUtils.cargarVista("/org/example/view/profesor/solicitudes-view.fxml",
                    (SolicitudesVistaController controller) -> controller.setEstadoSolicitud(EstadoSolicitud.APROBADA));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @FXML
    public void listarMisSolicitudesRechazadas(ActionEvent actionEvent) {
        try {
            vistaUtils.cargarVista("/org/example/view/profesor/solicitudes-view.fxml",
                    (SolicitudesVistaController controller) -> controller.setEstadoSolicitud(EstadoSolicitud.RECHAZADA));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @FXML
    public void solicitarCambio(ActionEvent actionEvent) {

    }

    @FXML
    public void listarMisReservas(ActionEvent actionEvent) {

    }

    @FXML
    public void cambiarPassword(ActionEvent actionEvent) {
        try {
            vistaUtils.cargarVista("/org/example/view/cambio-password-view.fxml");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    @FXML
    public void volverMenuPrincipal(ActionEvent actionEvent) {
        try {
            vistaUtils.cargarVista("/org/example/view/profesor/menu-profesor-view.fxml");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        vistaUtils.cerrarVentana(this.btnVolver);
    }
}
