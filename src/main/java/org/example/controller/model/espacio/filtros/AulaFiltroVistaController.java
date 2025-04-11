package org.example.controller.model.espacio.filtros;

import javafx.application.Platform;
import javafx.collections.FXCollections;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;


import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.controlsfx.control.CheckComboBox;
import org.example.enums.BloqueHorario;

import org.example.exception.NotFoundException;
import org.example.model.Aula;
import org.example.model.DiaBloque;
import org.example.service.DiaBloqueService;
import org.example.service.EspacioService;

import org.example.utils.TableUtils;
import org.example.utils.Utils;
import org.example.utils.VistaUtils;
import org.springframework.stereotype.Component;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AulaFiltroVistaController{
    private final VistaUtils vistaUtils;
    private final DiaBloqueService diaBloqueService;
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
    private TableColumn<Aula, Integer> colNum;
    @FXML
    private TableColumn<Aula, Integer> colCapacidad;
    @FXML
    private TableColumn<Aula, Boolean> colTieneProyector;
    @FXML
    private TableColumn<Aula, Boolean> colTieneTV;
    @FXML
    private Button btnBuscar;
    @FXML
    private Pagination pagination;

    private final EspacioService espacioService;

    private final Set<DiaBloque> diasYBloques;

    private static final int PAGE_SIZE = 10;
    private List<Aula> aulasFiltradas;

    public AulaFiltroVistaController(EspacioService espacioService, VistaUtils vistaUtils, DiaBloqueService diaBloqueService) {
        this.espacioService = espacioService;
        this.vistaUtils = vistaUtils;
        this.diaBloqueService = diaBloqueService;
        this.diasYBloques = new HashSet<>();
        this.aulasFiltradas = new ArrayList<>();
    }

    @FXML
    public void initialize() {
        // Asocia columnas de la tabla con atributos del modelo
        TableUtils.inicializarTablaAula(colNum, colCapacidad, colTieneProyector, colTieneTV);

        // Configura acciones de los CheckBox y CheckComboBox
        Utils.configurarDiaYBloques(diasYBloques, cbLunes, checkComboBoxBloquesLunes, DayOfWeek.MONDAY);
        Utils.configurarDiaYBloques(diasYBloques, cbMartes, checkComboBoxBloquesMartes, DayOfWeek.TUESDAY);
        Utils.configurarDiaYBloques(diasYBloques, cbMiercoles, checkComboBoxBloquesMiercoles, DayOfWeek.WEDNESDAY);
        Utils.configurarDiaYBloques(diasYBloques, cbJueves, checkComboBoxBloquesJueves, DayOfWeek.THURSDAY);
        Utils.configurarDiaYBloques(diasYBloques, cbViernes, checkComboBoxBloquesViernes, DayOfWeek.FRIDAY);
        Utils.configurarDiaYBloques(diasYBloques, cbSabado, checkComboBoxBloquesSabado, DayOfWeek.SATURDAY);
        Utils.configurarDiaYBloques(diasYBloques, cbDomingo, checkComboBoxBloquesDomingo, DayOfWeek.SUNDAY);

        // Configura fecha de inicio y de fin
        Utils.configurarCalendarios(fechaInicio, fechaFin);

        // Configura las fechas iniciales si no están establecidas
        if (fechaInicio.getValue() == null) {
            fechaInicio.setValue(LocalDate.now());
        }
        if (fechaFin.getValue() == null) {
            fechaFin.setValue(LocalDate.now());
        }

        // Configura los controles según el rango inicial de fechas
        Set<DayOfWeek> diasInicialesEnRango = Utils.obtenerDiasEnRango(fechaInicio.getValue(), fechaFin.getValue());
        configurarControlesPorRango(diasInicialesEnRango);

        // Agrega listeners para actualizar los controles cuando cambien las fechas
        fechaInicio.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && fechaFin.getValue() != null) {
                Set<DayOfWeek> diasEnRango = Utils.obtenerDiasEnRango(newVal, fechaFin.getValue());
                configurarControlesPorRango(diasEnRango);
            }
        });

        fechaFin.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && fechaInicio.getValue() != null) {
                Set<DayOfWeek> diasEnRango = Utils.obtenerDiasEnRango(fechaInicio.getValue(), newVal);
                configurarControlesPorRango(diasEnRango);
            }
        });
    }


    @FXML
    private void filtrarAulasDisponibles() {
        var errores = validarCampos();
        if (errores.isPresent()) {
            vistaUtils.mostrarAlerta(String.join("\n", errores.get()), Alert.AlertType.ERROR);
            return;
        }

        var text = capacidad.getText();
        Integer capacidadMin;
        if (text.isEmpty()) {
            capacidadMin = null;
        } else {
            capacidadMin = Integer.parseInt(text);
        }
        Boolean proyectorRequerido = tieneProyector.isSelected() ? true : null;
        Boolean tvRequerido = tieneTV.isSelected() ? true : null;
        LocalDate inicio = fechaInicio.getValue();
        LocalDate fin = fechaFin.getValue();

        // Determina días de inicio y fin, y días en el rango
        DayOfWeek diaInicio = inicio.getDayOfWeek();
        DayOfWeek diaFin = fin.getDayOfWeek();
        Set<DayOfWeek> diasEnRango = Utils.obtenerDiasEnRango(inicio, fin);

        // Configura controles según el rango de fechas
        configurarControlesPorRango(diasEnRango);

        // Verifica selección de bloques en días de inicio y fin
        if (hayErrorEnBloquesSeleccionados(diaInicio) || hayErrorEnBloquesSeleccionados(diaFin)) {
            return;
        }

        // Recopila días y bloques seleccionados
        recopilarDiasYBloques(diasEnRango);

        // Si no hay días/bloques seleccionados, muestra error
        if (diasYBloques.isEmpty()) {
            vistaUtils.mostrarAlerta("Debe seleccionar al menos un día con sus bloques horarios", Alert.AlertType.ERROR);
            return;
        }

        var diasBloquesPersist = diasYBloques.stream()
                .map(diaBloque -> {
                    try {
                        return diaBloqueService.buscarPorDiaYBloque(diaBloque.getBloqueHorario(),diaBloque.getDia());
                    } catch (NotFoundException e) {
                        log.error(e.getMessage());
                    }
                    return diaBloque;
                })
                .collect(Collectors.toSet());


        aulasFiltradas = espacioService.listarAulasDisponiblesConCondiciones(capacidadMin, proyectorRequerido, tvRequerido,
                inicio, fin, diasBloquesPersist);
        int totalPages = (int) Math.ceil((double) aulasFiltradas.size() / PAGE_SIZE);
        pagination.setPageCount(Math.max(totalPages, 1));

        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> cargarPagina(newIndex.intValue()));

        cargarPagina(0);
    }

    private void cargarPagina(int pageIndex) {
        int fromIndex = pageIndex * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, aulasFiltradas.size());

        ObservableList<Aula> espacioObservableList = FXCollections.observableArrayList();
        espacioObservableList.addAll(aulasFiltradas.subList(fromIndex, toIndex));

        tblAulas.setItems(espacioObservableList);
    }

    /**
     * Valìda los campos del formulario y devuelve una lista de errores si los hay.
     * @return Un Optional que contiene una lista de errores si hay errores de validación, o un Optional vacío si no hay errores.
     */
    private Optional<List<String>> validarCampos() {
        List<String> errores = new ArrayList<>();

        // Valida capacidad
        Utils.validarNumero(capacidad, "La capacidad debe ser mayor a 0.", errores);

        // Valida fechas
        Utils.validarFecha(fechaInicio, "Debe seleccionar una fecha de inicio.", errores);
        Utils.validarFecha(fechaFin, "Debe seleccionar una fecha de fin.", errores);

        if (fechaInicio.getValue() != null && fechaFin.getValue() != null && fechaFin.getValue().isBefore(fechaInicio.getValue())) {
            fechaInicio.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            fechaFin.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            errores.add("La fecha de fin no puede ser anterior a la de inicio.");
        }

        // Obtiene los días del rango
        LocalDate inicio = fechaInicio.getValue();
        LocalDate fin = fechaFin.getValue();

        if (inicio == null || fin == null) {
            return Optional.of(errores); // Ya hay errores de fecha
        }

        Set<DayOfWeek> diasEnRango = Utils.obtenerDiasEnRango(inicio, fin);

        // Verifica si hay al menos un día habilitado seleccionado
        boolean hayDiaSeleccionado = false;
        Map<DayOfWeek, Pair<CheckBox, CheckComboBox<BloqueHorario>>> mapaDiasControles = crearMapaDiasControles();

        for (DayOfWeek dia : diasEnRango) {
            CheckBox checkBox = mapaDiasControles.get(dia).getKey();
            if (checkBox.isSelected()) {
                hayDiaSeleccionado = true;
                break;
            }
        }

        if (!hayDiaSeleccionado) {
            errores.add("Debe seleccionar al menos un día dentro del rango de fechas.");
        }

        // Verifica específicamente el día de inicio y fin
        DayOfWeek diaInicio = inicio.getDayOfWeek();
        DayOfWeek diaFin = fin.getDayOfWeek();

        CheckBox checkBoxInicio = mapaDiasControles.get(diaInicio).getKey();
        CheckComboBox<BloqueHorario> comboBoxInicio = mapaDiasControles.get(diaInicio).getValue();

        CheckBox checkBoxFin = mapaDiasControles.get(diaFin).getKey();
        CheckComboBox<BloqueHorario> comboBoxFin = mapaDiasControles.get(diaFin).getValue();

        // Valida día de inicio
        if (checkBoxInicio.isSelected() && comboBoxInicio.getCheckModel().getCheckedItems().isEmpty()) {
            errores.add("Debe seleccionar al menos un bloque horario para el día " +
                    Utils.traducirDiaSemana(diaInicio) + " (día de inicio).");
        }

        // Valida día de fin (solo si es diferente del día de inicio)
        if (!diaInicio.equals(diaFin) && checkBoxFin.isSelected() && comboBoxFin.getCheckModel().getCheckedItems().isEmpty()) {
            errores.add("Debe seleccionar al menos un bloque horario para el día " +
                    Utils.traducirDiaSemana(diaFin) + " (día de fin).");
        }

        return errores.isEmpty() ? Optional.empty() : Optional.of(errores);
    }

    private void configurarControlesPorRango(Set<DayOfWeek> diasEnRango) {
        Map<DayOfWeek, Pair<CheckBox, CheckComboBox<BloqueHorario>>> mapaDiasControles = crearMapaDiasControles();

        for (DayOfWeek dia : DayOfWeek.values()) {
            CheckBox checkBox = mapaDiasControles.get(dia).getKey();
            CheckComboBox<BloqueHorario> comboBox = mapaDiasControles.get(dia).getValue();

            boolean estaEnRango = diasEnRango.contains(dia);
            checkBox.setDisable(!estaEnRango);

            if (!estaEnRango) {
                checkBox.setSelected(false);
                Platform.runLater(() -> comboBox.getCheckModel().clearChecks());
            }
        }
    }

    private Map<DayOfWeek, Pair<CheckBox, CheckComboBox<BloqueHorario>>> crearMapaDiasControles() {
        Map<DayOfWeek, Pair<CheckBox, CheckComboBox<BloqueHorario>>> mapa = new EnumMap<>(DayOfWeek.class);
        mapa.put(DayOfWeek.MONDAY, new Pair<>(cbLunes, checkComboBoxBloquesLunes));
        mapa.put(DayOfWeek.TUESDAY, new Pair<>(cbMartes, checkComboBoxBloquesMartes));
        mapa.put(DayOfWeek.WEDNESDAY, new Pair<>(cbMiercoles, checkComboBoxBloquesMiercoles));
        mapa.put(DayOfWeek.THURSDAY, new Pair<>(cbJueves, checkComboBoxBloquesJueves));
        mapa.put(DayOfWeek.FRIDAY, new Pair<>(cbViernes, checkComboBoxBloquesViernes));
        mapa.put(DayOfWeek.SATURDAY, new Pair<>(cbSabado, checkComboBoxBloquesSabado));
        mapa.put(DayOfWeek.SUNDAY, new Pair<>(cbDomingo, checkComboBoxBloquesDomingo));
        return mapa;
    }

    private boolean hayErrorEnBloquesSeleccionados(DayOfWeek dia) {
        Map<DayOfWeek, Pair<CheckBox, CheckComboBox<BloqueHorario>>> mapaDiasControles = crearMapaDiasControles();
        CheckBox checkBox = mapaDiasControles.get(dia).getKey();
        CheckComboBox<BloqueHorario> comboBox = mapaDiasControles.get(dia).getValue();

        checkBox.setSelected(true);

        if (!comboBox.getCheckModel().getCheckedItems().isEmpty()) {
            return false;
        }

        vistaUtils.mostrarAlerta("Debe seleccionar al menos un bloque horario para el día: " +
                Utils.traducirDiaSemana(dia), Alert.AlertType.ERROR);
        return true;
    }

    private void recopilarDiasYBloques(Set<DayOfWeek> diasEnRango) {
        Map<DayOfWeek, Pair<CheckBox, CheckComboBox<BloqueHorario>>> mapaDiasControles = crearMapaDiasControles();
        diasYBloques.clear();

        for (DayOfWeek dia : diasEnRango) {
            CheckBox checkBox = mapaDiasControles.get(dia).getKey();
            CheckComboBox<BloqueHorario> comboBox = mapaDiasControles.get(dia).getValue();

            if (checkBox.isSelected()) {
                Utils.agregarDiaBloque(diasYBloques, checkBox, comboBox, dia);
            }
        }
    }
}
