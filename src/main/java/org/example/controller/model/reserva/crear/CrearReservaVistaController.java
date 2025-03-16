package org.example.controller.model.reserva.crear;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.controlsfx.control.CheckComboBox;
import org.example.enums.BloqueHorario;
import org.example.exception.*;
import org.example.model.Aula;
import org.example.model.Inscripcion;
import org.example.model.Laboratorio;
import org.example.model.Reserva;
import org.example.service.AulaService;
import org.example.service.ReservaService;
import org.example.utils.ConfigurationUtils;
import org.example.utils.TableUtils;
import org.example.utils.VistaUtils;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class CrearReservaVistaController {
    private final VistaUtils vistaUtils;
    private final ReservaService reservaService;
    private final GlobalExceptionHandler globalExceptionHandler;
    @Setter
    private Inscripcion inscripcion;
    private final AulaService aulaService;
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
    private TableView<Aula> tblEspacios;
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
    private TableColumn<Laboratorio, Integer>  colComputadoras;
    @FXML
    private Button btnReservar;
    @FXML
    private Button btnBuscar;
    @FXML
    private CheckBox tieneProyector;
    @FXML
    private CheckBox tieneTV;
    private final Map<DayOfWeek, Set<BloqueHorario>> diasYBloques;

    public CrearReservaVistaController(AulaService aulaService, VistaUtils vistaUtils, ReservaService reservaService, GlobalExceptionHandler globalExceptionHandler) {
        this.aulaService = aulaService;
        this.diasYBloques = new EnumMap<>(DayOfWeek.class);
        this.vistaUtils = vistaUtils;
        this.reservaService = reservaService;
        this.globalExceptionHandler = globalExceptionHandler;
    }

    @FXML
    public void initialize() {
        //Asocio columnas de la tabla con atributos del modelo
        TableUtils.inicializarTablaEspacio(colId,colNum,colCapacidad,colTieneProyector,colTieneTV,colComputadoras);

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
        ConfigurationUtils.configurarCalendarios(fechaInicioPicker,fechaFinPicker);

        btnReservar.disableProperty().bind(tblEspacios.getSelectionModel().selectedItemProperty().isNull());
    }

    @FXML
    public void filtrarEspaciosDisponibles(ActionEvent actionEvent) {
        try {
            var errores = validarCampos();
            if (errores.isPresent()) {
                vistaUtils.mostrarAlerta(
                        String.join("\n", errores.get()),
                        Alert.AlertType.ERROR);
                return;
            }
            Integer capacidadMin = inscripcion.getFechaFinInscripcion().isAfter(LocalDate.now()) ?
                    inscripcion.getCantidadAlumnos() + inscripcion.getMargenAlumnos() : inscripcion.getCantidadAlumnos();
            Boolean proyectorRequerido = tieneProyector.isSelected() ? true : null;
            Boolean tvRequerido = tieneTV.isSelected() ? true : null;
            LocalDate inicio = fechaInicioPicker.getValue();
            LocalDate fin = fechaFinPicker.getValue();

            diasYBloques.clear();
            if (cbLunes.isSelected()) diasYBloques.put(DayOfWeek.MONDAY, new HashSet<>(checkComboBoxBloquesLunes.getCheckModel().getCheckedItems()));
            if (cbMartes.isSelected()) diasYBloques.put(DayOfWeek.TUESDAY, new HashSet<>(checkComboBoxBloquesMartes.getCheckModel().getCheckedItems()));
            if (cbMiercoles.isSelected()) diasYBloques.put(DayOfWeek.WEDNESDAY, new HashSet<>(checkComboBoxBloquesMiercoles.getCheckModel().getCheckedItems()));
            if (cbJueves.isSelected()) diasYBloques.put(DayOfWeek.THURSDAY, new HashSet<>(checkComboBoxBloquesJueves.getCheckModel().getCheckedItems()));
            if (cbViernes.isSelected()) diasYBloques.put(DayOfWeek.FRIDAY, new HashSet<>(checkComboBoxBloquesViernes.getCheckModel().getCheckedItems()));
            if (cbSabado.isSelected()) diasYBloques.put(DayOfWeek.SATURDAY, new HashSet<>(checkComboBoxBloquesSabado.getCheckModel().getCheckedItems()));
            if (cbDomingo.isSelected()) diasYBloques.put(DayOfWeek.SUNDAY, new HashSet<>(checkComboBoxBloquesDomingo.getCheckModel().getCheckedItems()));

            var aulasFiltradas = aulaService.listarEspaciosDisponiblesConCondiciones(capacidadMin, proyectorRequerido, tvRequerido, inicio, fin, diasYBloques);
            var aulaObservableList = FXCollections.observableArrayList(aulasFiltradas);
            this.tblEspacios.setItems(aulaObservableList);
            this.tblEspacios.refresh();
        } catch (JsonNotFoundException e) {
            vistaUtils.mostrarAlerta(e.getMessage(), Alert.AlertType.ERROR);
            log.error(e.getMessage());
        }
    }

    @FXML
    public void crearReserva(ActionEvent actionEvent) {
        try {
            List<String> errores = new ArrayList<>();
            vistaUtils.validarTabla(tblEspacios,"Debes seleccionar un espacio", errores);

            if (!errores.isEmpty()) {
                vistaUtils.mostrarAlerta(
                        String.join("\n", errores),
                        Alert.AlertType.ERROR);
                return;
            }
            var espacio = tblEspacios.getSelectionModel().getSelectedItem();
            Reserva reserva = new Reserva(0,fechaInicioPicker.getValue(),fechaFinPicker.getValue(),espacio,inscripcion, diasYBloques);
            reservaService.guardar(reserva);
            vistaUtils.mostrarAlerta("Reserva creada con éxito", Alert.AlertType.INFORMATION);
            vistaUtils.cerrarVentana(btnReservar);
        }catch (JsonNotFoundException e) {
            globalExceptionHandler.handleJsonNotFoundException(e);
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
        vistaUtils.validarFecha(fechaInicioPicker, "Debe seleccionar una fecha de inicio.", errores);
        vistaUtils.validarFecha(fechaFinPicker, "Debe seleccionar una fecha de fin.", errores);

        if (fechaInicioPicker.getValue() != null && fechaFinPicker.getValue() != null && fechaFinPicker.getValue().isBefore(fechaInicioPicker.getValue())) {
            fechaInicioPicker.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            fechaFinPicker.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            errores.add("La fecha de fin no puede ser anterior a la de inicio.");
        }

        // Validar días seleccionados
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
