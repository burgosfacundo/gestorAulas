package org.example.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.controlsfx.control.CheckComboBox;
import org.example.enums.BloqueHorario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
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

    /**
 * Valida un TextField para asegurarse de que contiene un valor entero positivo no vacío.
 * Si la validación falla, el borde del TextField se establece en rojo y se agrega un mensaje de error a la lista de errores.
 *
 * @param campo El TextField a validar.
 * @param mensajeError El mensaje de error a agregar si la validación falla.
 * @param errores La lista de errores a la que se agrega el mensaje de error.
 */
public void validarCampo(TextField campo, String mensajeError, List<String> errores) {
    String text = campo.getText();
    if (text.isEmpty() || !text.matches("\\d+") || Integer.parseInt(text) <= 0) {
        campo.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        errores.add(mensajeError);
    } else {
        campo.setStyle("-fx-border-color: transparent;");
    }
}

/**
 * Valida que un CheckComboBox tenga al menos un elemento seleccionado si el CheckBox correspondiente está seleccionado.
 * Si la validación falla, se agrega un mensaje de error a la lista de errores.
 *
 * @param dia El CheckBox que representa el día.
 * @param horarios El CheckComboBox que contiene los bloques horarios.
 * @param nombreDia El nombre del día para el mensaje de error.
 * @param errores La lista de errores a la que se agrega el mensaje de error.
 */
public void validarHorarios(CheckBox dia, CheckComboBox<BloqueHorario> horarios, String nombreDia, List<String> errores) {
    if (dia.isSelected() && horarios.getCheckModel().getCheckedItems().isEmpty()) {
        errores.add("Debe seleccionar al menos un horario para el " + nombreDia + ".");
    }
}

/**
 * Valida que un DatePicker tenga una fecha seleccionada.
 * Si la validación falla, el borde del DatePicker se establece en rojo y se agrega un mensaje de error a la lista de errores.
 *
 * @param fecha El DatePicker a validar.
 * @param mensajeError El mensaje de error a agregar si la validación falla.
 * @param errores La lista de errores a la que se agrega el mensaje de error.
 */
public void validarFecha(DatePicker fecha, String mensajeError, List<String> errores) {
    if (fecha.getValue() == null) {
        fecha.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        errores.add(mensajeError);
    } else {
        fecha.setStyle("-fx-border-color: transparent;");
    }
}
}