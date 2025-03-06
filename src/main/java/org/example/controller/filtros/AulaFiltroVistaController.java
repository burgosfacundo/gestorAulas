package org.example.controller.filtros;

import javafx.collections.FXCollections;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;


import lombok.extern.slf4j.Slf4j;
import org.controlsfx.control.CheckComboBox;
import org.example.enums.BloqueHorario;

import org.example.exception.JsonNotFoundException;
import org.example.model.Aula;
import org.example.service.AulaService;

import org.example.utils.ConfigurationUtils;
import org.example.utils.TableUtils;
import org.example.utils.VistaUtils;
import org.springframework.stereotype.Component;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class AulaFiltroVistaController{
    private final VistaUtils vistaUtils;
    @FXML
    private TextField capacidad;
    @FXML
    private CheckBox tieneProyector;
    @FXML
    private CheckBox tieneTV;
    @FXML
    private DatePicker fechaInicio;
    @FXML
    private DatePicker fechaFin;
    @FXML
    private CheckBox cbLunes;
    @FXML
    private CheckComboBox<BloqueHorario> checkComboBoxBloquesLunes;
    @FXML
    private CheckBox cbMartes;
    @FXML
    private CheckComboBox<BloqueHorario> checkComboBoxBloquesMartes;
    @FXML
    private CheckBox cbMiercoles;
    @FXML
    private CheckComboBox<BloqueHorario> checkComboBoxBloquesMiercoles;
    @FXML
    private CheckBox cbJueves;
    @FXML
    private CheckComboBox<BloqueHorario> checkComboBoxBloquesJueves;
    @FXML
    private CheckBox cbViernes;
    @FXML
    private CheckComboBox<BloqueHorario> checkComboBoxBloquesViernes;
    @FXML
    private CheckBox cbSabado;
    @FXML
    private CheckComboBox<BloqueHorario> checkComboBoxBloquesSabado;
    @FXML
    private CheckBox cbDomingo;
    @FXML
    private CheckComboBox<BloqueHorario> checkComboBoxBloquesDomingo;
    @FXML
    private TableView<Aula> tblAulas;
    @FXML
    private TableColumn<Aula, Integer> colId;
    @FXML
    private TableColumn<Aula, Integer> colNum;
    @FXML
    private TableColumn<Aula, Integer> colCapacidad;
    @FXML
    private TableColumn<Aula, Boolean> colTieneProyector;
    @FXML
    private TableColumn<Aula, Boolean> colTieneTV;
    @FXML
    private Button btnBuscar;

    private final AulaService aulaService;

    private final Map<DayOfWeek, Set<BloqueHorario>> diasYBloques;

    public AulaFiltroVistaController(AulaService aulaService,VistaUtils vistaUtils) {
        this.aulaService = aulaService;
        this.vistaUtils = vistaUtils;
        this.diasYBloques = new EnumMap<>(DayOfWeek.class);
    }

    @FXML
    public void initialize() {
        //Asocio columnas de la tabla con atributos del modelo
        TableUtils.inicializarTablaAula(colId,colNum,colCapacidad,colTieneProyector,colTieneTV);

        // Inicializar Map con días de la semana
        for (DayOfWeek day : DayOfWeek.values()) {
            diasYBloques.put(day, new HashSet<>());
        }

        // Configurar acciones de los CheckBox y CheckComboBox
        ConfigurationUtils.configurarDiaYBloques(diasYBloques,cbLunes, checkComboBoxBloquesLunes, DayOfWeek.MONDAY);
        ConfigurationUtils.configurarDiaYBloques(diasYBloques,cbMartes, checkComboBoxBloquesMartes, DayOfWeek.TUESDAY);
        ConfigurationUtils.configurarDiaYBloques(diasYBloques,cbMiercoles, checkComboBoxBloquesMiercoles, DayOfWeek.WEDNESDAY);
        ConfigurationUtils.configurarDiaYBloques(diasYBloques,cbJueves, checkComboBoxBloquesJueves, DayOfWeek.THURSDAY);
        ConfigurationUtils.configurarDiaYBloques(diasYBloques,cbViernes, checkComboBoxBloquesViernes, DayOfWeek.FRIDAY);
        ConfigurationUtils.configurarDiaYBloques(diasYBloques,cbSabado, checkComboBoxBloquesSabado, DayOfWeek.SATURDAY);
        ConfigurationUtils.configurarDiaYBloques(diasYBloques,cbDomingo, checkComboBoxBloquesDomingo, DayOfWeek.SUNDAY);

        //Configurar fecha de inicio y de fin
        ConfigurationUtils.configurarCalendarios(fechaInicio,fechaFin);
    }

    @FXML
    private void filtrarAulasDisponibles() {
        try {
            var errores = validarCampos();
            if (errores.isPresent()) {
                vistaUtils.mostrarAlerta(
                        String.join("\n", errores.get()),
                        Alert.AlertType.ERROR);
                return;
            }

            var text = capacidad.getText();
            Integer capacidadMin;
            if (text.isEmpty()) {
                capacidadMin = null;
            }else {
                capacidadMin = Integer.parseInt(text);
            }
            Boolean proyectorRequerido = tieneProyector.isSelected() ? true : null;
            Boolean tvRequerido = tieneTV.isSelected() ? true : null;
            LocalDate inicio = fechaInicio.getValue();
            LocalDate fin = fechaFin.getValue();

            diasYBloques.clear();
            if (cbLunes.isSelected()) diasYBloques.put(DayOfWeek.MONDAY, new HashSet<>(checkComboBoxBloquesLunes.getCheckModel().getCheckedItems()));
            if (cbMartes.isSelected()) diasYBloques.put(DayOfWeek.TUESDAY, new HashSet<>(checkComboBoxBloquesMartes.getCheckModel().getCheckedItems()));
            if (cbMiercoles.isSelected()) diasYBloques.put(DayOfWeek.WEDNESDAY, new HashSet<>(checkComboBoxBloquesMiercoles.getCheckModel().getCheckedItems()));
            if (cbJueves.isSelected()) diasYBloques.put(DayOfWeek.THURSDAY, new HashSet<>(checkComboBoxBloquesJueves.getCheckModel().getCheckedItems()));
            if (cbViernes.isSelected()) diasYBloques.put(DayOfWeek.FRIDAY, new HashSet<>(checkComboBoxBloquesViernes.getCheckModel().getCheckedItems()));
            if (cbSabado.isSelected()) diasYBloques.put(DayOfWeek.SATURDAY, new HashSet<>(checkComboBoxBloquesSabado.getCheckModel().getCheckedItems()));
            if (cbDomingo.isSelected()) diasYBloques.put(DayOfWeek.SUNDAY, new HashSet<>(checkComboBoxBloquesDomingo.getCheckModel().getCheckedItems()));

            var aulasFiltradas = aulaService.listarAulasDisponiblesConCondiciones(capacidadMin, proyectorRequerido, tvRequerido, inicio, fin, diasYBloques);
            var aulaObservableList = FXCollections.observableArrayList(aulasFiltradas);
            this.tblAulas.setItems(aulaObservableList);
            this.tblAulas.refresh();
        } catch (JsonNotFoundException e) {
            vistaUtils.mostrarAlerta(e.getMessage(), Alert.AlertType.ERROR);
            log.error(e.getMessage());
        }
    }


    private Optional<List<String>> validarCampos() {
        List<String> errores = new ArrayList<>();

        // Validar capacidad
        String texto = capacidad.getText();
        if (texto.isEmpty()) {
            capacidad.setStyle("-fx-border-color: transparent;");
        }else if (!texto.matches("\\d+")) {
            capacidad.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            errores.add("La capacidad debe ser un número válido.");
        } else {
            int number = Integer.parseInt(texto);
            if (number < 0) {
                capacidad.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                errores.add("La capacidad debe ser mayor o igual que 0.");
            } else {
                capacidad.setStyle("-fx-border-color: transparent;");
            }
        }


        // Validar fechas
        if (fechaInicio.getValue() == null) {
            fechaInicio.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            errores.add("Debe seleccionar una fecha de inicio.");
        } else {
            fechaInicio.setStyle("-fx-border-color: transparent;");
        }

        if (fechaFin.getValue() == null) {
            fechaFin.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            errores.add("Debe seleccionar una fecha de fin.");
        } else {
            fechaFin.setStyle("-fx-border-color: transparent;");
        }

        if (fechaFin.getValue().isBefore(fechaInicio.getValue())) {
            fechaInicio.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            fechaFin.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            errores.add("La fecha de fin no puede ser anterior a la de inicio.");
        }else {
            fechaFin.setStyle("-fx-border-color: transparent;");
            fechaInicio.setStyle("-fx-border-color: transparent;");
        }

        // Validar días seleccionados
        boolean algunDiaSeleccionado = cbLunes.isSelected() || cbMartes.isSelected() || cbMiercoles.isSelected() ||
                cbJueves.isSelected() || cbViernes.isSelected() || cbSabado.isSelected() || cbDomingo.isSelected();
        if (!algunDiaSeleccionado) {
            errores.add("Debe seleccionar al menos un día.");
        }

        // Validar horarios seleccionados por día
        if (cbLunes.isSelected() && checkComboBoxBloquesLunes.getCheckModel().getCheckedItems().isEmpty()) {
            errores.add("Debe seleccionar al menos un horario para el lunes.");
        }
        if (cbMartes.isSelected() && checkComboBoxBloquesMartes.getCheckModel().getCheckedItems().isEmpty()) {
            errores.add("Debe seleccionar al menos un horario para el martes.");
        }
        if (cbMiercoles.isSelected() && checkComboBoxBloquesMiercoles.getCheckModel().getCheckedItems().isEmpty()) {
            errores.add("Debe seleccionar al menos un horario para el miércoles.");
        }
        if (cbJueves.isSelected() && checkComboBoxBloquesJueves.getCheckModel().getCheckedItems().isEmpty()) {
            errores.add("Debe seleccionar al menos un horario para el jueves.");
        }
        if (cbViernes.isSelected() && checkComboBoxBloquesViernes.getCheckModel().getCheckedItems().isEmpty()) {
            errores.add("Debe seleccionar al menos un horario para el viernes.");
        }
        if (cbSabado.isSelected() && checkComboBoxBloquesSabado.getCheckModel().getCheckedItems().isEmpty()) {
            errores.add("Debe seleccionar al menos un horario para el sábado.");
        }
        if (cbDomingo.isSelected() && checkComboBoxBloquesDomingo.getCheckModel().getCheckedItems().isEmpty()) {
            errores.add("Debe seleccionar al menos un horario para el domingo.");
        }

        return errores.isEmpty() ? Optional.empty() : Optional.of(errores);
    }

}
