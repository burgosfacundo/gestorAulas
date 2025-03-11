package org.example.controller.filtros;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.extern.slf4j.Slf4j;
import org.controlsfx.control.CheckComboBox;
import org.example.enums.BloqueHorario;
import org.example.exception.JsonNotFoundException;
import org.example.model.Laboratorio;
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
public class LaboratorioFiltroVistaController{
    private final VistaUtils vistaUtils;
    @FXML
    private TextField computadoras;
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
    private TableView<Laboratorio> tblLaboratorios;
    @FXML
    private TableColumn<Laboratorio,Integer> colComputadoras;
    @FXML
    private TableColumn<Laboratorio, Integer> colId;
    @FXML
    private TableColumn<Laboratorio, Integer> colNum;
    @FXML
    private TableColumn<Laboratorio, Integer> colCapacidad;
    @FXML
    private TableColumn<Laboratorio, Boolean> colTieneProyector;
    @FXML
    private TableColumn<Laboratorio, Boolean> colTieneTV;
    @FXML
    private Button btnBuscar;

    private final AulaService aulaService;

    private final Map<DayOfWeek, Set<BloqueHorario>> diasYBloques;

    public LaboratorioFiltroVistaController(AulaService aulaService, VistaUtils vistaUtils) {
        this.aulaService = aulaService;
        this.diasYBloques = new EnumMap<>(DayOfWeek.class);
        this.vistaUtils = vistaUtils;
    }

    @FXML
    public void initialize() {
        //Asocio columnas de la tabla con atributos del modelo
        TableUtils.inicializarTablaLaboratorio(colId,colNum,colCapacidad,colTieneProyector,colTieneTV,colComputadoras);

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
    private void filtrarLaboratoriosDisponibles() {
        try {
            var errores = validarCampos();
            if (errores.isPresent()) {
                vistaUtils.mostrarAlerta(String.join("\n", errores.get()), Alert.AlertType.ERROR);
                return;
            }

            var textComputadoras = computadoras.getText();
            Integer computadorasMin;
            if (textComputadoras.isEmpty()) {
                computadorasMin = null;
            }else {
                computadorasMin = Integer.parseInt(textComputadoras);
            }

            var textCapacidad = capacidad.getText();
            Integer capacidadMin;
            if (textCapacidad.isEmpty()) {
                capacidadMin = null;
            }else {
                capacidadMin = Integer.parseInt(textCapacidad);
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

            var labsFiltrados = aulaService.listarLaboratoriosDisponiblesConCondiciones(computadorasMin,capacidadMin, proyectorRequerido, tvRequerido, inicio, fin, diasYBloques);
            var labsObservableList = FXCollections.observableArrayList(labsFiltrados);
            this.tblLaboratorios.setItems(labsObservableList);
            this.tblLaboratorios.refresh();
        } catch (JsonNotFoundException e) {
            vistaUtils.mostrarAlerta(e.getMessage(), Alert.AlertType.ERROR);
            log.error(e.getMessage());
        }
    }

/**
     * Valida los campos del formulario y devuelve una lista de errores si los hay.
     * @return Un Optional que contiene una lista de errores si hay errores de validación, o un Optional vacío si no hay errores.
     */
    private Optional<List<String>> validarCampos() {
        List<String> errores = new ArrayList<>();

        // Validar computadoras
        vistaUtils.validarNumero(computadoras, "La cantidad de computadoras debe ser mayor a 0.", errores);

        // Validar capacidad
        vistaUtils.validarNumero(capacidad, "La capacidad debe ser un número válido.", errores);

        // Validar fechas
        vistaUtils.validarFecha(fechaInicio, "Debe seleccionar una fecha de inicio.", errores);
        vistaUtils.validarFecha(fechaFin, "Debe seleccionar una fecha de fin.", errores);

        // Validar que la fecha de fin no sea anterior a la fecha de inicio
        if (fechaInicio.getValue() != null && fechaFin.getValue() != null && fechaFin.getValue().isBefore(fechaInicio.getValue())) {
            fechaInicio.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            fechaFin.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            errores.add("La fecha de fin no puede ser anterior a la de inicio.");
        }

        // Validar que al menos un día esté seleccionado
        if (!cbLunes.isSelected() && !cbMartes.isSelected() && !cbMiercoles.isSelected() &&
                !cbJueves.isSelected() && !cbViernes.isSelected() && !cbSabado.isSelected() && !cbDomingo.isSelected()) {
            errores.add("Debe seleccionar al menos un día.");
        }

        // Validar horarios seleccionados por día
        vistaUtils.validarHorarios(cbLunes, checkComboBoxBloquesLunes, "lunes", errores);
        vistaUtils.validarHorarios(cbMartes, checkComboBoxBloquesMartes, "martes", errores);
        vistaUtils.validarHorarios(cbMiercoles, checkComboBoxBloquesMiercoles, "miércoles", errores);
        vistaUtils.validarHorarios(cbJueves, checkComboBoxBloquesJueves, "jueves", errores);
        vistaUtils.validarHorarios(cbViernes, checkComboBoxBloquesViernes, "viernes", errores);
        vistaUtils.validarHorarios(cbSabado, checkComboBoxBloquesSabado, "sábado", errores);
        vistaUtils.validarHorarios(cbDomingo, checkComboBoxBloquesDomingo, "domingo", errores);

        return errores.isEmpty() ? Optional.empty() : Optional.of(errores);
    }
}
