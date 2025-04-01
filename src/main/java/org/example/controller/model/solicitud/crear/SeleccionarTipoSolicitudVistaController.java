package org.example.controller.model.solicitud.crear;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.enums.TipoSolicitud;
import org.example.exception.BadRequestException;
import org.example.exception.GlobalExceptionHandler;
import org.example.exception.NotFoundException;
import org.example.model.*;
import org.example.service.EspacioService;
import org.example.service.SolicitudCambioAulaService;
import org.example.utils.Mapper;
import org.example.utils.TableUtils;
import org.example.utils.Utils;
import org.example.utils.VistaUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
@Setter
public class SeleccionarTipoSolicitudVistaController {
    private final VistaUtils vistaUtils;
    private final EspacioService espacioService;
    private final GlobalExceptionHandler globalExceptionHandler;
    private final SolicitudCambioAulaService solicitudCambioAulaService;
    private Reserva reserva;
    @FXML
    private ComboBox<TipoSolicitud> tipoSolicitudComboBox;
    @FXML
    private DatePicker fechaInicioPicker;
    @FXML
    private DatePicker fechaFinPicker;
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
    private TableColumn<Laboratorio, Integer> colComputadoras;
    @FXML
    private TextArea comentarioProfesorTextArea;
    @FXML
    private Button btnEnviarSolicitudButton;
    @FXML
    private Button btnBuscar;
    @FXML
    private Pagination pagination;
    private List<? extends Espacio> espaciosDisponibles;
    private static final int PAGE_SIZE = 10;

    @FXML
    public void initialize() {
        tipoSolicitudComboBox.getItems().setAll(TipoSolicitud.values());
        tipoSolicitudComboBox.setOnAction(event -> actualizarFechas());

        TableUtils.inicializarTablaEspacio(colNum,colCapacidad,colTieneProyector,colTieneTV,colComputadoras);
        //Configurar fecha de inicio y de fin
        Utils.configurarCalendarios(fechaInicioPicker, fechaFinPicker);
        fechaInicioPicker.setDisable(true);
        fechaFinPicker.setDisable(true);
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;

        if (reserva.getFechaInicio().isEqual(reserva.getFechaFin())) {
            tipoSolicitudComboBox.setValue(TipoSolicitud.TEMPORAL);
            tipoSolicitudComboBox.setDisable(true);
            fechaInicioPicker.setValue(reserva.getFechaInicio());
            fechaInicioPicker.setDisable(true);
            fechaFinPicker.setValue(reserva.getFechaFin());
            fechaFinPicker.setDisable(true);
        }
    }

    private void actualizarFechas() {
            Optional.ofNullable(tipoSolicitudComboBox.getValue())
                    .ifPresent(tipo -> {
                        if (tipo == TipoSolicitud.PERMANENTE) {
                            fechaInicioPicker.setValue(reserva.getFechaInicio());
                            fechaFinPicker.setValue(reserva.getFechaFin());
                            fechaInicioPicker.setDisable(true);
                            fechaFinPicker.setDisable(true);
                        } else {
                            fechaInicioPicker.setValue(LocalDate.now());
                            fechaFinPicker.setValue(LocalDate.now());
                            fechaInicioPicker.setDisable(false);
                            fechaFinPicker.setDisable(false);
                        }
                    });
    }

    @FXML
    private void actualizarListaAulas(){
        Optional.ofNullable(reserva)
                .ifPresent(r -> {
                    var errores = validarCampos();
                    if (errores.isPresent()) {
                        vistaUtils.mostrarAlerta(String.join("\n", errores.get()), Alert.AlertType.ERROR);
                        return;
                    }
                    if (r.getEspacio() instanceof Laboratorio lab) {
                        espaciosDisponibles = espacioService.listarLaboratoriosDisponiblesConCondiciones(
                                lab.getComputadoras(), lab.getCapacidad(), lab.isTieneProyector(), lab.isTieneTV(),
                                fechaInicioPicker.getValue(), fechaFinPicker.getValue(),
                                r.getDiasYBloques());

                        int totalPages = (int) Math.ceil((double) espaciosDisponibles.size() / PAGE_SIZE);
                        pagination.setPageCount(Math.max(totalPages, 1));

                        pagination.currentPageIndexProperty().addListener(
                                (obs, oldIndex, newIndex) -> cargarPagina(newIndex.intValue()));

                        cargarPagina(0);

                        if (espaciosDisponibles.isEmpty()) {
                            vistaUtils.mostrarAlerta("No hay aulas disponibles.", Alert.AlertType.INFORMATION);
                        }
                    } else {
                        List<? extends Espacio> disponibles = espacioService.listarAulasDisponiblesConCondiciones(
                                r.getEspacio().getCapacidad(), r.getEspacio().isTieneProyector(), r.getEspacio().isTieneTV(),
                                fechaInicioPicker.getValue(), fechaFinPicker.getValue(),
                                r.getDiasYBloques());

                        int totalPages = (int) Math.ceil((double) espaciosDisponibles.size() / PAGE_SIZE);
                        pagination.setPageCount(Math.max(totalPages, 1));

                        pagination.currentPageIndexProperty().addListener(
                                (obs, oldIndex, newIndex) -> cargarPagina(newIndex.intValue()));

                        cargarPagina(0);

                        if (disponibles.isEmpty()) {
                            vistaUtils.mostrarAlerta("No hay aulas disponibles.", Alert.AlertType.INFORMATION);
                        }
                    }
                });
    }

