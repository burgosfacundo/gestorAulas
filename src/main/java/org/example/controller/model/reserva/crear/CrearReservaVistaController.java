package org.example.controller.model.reserva.crear;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Pair;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.controlsfx.control.CheckComboBox;
import org.example.enums.BloqueHorario;
import org.example.exception.*;
import org.example.model.*;
import org.example.model.dto.ReservaDTO;
import org.example.service.DiaBloqueService;
import org.example.service.EspacioService;
import org.example.service.ReservaService;
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
public class CrearReservaVistaController {
    private final VistaUtils vistaUtils;
    private final ReservaService reservaService;
    private final DiaBloqueService diaBloqueService;
    private final GlobalExceptionHandler globalExceptionHandler;
    @Setter
    private Inscripcion inscripcion;
    private final EspacioService espacioService;
    @FXML
    private DatePicker fechaInicioPicker;
    @FXML
    private DatePicker fechaFinPicker;
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
    private TableView<Espacio> tblEspacios;
    @FXML
    private TableColumn<Espacio, Integer> colNum;
    @FXML
    private TableColumn<Espacio, Integer> colCapacidad;
    @FXML
    private TableColumn<Espacio, Boolean> colTieneProyector;
    @FXML
    private TableColumn<Espacio, Boolean> colTieneTV;
    @FXML
    private TableColumn<Laboratorio, Integer>  colComputadoras;
    @FXML
    private Button btnReservar;
    @FXML
    private Button btnBuscar;
    @FXML
    private CheckBox tieneProyector;
    @FXML
    private CheckBox tieneTV;
    @FXML
    private Pagination pagination;
    private final Set<DiaBloque> diasYBloques;

    private static final int PAGE_SIZE = 10;
    private List<? extends Espacio> espaciosFiltrados;

    public CrearReservaVistaController(EspacioService espacioService, VistaUtils vistaUtils, ReservaService reservaService, DiaBloqueService diaBloqueService, GlobalExceptionHandler globalExceptionHandler) {
        this.espacioService = espacioService;
        this.diaBloqueService = diaBloqueService;
        this.diasYBloques = new HashSet<>();
        this.vistaUtils = vistaUtils;
        this.reservaService = reservaService;
        this.globalExceptionHandler = globalExceptionHandler;
    }

