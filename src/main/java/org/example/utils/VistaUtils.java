package org.example.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class VistaUtils {

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
            stage.setTitle("UTN - Sistema de Gestión de Aulas");
            stage.show();
        } catch (IOException e) {
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
        stage.setTitle("UTN - Sistema de Gestión de Aulas");
        stage.setScene(scene);
        stage.showAndWait(); // Espera a que se cierre antes de continuar
    }

    /**
     * Muestra una alerta con el título y contenido especificados.
     */
    public void mostrarAlerta(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setTitle(title);
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