package org.example.controller.menus;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.controller.model.espacio.AulaVistaController;
import org.example.controller.model.espacio.LaboratorioVistaController;
import org.example.exception.JsonNotFoundException;
import org.example.security.SesionActual;
import org.example.service.AulaService;
import org.example.utils.VistaUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class MenuListarEspaciosVistaController {
    private final VistaUtils vistaUtils;
    private final AulaService aulaService;
    private final SesionActual sesionActual;
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
            var aulas = aulaService.listarAulas();
            vistaUtils.cargarVista("/org/example/view/model/espacio/aula-view.fxml",
                    (AulaVistaController controller) -> controller.setAulas(aulas));
        } catch (IOException | JsonNotFoundException e) {
            log.error(e.getMessage());
        }
    }

    @FXML
    public void listarLaboratorios(ActionEvent actionEvent) {
        try {
            var laboratorios = aulaService.listarLaboratorios();
            vistaUtils.cargarVista("/org/example/view/model/espacio/laboratorio-view.fxml",
                    (LaboratorioVistaController controller) -> controller.setLaboratorios(laboratorios));
        } catch (IOException | JsonNotFoundException e) {
            log.error(e.getMessage());
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
