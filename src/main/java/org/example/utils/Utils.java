package org.example.utils;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.converter.LocalDateStringConverter;
import lombok.experimental.UtilityClass;
import org.controlsfx.control.CheckComboBox;
import org.example.enums.BloqueHorario;
import org.example.exception.BadRequestException;
import org.example.model.DiaBloque;
import org.example.model.Espacio;
import org.example.model.Inscripcion;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@UtilityClass
public class Utils {
    /**
     * Obtiene los nombres de los días de la semana en español.
     * @param diasSemana Set de DayOfWeek con los días de la semana.
     * @return String con los nombres de los días separados por coma.
     */
    public String traducirDiasSemana(Set<DayOfWeek> diasSemana) {
        return diasSemana.stream()
                .map(Utils::traducirDiaSemana)
                .map(day -> day.substring(0, 1).toUpperCase() + day.substring(1))
                .collect(Collectors.joining(", "));
    }

    // Traduce el día de la semana
    public String traducirDiaSemana(DayOfWeek day) {
        return switch (day) {
            case MONDAY -> "Lunes";
            case TUESDAY -> "Martes";
            case WEDNESDAY -> "Miércoles";
            case THURSDAY -> "Jueves";
            case FRIDAY -> "Viernes";
            case SATURDAY -> "Sábado";
            case SUNDAY -> "Domingo";
        };
    }

    /**
     * Valída la capacidad de un espacio con respecto a la cantidad de alumnos de una Inscripción
     * @param espacio que se quiere validar
     * @param inscripcion que se quiere validar
     * @throws BadRequestException si no alcanza la capacidad del aula para la cantidad de alumnos de la inscripción
     */
    public void validarCapacidadEspacio(Espacio espacio, Inscripcion inscripcion) throws BadRequestException {
        int alumnosRequeridos = inscripcion.getCantidadAlumnos() +
                (inscripcion.getFechaFinInscripcion().isAfter(LocalDate.now()) ? inscripcion.getMargenAlumnos() : 0);
        if (espacio.getCapacidad() < alumnosRequeridos) {
            throw new BadRequestException(String.format("El aula %d tiene capacidad para %d alumnos, pero se requieren %d.", espacio.getId(), espacio.getCapacidad(), alumnosRequeridos));
        }
    }

    public void agregarDiaBloque( Set<DiaBloque> diasYBloques,CheckBox checkBox, CheckComboBox<BloqueHorario> checkComboBox, DayOfWeek dayOfWeek) {
        if (checkBox.isSelected()) {
            for (BloqueHorario bloque : checkComboBox.getCheckModel().getCheckedItems()) {
                diasYBloques.add(new DiaBloque(bloque,dayOfWeek));
            }
        }
    }

    public Set<DayOfWeek> obtenerDiasEnRango(LocalDate inicio, LocalDate fin) {
        Set<DayOfWeek> diasEnRango = new HashSet<>();
        LocalDate fechaActual = inicio;
        while (!fechaActual.isAfter(fin)) {
            diasEnRango.add(fechaActual.getDayOfWeek());
            fechaActual = fechaActual.plusDays(1);
        }
        return diasEnRango;
    }

    public void configurarDiaYBloques(Set<DiaBloque> diasYBloques,
                                      CheckBox dia,
                                      CheckComboBox<BloqueHorario> comboBox,
                                      DayOfWeek day) {
        // Listener para el día
        dia.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (Boolean.TRUE.equals(newValue)) { // Día seleccionado
                comboBox.getItems().setAll(BloqueHorario.values());

            } else { // Día deseleccionado
                // Removemos todos los DiaBloque asociados a este día
                diasYBloques.removeIf(diaBloque -> diaBloque.getDia().equals(day));

                // Limpiamos el modelo de selección del ComboBox
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
                        // Primero eliminamos los DiaBloque existentes para este día
                        diasYBloques.removeIf(diaBloque -> diaBloque.getDia().equals(day));

                        // Luego agregamos los nuevos DiaBloque según los bloques seleccionados
                        for (BloqueHorario bloque : comboBox.getCheckModel().getCheckedItems()) {
                            diasYBloques.add(new DiaBloque(bloque,day));
                        }
                    }
                }
        );

        // Vincular el estado habilitado del comboBox con el checkbox del día
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
                    Paint color = Color.rgb(211,126,134);
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


    /**
     * Validar un TextField para asegurarse de que contiene un valor entero positivo no vacío.
     * Si la validación falla, el borde del TextField se establece en rojo y se agrega un mensaje de error a la lista de errores.
     *
     * @param campo El TextField a validar.
     * @param mensajeError El mensaje de error a agregar si la validación falla.
     * @param errores La lista de errores a la que se agrega el mensaje de error.
     */
    public void validarNumero(TextField campo, String mensajeError, List<String> errores) {
        String text = campo.getText();
        if (text.isEmpty() || !text.matches("\\d+") || Integer.parseInt(text) <= 0) {
            campo.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            errores.add(mensajeError);
        } else {
            campo.setStyle("-fx-border-color: transparent;");
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

    public void validarTexto(TextField campo, String mensajeError, List<String> errores) {
        String text = campo.getText();
        if (text.isEmpty()) {
            campo.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            errores.add(mensajeError);
        } else {
            campo.setStyle("-fx-border-color: transparent;");
        }
    }

    public void validarPassword(PasswordField campo, List<String> errores) {
        String text = campo.getText();
        String[] patterns = {
                ".*\\d.*", ".*[a-z].*", ".*[A-Z].*", ".*[!@#$%^&*].*"

        };
        String[] messages = {
                "La contraseña debe contener al menos un dígito.",
                "La contraseña debe contener al menos una letra minúscula.",
                "La contraseña debe contener al menos una letra mayúscula.",
                "La contraseña debe contener al menos un carácter especial."
        };

        if (text.length() < 8) {
            errores.add("La contraseña debe tener al menos 8 caracteres.");
        }

        for (int i = 0; i < patterns.length; i++) {
            if (!text.matches(patterns[i])) {
                errores.add(messages[i]);
            }
        }

        String currentStyle = campo.getStyle();
        if (errores.isEmpty()) {
            campo.setStyle(currentStyle.replace("-fx-border-color: red; -fx-border-width: 2px;", "").trim());
        } else {
            if (!currentStyle.contains("-fx-border-color: red;")) {
                campo.setStyle(currentStyle + " -fx-border-color: red; -fx-border-width: 2px;");
            }
        }

    }

    public <T> void validarComboBox(ComboBox<T> comboBox, String mensajeError, List<String> errores) {
        if (comboBox.getSelectionModel().isEmpty()) {
            comboBox.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            errores.add(mensajeError);
        } else {
            comboBox.setStyle("-fx-border-color: transparent;");

        }
    }

    public <T> void validarTabla(TableView<T> tabla, String mensajeError, List<String> errores) {
        if (tabla.getSelectionModel().isEmpty()) {
            tabla.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            errores.add(mensajeError);
        } else {
            tabla.setStyle("-fx-border-color: transparent;");

        }
    }
}

