package org.example.utils;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.enums.BloqueHorario;
import org.example.model.Aula;
import org.example.model.Laboratorio;
import org.example.model.Reserva;
import org.example.model.SolicitudCambioAula;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

public class TableUtils {

    private TableUtils() {
        throw new UnsupportedOperationException("Esta es una clase de utilidad y no debe ser instanciada.");
    }

    public static void inicializarTablaAula(TableColumn<Aula, Integer> colId,
                                            TableColumn<Aula, Integer> colNum,
                                            TableColumn<Aula, Integer> colCapacidad,
                                            TableColumn<Aula, Boolean> colTieneProyector,
                                            TableColumn<Aula, Boolean> colTieneTV){
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNum.setCellValueFactory(new PropertyValueFactory<>("numero"));
        colCapacidad.setCellValueFactory(new PropertyValueFactory<>("capacidad"));
        colTieneProyector.setCellValueFactory(new PropertyValueFactory<>("tieneProyector"));
        colTieneTV.setCellValueFactory(new PropertyValueFactory<>("tieneTV"));
        colTieneProyector.setCellFactory(TableUtils::columnTic);

        colTieneTV.setCellFactory(TableUtils::columnTic);
    }

    public static void inicializarTablaLaboratorio(TableColumn<Laboratorio, Integer> colId,
                                                   TableColumn<Laboratorio, Integer> colNum,
                                                   TableColumn<Laboratorio, Integer> colCapacidad,
                                                   TableColumn<Laboratorio, Boolean> colTieneProyector,
                                                   TableColumn<Laboratorio, Boolean> colTieneTV,
                                                   TableColumn<Laboratorio, Integer> colComputadoras){
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colComputadoras.setCellValueFactory(new PropertyValueFactory<>("computadoras"));
        colNum.setCellValueFactory(new PropertyValueFactory<>("numero"));
        colCapacidad.setCellValueFactory(new PropertyValueFactory<>("capacidad"));
        colTieneProyector.setCellValueFactory(new PropertyValueFactory<>("tieneProyector"));
        colTieneTV.setCellValueFactory(new PropertyValueFactory<>("tieneTV"));

        // Personalizo las columnas booleanas (tieneProyector y tieneTV)
        colTieneProyector.setCellFactory(TableUtils::columnTic);

        colTieneTV.setCellFactory(TableUtils::columnTic);
    }

    public static void inicializarTablaSolicitudes(TableColumn<SolicitudCambioAula,Integer> colId,
                                            TableColumn<SolicitudCambioAula,String> colReserva,
                                            TableColumn<SolicitudCambioAula,String> colAula,
                                            TableColumn<SolicitudCambioAula, String> colEstado,
                                            TableColumn<SolicitudCambioAula, String> colTipo,
                                            TableColumn<SolicitudCambioAula, LocalDate> colInicio,
                                            TableColumn<SolicitudCambioAula, LocalDate> colFin,
                                            TableColumn<SolicitudCambioAula, Map<DayOfWeek, Set<BloqueHorario>>> colDiaHorario,
                                            TableColumn<SolicitudCambioAula,String> colComenProfe,
                                            TableColumn<SolicitudCambioAula,String> colComenAdmin){
        // Configurar las columnas (PropertyValueFactory según tu modelo)
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colReserva.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReservaOriginal().toString()));
        colAula.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNuevaAula().toString()));
        colEstado.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEstado().toString()));
        colTipo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTipoSolicitud().toString()));
        colInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        colFin.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));
        colDiaHorario.setCellValueFactory(new PropertyValueFactory<>("diasYBloques"));
        colDiaHorario.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Map<DayOfWeek, Set<BloqueHorario>> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(Utils.formatDiasYBloques(item));
                }
            }
        });
        colComenProfe.setCellValueFactory(new PropertyValueFactory<>("comentarioProfesor"));
        colComenAdmin.setCellValueFactory(new PropertyValueFactory<>("comentarioEstado"));
    }

    public static void inicializarTablaReserva(TableColumn<Reserva,Integer> colId,
                                               TableColumn<Reserva, LocalDate> colFechaInicio,
                                               TableColumn<Reserva,LocalDate> colFechaFin,
                                               TableColumn<Reserva,String> colAula,
                                               TableColumn<Reserva,String> colInscripcion,
                                               TableColumn<SolicitudCambioAula, Map<DayOfWeek, Set<BloqueHorario>>> colDiaHorario){
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        colFechaFin.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));
        colAula.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAula().toString()));
        colInscripcion.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getInscripcion().toString()));
        colDiaHorario.setCellValueFactory(new PropertyValueFactory<>("diasYBloques"));
        colDiaHorario.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Map<DayOfWeek, Set<BloqueHorario>> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(Utils.formatDiasYBloques(item));
                }
            }
        });
    }

    private static <T extends Aula> TableCell<T, Boolean> columnTic(TableColumn<T, Boolean> column) {
        return new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(Boolean.TRUE.equals(item) ? "✓" : "✗");
                    if (Boolean.TRUE.equals(item)) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    }
                }
            }
        };
    }

}
