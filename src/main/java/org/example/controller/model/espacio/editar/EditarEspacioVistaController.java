package org.example.controller.model.espacio.editar;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.BadRequestException;
import org.example.exception.GlobalExceptionHandler;
import org.example.model.Aula;
import org.example.model.Espacio;
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
public class EditarEspacioVistaController {
    private Espacio espacio;
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
    private Button btnEditar;

    public void setEspacio(Espacio espacio) {
        this.espacio = espacio;
        actualizarData();
    }

    public void actualizarData() {
        Optional.ofNullable(espacio).ifPresent(a -> {
            tipoEspacioComboBox.getItems().addAll("Aula", "Laboratorio");
            if (espacio instanceof Laboratorio laboratorio) {
                tipoEspacioComboBox.setValue("Laboratorio");
                computadorasField.setText(String.valueOf(laboratorio.getComputadoras()));
            } else {
                tipoEspacioComboBox.setValue("Aula");
                computadorasField.setDisable(true);
            }
            tipoEspacioComboBox.setDisable(true);
            numeroField.setText(String.valueOf(a.getNumero()));
            capacidadField.setText(String.valueOf(a.getCapacidad()));
            tieneProyectorCheckBox.setSelected(a.isTieneProyector());
            tieneTVCheckBox.setSelected(a.isTieneTV());
        });
    }

    @FXML
    private void editar(ActionEvent actionEvent) {
        try {
            var errores = validarCampos();
            if (errores.isPresent()) {
                vistaUtils.mostrarAlerta(String.join("\n", errores.get()), Alert.AlertType.ERROR);
                return;
            }

            var id = espacio.getId();
            var numero = Integer.parseInt(numeroField.getText());
            var capacidad = Integer.parseInt(capacidadField.getText());
            boolean tieneProyector = tieneProyectorCheckBox.isSelected();
            boolean tieneTv = tieneTVCheckBox.isSelected();

            var tipo = tipoEspacioComboBox.getSelectionModel().getSelectedItem();
            var result = vistaUtils.mostrarAlerta("Estas seguro?", Alert.AlertType.CONFIRMATION);

            if (result == ButtonType.OK) {
                switch (tipo) {
                    case "Aula":
                        espacioService.modificar(new Aula(id, numero, capacidad, tieneProyector, tieneTv));
                        vistaUtils.mostrarAlerta("Se edito el aula " + numero + " correctamente.", Alert.AlertType.INFORMATION);
                        vistaUtils.cerrarVentana(btnEditar);
                        break;
                    case "Laboratorio":
                        var computadoras = Integer.parseInt(computadorasField.getText());
                        espacioService.modificar(new Laboratorio(id, numero, capacidad,
                                tieneProyector, tieneTv, computadoras));
                        vistaUtils.mostrarAlerta("Se edito el laboratorio " + numero + " correctamente.", Alert.AlertType.INFORMATION);
                        vistaUtils.cerrarVentana(btnEditar);
                        break;
                    default:
                        throw new BadRequestException("Tipo de espacio no valido");
                }
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
