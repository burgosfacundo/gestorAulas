package org.example.utils;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.converter.LocalDateStringConverter;
import org.controlsfx.control.CheckComboBox;
import org.example.enums.BloqueHorario;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConfigurationUtils {

    private ConfigurationUtils() {
        throw new UnsupportedOperationException("Esta es una clase de utilidad y no debe ser instanciada.");
    }

    public static void configurarDiaYBloques(Map<DayOfWeek, Set<BloqueHorario>> diasYBloques,
                                      CheckBox dia,
                                      CheckComboBox<BloqueHorario> comboBox,
                                      DayOfWeek day) {
        // Inicialización del Set para este día
        diasYBloques.computeIfAbsent(day, k -> new HashSet<>());

        // Listener para el día
        dia.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (Boolean.TRUE.equals(newValue)) { // Día seleccionado
                comboBox.getItems().setAll(BloqueHorario.values());
                diasYBloques.putIfAbsent(day, new HashSet<>());
            } else { // Día deseleccionado
                // Primero limpiamos el Set de bloques seleccionados
                diasYBloques.get(day).clear();

                // Luego limpiamos el modelo de selección del ComboBox
                Platform.runLater(() -> {
                    comboBox.getCheckModel().clearChecks();
                    comboBox.getItems().clear();
                });
            }
        });

        // Listener para los bloques horarios seleccionados
        comboBox.getCheckModel().getCheckedItems().addListener(
                (ListChangeListener<BloqueHorario>) change -> {
                    if (dia.isSelected()) {
                        Set<BloqueHorario> bloques = diasYBloques.get(day);
                        bloques.clear();
                        bloques.addAll(comboBox.getCheckModel().getCheckedItems());
                    }
                }
        );

        // Vincular el estado habilitado del comboBox con el checkbox del día
        comboBox.disableProperty().bind(dia.selectedProperty().not());
    }

    public static void configurarCalendarios(DatePicker fechaInicio, DatePicker fechaFin){
        //Configurando fechas
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");

        fechaInicio.setValue(LocalDate.now());
        fechaInicio.setEditable(false);
        fechaInicio.setConverter(new LocalDateStringConverter(formatter, null));
        fechaInicio.setDayCellFactory(column -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {

                super.updateItem(item, empty);

                this.setDisable(false);
                this.setBackground(null);
                this.setTextFill(Color.BLACK);

                // deshabilitar y pintar de rojo las fechas anteriores y que no son de este año
                if (item.isBefore(LocalDate.now()) || item.getYear() != LocalDate.now().getYear()) {
                    this.setDisable(true);
                    Paint color = Color.rgb(241,26,44);
                    BackgroundFill fill = new BackgroundFill(color, null, null);

                    this.setBackground(new Background(fill));
                    this.setTextFill(Color.WHITESMOKE);
                }
            }
        });


        fechaFin.setValue(LocalDate.now());
        fechaFin.setEditable(false);
        fechaFin.setConverter(new LocalDateStringConverter(formatter, null));
        fechaFin.setDayCellFactory(column -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {

                super.updateItem(item, empty);

                this.setDisable(false);
                this.setBackground(null);
                this.setTextFill(Color.BLACK);

                // deshabilitar las fechas anteriores y pintarlas de rojo
                if (item.isBefore(fechaInicio.getValue()) || item.getYear() != fechaInicio.getValue().getYear()) {
                    this.setDisable(true);
                    Paint color = Color.rgb(241,26,44);
                    BackgroundFill fill = new BackgroundFill(color, null, null);

                    this.setBackground(new Background(fill));
                    this.setTextFill(Color.WHITESMOKE);
                }
            }
        });
    }
}
