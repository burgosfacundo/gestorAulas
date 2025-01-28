package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.example.utils.VistaUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

@Slf4j
@SpringBootApplication
public class App extends Application {

    private ConfigurableApplicationContext springContext;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        springContext = new SpringApplicationBuilder(App.class)
                .run(getParameters().getRaw().toArray(new String[0]));
    }

    @Override
    public void start(Stage stage) {
        try {
            VistaUtils vistaUtils = springContext.getBean(VistaUtils.class);
            vistaUtils.cargarVista("/org/example/view/menus/menu-inicio-view.fxml");
        } catch (IOException e) {
            log.error("Error al cargar la vista inicial:");
            Platform.exit();
        }
    }

    @Override
    public void stop() {
        springContext.close();
        Platform.exit();
    }
}