    @FXML
    public void initialize() {
        //Asocio columnas de la tabla con atributos del modelo
        TableUtils.inicializarTablaEspacio(colNum, colCapacidad, colTieneProyector, colTieneTV, colComputadoras);

        // Configurar acciones de los CheckBox y CheckComboBox
        Utils.configurarDiaYBloques(diasYBloques, cbLunes, checkComboBoxBloquesLunes, DayOfWeek.MONDAY);
        Utils.configurarDiaYBloques(diasYBloques, cbMartes, checkComboBoxBloquesMartes, DayOfWeek.TUESDAY);
        Utils.configurarDiaYBloques(diasYBloques, cbMiercoles, checkComboBoxBloquesMiercoles, DayOfWeek.WEDNESDAY);
        Utils.configurarDiaYBloques(diasYBloques, cbJueves, checkComboBoxBloquesJueves, DayOfWeek.THURSDAY);
        Utils.configurarDiaYBloques(diasYBloques, cbViernes, checkComboBoxBloquesViernes, DayOfWeek.FRIDAY);
        Utils.configurarDiaYBloques(diasYBloques, cbSabado, checkComboBoxBloquesSabado, DayOfWeek.SATURDAY);
        Utils.configurarDiaYBloques(diasYBloques, cbDomingo, checkComboBoxBloquesDomingo, DayOfWeek.SUNDAY);

        //Configurar fecha de inicio y de fin
        Utils.configurarCalendarios(fechaInicioPicker, fechaFinPicker);

        btnReservar.disableProperty().bind(tblEspacios.getSelectionModel().selectedItemProperty().isNull());

        // Configurar las fechas iniciales si no están establecidas
        if (fechaInicioPicker.getValue() == null) {
            fechaInicioPicker.setValue(LocalDate.now());
        }
        if (fechaFinPicker.getValue() == null) {
            fechaFinPicker.setValue(LocalDate.now());
        }

        // Configurar los controles según el rango inicial de fechas
        Set<DayOfWeek> diasInicialesEnRango = Utils.obtenerDiasEnRango(
                fechaInicioPicker.getValue(),
                fechaFinPicker.getValue());
        configurarControlesPorRango(diasInicialesEnRango);

        // Agregar listeners para actualizar los controles cuando cambien las fechas
        fechaInicioPicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && fechaFinPicker.getValue() != null) {
                Set<DayOfWeek> diasEnRango = Utils.obtenerDiasEnRango(newVal, fechaFinPicker.getValue());
                configurarControlesPorRango(diasEnRango);
            }
        });

        fechaFinPicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && fechaInicioPicker.getValue() != null) {
                Set<DayOfWeek> diasEnRango = Utils.obtenerDiasEnRango(fechaInicioPicker.getValue(), newVal);
                configurarControlesPorRango(diasEnRango);
            }
        });
    }

    @FXML
    public void filtrarEspaciosDisponibles(ActionEvent actionEvent) {
        var errores = validarCampos();
        if (errores.isPresent()) {
            vistaUtils.mostrarAlerta(String.join("\n", errores.get()), Alert.AlertType.ERROR);
            return;
        }

        Integer capacidadMin = inscripcion.getFechaFinInscripcion().isAfter(LocalDate.now()) ?
                inscripcion.getCantidadAlumnos() + inscripcion.getMargenAlumnos() : inscripcion.getCantidadAlumnos();
        Boolean proyectorRequerido = tieneProyector.isSelected() ? true : null;
        Boolean tvRequerido = tieneTV.isSelected() ? true : null;
        LocalDate inicio = fechaInicioPicker.getValue();
        LocalDate fin = fechaFinPicker.getValue();

        // Determinar días de inicio y fin, y días en el rango
        DayOfWeek diaInicio = inicio.getDayOfWeek();
        DayOfWeek diaFin = fin.getDayOfWeek();
        Set<DayOfWeek> diasEnRango = Utils.obtenerDiasEnRango(inicio, fin);

        // Configurar controles según el rango de fechas
        configurarControlesPorRango(diasEnRango);

        // Verificar selección de bloques en días de inicio y fin
        if (hayErrorEnBloquesSeleccionados(diaInicio) || hayErrorEnBloquesSeleccionados(diaFin)) {
            return;
        }

        // Recopilar días y bloques seleccionados
        recopilarDiasYBloques(diasEnRango);

        // Si no hay días/bloques seleccionados, mostrar error
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

        if (inscripcion.getAsignatura().isRequiereLaboratorio()){
            // Si la asignatura requiere laboratorio, filtrar por laboratorios
            espaciosFiltrados = espacioService.listarLaboratoriosDisponiblesConCondiciones(
                    null,capacidadMin, proyectorRequerido, tvRequerido, inicio, fin, diasBloquesPersist);
        } else {
            // Si no requiere laboratorio, filtrar por aulas
            espaciosFiltrados = espacioService.listarAulasDisponiblesConCondiciones(
                    capacidadMin, proyectorRequerido, tvRequerido, inicio, fin, diasBloquesPersist);
        }

        int totalPages = (int) Math.ceil((double) espaciosFiltrados.size() / PAGE_SIZE);
        pagination.setPageCount(Math.max(totalPages, 1));

        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> cargarPagina(newIndex.intValue()));

        cargarPagina(0);
    }

    private void cargarPagina(int pageIndex) {
        int fromIndex = pageIndex * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, espaciosFiltrados.size());

        ObservableList<Espacio> espacioObservableList = FXCollections.observableArrayList();
        espacioObservableList.addAll(espaciosFiltrados.subList(fromIndex, toIndex));

        tblEspacios.setItems(espacioObservableList);
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
        Map<DayOfWeek, Pair<CheckBox, CheckComboBox<BloqueHorario>>> mapa =
                new EnumMap<>(DayOfWeek.class);
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

    @FXML
    public void crearReserva(ActionEvent actionEvent) {
        try {
            List<String> errores = new ArrayList<>();
            Utils.validarTabla(tblEspacios,"Debes seleccionar un espacio", errores);

            if (!errores.isEmpty()) {
                vistaUtils.mostrarAlerta(
                        String.join("\n", errores),
                        Alert.AlertType.ERROR);
                return;
            }
            var espacio = tblEspacios.getSelectionModel().getSelectedItem();

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

            ReservaDTO reserva = new ReservaDTO(null,fechaInicioPicker.getValue(),
                    fechaFinPicker.getValue(),espacio.getId(),inscripcion.getId(), diasBloquesPersist);

            reservaService.guardar(reserva);
            vistaUtils.mostrarAlerta("Reserva creada con éxito", Alert.AlertType.INFORMATION);
            vistaUtils.cerrarVentana(btnReservar);
        }catch (BadRequestException e){
            globalExceptionHandler.handleBadRequestException(e);
        }catch (ConflictException e ){
            globalExceptionHandler.handleConflictException(e);
        }catch (NotFoundException e){
            globalExceptionHandler.handleNotFoundException(e);
        }
    }

    private Optional<List<String>> validarCampos() {
        List<String> errores = new ArrayList<>();

        // Validar fechas
        Utils.validarFecha(fechaInicioPicker, "Debe seleccionar una fecha de inicio.", errores);
        Utils.validarFecha(fechaFinPicker, "Debe seleccionar una fecha de fin.", errores);

        // Verificar que la fecha fin no sea anterior a la de inicio
        if (fechaInicioPicker.getValue() != null && fechaFinPicker.getValue() != null &&
                fechaFinPicker.getValue().isBefore(fechaInicioPicker.getValue())) {
            fechaInicioPicker.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            fechaFinPicker.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            errores.add("La fecha de fin no puede ser anterior a la de inicio.");
            return Optional.of(errores);
        }

        // Obtener los días del rango
        LocalDate inicio = fechaInicioPicker.getValue();
        LocalDate fin = fechaFinPicker.getValue();

        if (inicio == null || fin == null) {
            return Optional.of(errores); // Ya hay errores de fecha
        }

        Set<DayOfWeek> diasEnRango = Utils.obtenerDiasEnRango(inicio, fin);

        // Verificar si hay al menos un día habilitado seleccionado
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

        // Verificar específicamente el día de inicio y fin
        DayOfWeek diaInicio = inicio.getDayOfWeek();
        DayOfWeek diaFin = fin.getDayOfWeek();

        CheckBox checkBoxInicio = mapaDiasControles.get(diaInicio).getKey();
        CheckComboBox<BloqueHorario> comboBoxInicio = mapaDiasControles.get(diaInicio).getValue();

        CheckBox checkBoxFin = mapaDiasControles.get(diaFin).getKey();
        CheckComboBox<BloqueHorario> comboBoxFin = mapaDiasControles.get(diaFin).getValue();

        // Validar día de inicio
        if (checkBoxInicio.isSelected() && comboBoxInicio.getCheckModel().getCheckedItems().isEmpty()) {
            errores.add("Debe seleccionar al menos un bloque horario para el día " +
                    Utils.traducirDiaSemana(diaInicio) + " (día de inicio).");
        }

        // Validar día de fin (solo si es diferente del día de inicio)
        if (!diaInicio.equals(diaFin) && checkBoxFin.isSelected() && comboBoxFin.getCheckModel().getCheckedItems().isEmpty()) {
                errores.add("Debe seleccionar al menos un bloque horario para el día " +
                        Utils.traducirDiaSemana(diaFin) + " (día de fin).");
            }


        return errores.isEmpty() ? Optional.empty() : Optional.of(errores);
    }
}
