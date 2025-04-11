package org.example.controller.model.espacio;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.BadRequestException;
import org.example.exception.GlobalExceptionHandler;
import org.example.model.Aula;
import org.example.model.Laboratorio;
import org.example.service.EspacioService;
import org.example.utils.Utils;
import org.example.utils.VistaUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class CrearEspacioVistaController {
    private final VistaUtils vistaUtils;
    private final EspacioService espacioService;
    private final GlobalExceptionHandler globalExceptionHandler;
    @FXML
    private ComboBox<String> tipoEspacioComboBox;
    @FXML
    private TextField numeroField;
    @FXML
    private TextField computadorasField;
    @FXML
    private TextField capacidadField;
    @FXML
    private CheckBox tieneProyectorCheckBox;
    @FXML
    private CheckBox tieneTVCheckBox;
    @FXML
    private Button btnCrear;

    @FXML
    public void initialize() {
        tipoEspacioComboBox.getItems().setAll(List.of("Aula","Laboratorio"));
        numeroField.setDisable(true);
        computadorasField.setDisable(true);
        capacidadField.setDisable(true);
        tieneProyectorCheckBox.setDisable(true);
        tieneTVCheckBox.setDisable(true);
        btnCrear.setDisable(true);

        tipoEspacioComboBox.setOnAction(event -> {
            boolean isLaboratorio = tipoEspacioComboBox.getSelectionModel().getSelectedItem().equals("Laboratorio");
            computadorasField.setDisable(!isLaboratorio);
            if (!isLaboratorio) {
                computadorasField.setStyle("-fx-border-color: transparent;");
                computadorasField.clear();
            }
            numeroField.setDisable(false);
            capacidadField.setDisable(false);
            tieneProyectorCheckBox.setDisable(false);
            tieneTVCheckBox.setDisable(false);
            btnCrear.setDisable(false);
        });
    }

    @FXML
    private void crear(ActionEvent actionEvent) {
        try {
            var errores = validarCampos();
            if (errores.isPresent()) {
                vistaUtils.mostrarAlerta(String.join("\n", errores.get()), Alert.AlertType.ERROR);
                return;
            }

            var numero = Integer.parseInt(numeroField.getText());
            var capacidad = Integer.parseInt(capacidadField.getText());
            boolean tieneProyector = tieneProyectorCheckBox.isSelected();
            boolean tieneTv = tieneTVCheckBox.isSelected();

            var tipo = tipoEspacioComboBox.getSelectionModel().getSelectedItem();
            switch (tipo){
                case "Aula":
                    espacioService.guardar(new Aula(null,numero,capacidad,tieneProyector,tieneTv));
                    vistaUtils.mostrarAlerta("Se creo el aula " + numero + " correctamente.", Alert.AlertType.INFORMATION);
                    vistaUtils.cerrarVentana(btnCrear);
                    break;
                case "Laboratorio":
                    var computadoras = Integer.parseInt(computadorasField.getText());
                    espacioService.guardar(new Laboratorio(null,numero,capacidad,
                            tieneProyector,tieneTv,computadoras));
                    vistaUtils.mostrarAlerta("Se creo el laboratorio " + numero + " correctamente.", Alert.AlertType.INFORMATION);
                    vistaUtils.cerrarVentana(btnCrear);
                    break;
                default:
                    throw new BadRequestException("Tipo de espacio no valido");
            }
        } catch (BadRequestException e) {
            globalExceptionHandler.handleBadRequestException(e);
        }
    }

    private Optional<List<String>> validarCampos() {
        List<String> errores = new ArrayList<>();

        // Validar computadoras
        if ("Laboratorio".equals(tipoEspacioComboBox.getSelectionModel().getSelectedItem())) {
            Utils.validarNumero(computadorasField, "Debes ingresar una cantidad de computadoras.", errores);
        }

        // Validar capacidad
        Utils.validarNumero(capacidadField, "Debes ingresar una capacidad.", errores);

        // Validar número
        Utils.validarNumero(numeroField, "Debes ingresar un número.", errores);

        return errores.isEmpty() ? Optional.empty() : Optional.of(errores);
    }
}
