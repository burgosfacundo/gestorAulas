package org.example.controller.menus;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.controller.model.espacio.EspacioVistaController;
import org.example.security.SesionActual;
import org.example.service.EspacioService;
import org.example.utils.VistaUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class MenuListarEspaciosVistaController {
    private final VistaUtils vistaUtils;
    private final SesionActual sesionActual;
    private final EspacioService espacioService;
    @FXML
    private Button btnListarLaboratorios ;
    @FXML
    private Button btnFiltrarAulas;
    @FXML
    private Button btnListarAulas;
    @FXML
    private Button btnFiltrarLaboratorios;
    @FXML
    private Button btnVolver;

    @FXML
    public void listarAulas(ActionEvent actionEvent) {
        try {
            var aulas = espacioService.listarAulas();
            vistaUtils.cargarVista("/org/example/view/model/espacio/espacio-view.fxml",
                    (EspacioVistaController controller) -> controller.setEspacios(aulas));
        } catch (IOException e) {
            log.error("Error al cargar la vista de aulas: {}", e.getMessage());
        }
    }

    @FXML
    public void listarLaboratorios(ActionEvent actionEvent) {
        try {
            var laboratorios = espacioService.listarLaboratorios();
            vistaUtils.cargarVista("/org/example/view/model/espacio/espacio-view.fxml",
                    (EspacioVistaController controller) -> controller.setEspacios(laboratorios));
        } catch (IOException e) {
            log.error("Error al cargar la vista de laboratorios: {}", e.getMessage());
        }
    }

    @FXML
    public void filtrarAulasDisponibles(ActionEvent actionEvent) {
        try {
            vistaUtils.cargarVista("/org/example/view/model/espacio/filtros/aula-disponibles-view.fxml");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @FXML
    public void filtrarLaboratoriosDisponibles(ActionEvent actionEvent) {
        try {
            vistaUtils.cargarVista("/org/example/view/model/espacio/filtros/laboratorio-disponibles-view.fxml");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @FXML
    public void volverMenuPrincipal(ActionEvent actionEvent) {
        try {
            if (sesionActual.getUsuario().getRol().getNombre().equals("Administrador")) {
                vistaUtils.cargarVista("/org/example/view/menus/menu-espacios-view.fxml");
            }else if (sesionActual.getUsuario().getRol().getNombre().equals("Profesor")) {
                vistaUtils.cargarVista("/org/example/view/menus/menu-profesor-view.fxml");
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
        vistaUtils.cerrarVentana(this.btnVolver);
    }
}
