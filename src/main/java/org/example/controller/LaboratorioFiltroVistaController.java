package org.example.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
import java.util.logging.Logger;

@Component
public class LaboratorioFiltroVistaController{
    private static final Logger logger = Logger.getLogger(LaboratorioFiltroVistaController.class.getName());
    private final VistaUtils vistaUtils;
    private final ConfigurationUtils configurationUtils;
    private final TableUtils tableUtils;
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

    public LaboratorioFiltroVistaController(AulaService aulaService, VistaUtils vistaUtils, ConfigurationUtils configurationUtils, TableUtils tableUtils) {
        this.aulaService = aulaService;
        this.diasYBloques = new HashMap<>();
        this.vistaUtils = vistaUtils;
        this.configurationUtils = configurationUtils;
        this.tableUtils = tableUtils;
    }

    @FXML
    public void initialize() {
        //Asocio columnas de la tabla con atributos del modelo
        tableUtils.inicializarTablaLaboratorio(colId,colNum,colCapacidad,colTieneProyector,colTieneTV,colComputadoras);

        // Inicializar Map con días de la semana
        for (DayOfWeek day : DayOfWeek.values()) {
            diasYBloques.put(day, new HashSet<>());
        }

        // Configurar acciones de los CheckBox y CheckComboBox
        configurationUtils.configurarDiaYBloques(diasYBloques,cbLunes, checkComboBoxBloquesLunes, DayOfWeek.MONDAY);
        configurationUtils.configurarDiaYBloques(diasYBloques,cbMartes, checkComboBoxBloquesMartes, DayOfWeek.TUESDAY);
        configurationUtils.configurarDiaYBloques(diasYBloques,cbMiercoles, checkComboBoxBloquesMiercoles, DayOfWeek.WEDNESDAY);
        configurationUtils.configurarDiaYBloques(diasYBloques,cbJueves, checkComboBoxBloquesJueves, DayOfWeek.THURSDAY);
        configurationUtils.configurarDiaYBloques(diasYBloques,cbViernes, checkComboBoxBloquesViernes, DayOfWeek.FRIDAY);
        configurationUtils.configurarDiaYBloques(diasYBloques,cbSabado, checkComboBoxBloquesSabado, DayOfWeek.SATURDAY);
        configurationUtils.configurarDiaYBloques(diasYBloques,cbDomingo, checkComboBoxBloquesDomingo, DayOfWeek.SUNDAY);

        //Configurar fecha de inicio y de fin
        configurationUtils.configurarCalendarios(fechaInicio,fechaFin);
    }

    @FXML
    private void filtrarLaboratoriosDisponibles() {
        try {
            var errores = validarCampos();
            if (errores.isPresent()) {
                vistaUtils.mostrarAlerta("Error en el formulario",String.join("\n", errores.get()), Alert.AlertType.ERROR);
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
            vistaUtils.mostrarAlerta("Error:",e.getMessage(), Alert.AlertType.ERROR);
            logger.severe(e.getMessage());
        }
    }


    private Optional<List<String>> validarCampos() {
        List<String> errores = new ArrayList<>();

        // Validar computadoras
        if (computadoras.isVisible()){
            String text = computadoras.getText();
            if (text.isEmpty()) {
                computadoras.setStyle("-fx-border-color: transparent;");
            }else if (!text.matches("\\d+")) {
                computadoras.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                errores.add("La cantidad de computadoras debe ser un número válido.");
            } else {
                int number = Integer.parseInt(text);
                if (number < 0) {
                    computadoras.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                    errores.add("La cantidad de computadoras debe ser mayor o igual que 0.");
                } else {
                    computadoras.setStyle("-fx-border-color: transparent;");
                }
            }
        }

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
