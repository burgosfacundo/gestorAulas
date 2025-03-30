package org.example.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.example.controller.model.CalendarioVistaController;
import org.example.controller.model.InscripcionVistaController;
import org.example.controller.model.reserva.ReservaVistaController;
import org.example.controller.model.espacio.EspacioVistaController;
import org.example.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@Slf4j
@Component
public class VistaUtils {

    private static final String TITLE = "UTN - Sistema de Gestión de Aulas";
    private final ApplicationContext springContext;

    @Autowired
    public VistaUtils(ApplicationContext springContext) {
        this.springContext = springContext;
    }

    /**
     * Carga una vista y la muestra en una nueva ventana.
     */
    public void cargarVista(String url) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(url));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(TITLE);
            stage.show();
        } catch (IOException e) {
            throw new IOException("Error al cargar la vista: " + url, e);
        }
    }

    /**
     * Carga una vista y la muestra en una nueva ventana, opcionalmente pasando datos al controlador.
     *
     * @param url la ruta del archivo FXML dentro del classpath
     * @param configurador un Consumer que permite configurar el controlador después de cargar la vista
     * @throws IOException si ocurre un error al cargar la vista
     */
    public <T> void cargarVista(String url, Consumer<T> configurador) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(url));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            // Configurar el controlador si se pasa un configurador
            T controller = loader.getController();
            if (configurador != null) {
                configurador.accept(controller);
            }

            // Mostrar la vista
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(TITLE);
            stage.show();
        } catch (IOException e) {
            throw new IOException("Error al cargar la vista: " + url, e);
        }
    }

    public ButtonType mostrarAlerta(String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setTitle(TITLE);
        alert.setContentText(content);
        alert.showAndWait();
        return alert.getResult();
    }

    public void mostrarVistaInscripcion(Inscripcion inscripcion) {
        try {
            cargarVista("/org/example/view/model/inscripcion-view.fxml",
                    (InscripcionVistaController controller) -> controller.setInscripciones(List.of(inscripcion)));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void mostrarVistaHorarios(Set<DiaBloque> horarios) {
        try {
            cargarVista("/org/example/view/model/calendario-view.fxml",
                    (CalendarioVistaController controller) -> controller.cargarHorarios(horarios));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void mostrarVistaReserva(Reserva reserva) {
        try {
            cargarVista("/org/example/view/model/reserva/reserva-view.fxml",
                    (ReservaVistaController controller) -> controller.setReservas(List.of(reserva)));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void mostrarVistaEspacio(Espacio espacio) {
        try {
            cargarVista("/org/example/view/model/espacio/espacio-view.fxml",
                    (EspacioVistaController controller) -> controller.setEspacios(List.of(espacio)));
        } catch (IOException e) {
            log.error("Error al cargar la vista de aulas: {}", e.getMessage());
        }
    }
    public void cerrarVentana(Control control) {
        Stage myStage = (Stage) control.getScene().getWindow();
        myStage.close();
    }
}