    private void cargarPagina(int pageIndex) {
        int fromIndex = pageIndex * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, espaciosDisponibles.size());

        ObservableList<Espacio> espaciosObservableList = FXCollections.observableArrayList();
        espaciosObservableList.addAll(espaciosDisponibles.subList(fromIndex, toIndex));
        tblEspacios.setItems(espaciosObservableList);
    }

    @FXML
    private void enviarSolicitud() {
        if (reserva == null) {
            vistaUtils.mostrarAlerta("No se seleccionó una reserva.", Alert.AlertType.ERROR);
            return;
        }

        TipoSolicitud tipo = tipoSolicitudComboBox.getValue();
        LocalDate fechaInicio = fechaInicioPicker.getValue();
        LocalDate fechaFin = fechaFinPicker.getValue();
        Espacio seleccionado = tblEspacios.getSelectionModel().getSelectedItem();
        String comentarioProfesor = comentarioProfesorTextArea.getText();

        if (tipo == null || seleccionado == null || fechaInicio == null || fechaFin == null) {
            vistaUtils.mostrarAlerta("Debe completar todos los campos.", Alert.AlertType.ERROR);
            return;
        }

        // Obtener todos los días del período seleccionado
        Set<LocalDate> fechasDelPeriodo = new HashSet<>();
        fechaInicio.datesUntil(fechaFin.plusDays(1))
                .forEach(fechasDelPeriodo::add);

        // Convertir las fechas a días de la semana
        var diasDelPeriodo = fechasDelPeriodo.stream()
                .map(LocalDate::getDayOfWeek)
                .collect(Collectors.toSet());

        // Filtrar los días y bloques que coinciden con los días de la semana dentro del período
        var diasYbloquesSeleccionados = reserva.getDiasYBloques().stream()
                .filter(diaBloque -> diasDelPeriodo.contains(diaBloque.getDia()))
                .collect(Collectors.toSet());

        if (diasYbloquesSeleccionados.isEmpty()) {
            vistaUtils.mostrarAlerta("Ninguno de los días de clase coincide con el período seleccionado.",
                    Alert.AlertType.ERROR);
            return;
        }

        SolicitudCambioAula solicitud = new SolicitudCambioAula(
                null, reserva.getInscripcion().getProfesor(), reserva, seleccionado,
                tipo, fechaInicio, fechaFin,
                diasYbloquesSeleccionados, // Solo los días y bloques que coinciden con el período
                comentarioProfesor
        );
        log.info(solicitud.getDiasYBloques().toString());

        try{
            solicitudCambioAulaService.guardar(Mapper.solicitudToDTO(solicitud));
            vistaUtils.mostrarAlerta("Solicitud enviada correctamente.", Alert.AlertType.INFORMATION);
            vistaUtils.cerrarVentana(btnEnviarSolicitudButton);
        }catch (NotFoundException e){
            globalExceptionHandler.handleNotFoundException(e);
        }catch (BadRequestException e){
            globalExceptionHandler.handleBadRequestException(e);
        }
    }

    /**
     * Valìda los campos del formulario y devuelve una lista de errores si los hay.
     * @return Un Optional que contiene una lista de errores si hay errores de validación, o un Optional vacío si no hay errores.
     */
    private Optional<List<String>> validarCampos() {
        List<String> errores = new ArrayList<>();

        Utils.validarComboBox(tipoSolicitudComboBox, "Debe seleccionar un tipo de solicitud.", errores);

        // Validar fechas
        Utils.validarFecha(fechaInicioPicker, "Debe seleccionar una fecha de inicio.", errores);
        Utils.validarFecha(fechaFinPicker, "Debe seleccionar una fecha de fin.", errores);

        // Obtener los días del rango
        LocalDate inicio = fechaInicioPicker.getValue();
        LocalDate fin = fechaFinPicker.getValue();

        // Validar que la fecha de fin no sea anterior a la fecha de inicio
        if (inicio != null && fin != null && fin.isBefore(inicio)) {
            fechaInicioPicker.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            fechaFinPicker.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            errores.add("La fecha de fin no puede ser anterior a la de inicio.");
        }

        if (inicio == null || fin == null) {
            return Optional.of(errores); // Ya hay errores de fecha
        }

        return errores.isEmpty() ? Optional.empty() : Optional.of(errores);
    }
}