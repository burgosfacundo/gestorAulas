package org.example.utils;

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
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

@Component
public class ConfigurationUtils {

    public void configurarDiaYBloques(Map<DayOfWeek, Set<BloqueHorario>> diasYBloques,
                                             CheckBox dia, CheckComboBox<BloqueHorario> comboBox, DayOfWeek day) {
        dia.selectedProperty().addListener((value, column, isSelected) -> {
            if (isSelected) {
                // Llenar el comboBox con bloques de horario al seleccionar el día
                comboBox.getItems().setAll(BloqueHorario.values());
            } else {
                comboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<BloqueHorario>) bloque -> {
                    diasYBloques.get(day).clear();
                    diasYBloques.get(day).addAll(comboBox.getCheckModel().getCheckedItems());
                });
                comboBox.getItems().clear();
                diasYBloques.get(day).clear(); // Limpiar bloques seleccionados
            }
        });

        comboBox.disableProperty().bind(dia.selectedProperty().not());
    }

    public void configurarCalendarios(DatePicker fechaInicio, DatePicker fechaFin){
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
