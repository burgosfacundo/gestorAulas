package org.example.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
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
            log.error(e.getMessage());
            throw new IOException("Error al cargar la vista: " + url, e);
        }
    }


    /**
     * Carga una vista en modo modal y espera a que se cierre.
     */
    public void cargarYEsperarVista(String url) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(url));
        loader.setControllerFactory(springContext::getBean);
        Parent root = loader.load();

        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL); // Configura el modo modal
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.showAndWait(); // Espera a que se cierre antes de continuar
    }

    /**
     * Muestra una alerta con el título y contenido especificados.
     */
    public void mostrarAlerta(String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setTitle(TITLE);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Cierra la ventana que contiene el control especificado.
     */
    public void cerrarVentana(Control control) {
        Stage myStage = (Stage) control.getScene().getWindow();
        myStage.close();
    }
}