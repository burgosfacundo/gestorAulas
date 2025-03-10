package org.example.utils;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.experimental.UtilityClass;
import org.example.enums.BloqueHorario;
import org.example.model.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

@UtilityClass
public class TableUtils {
public void inicializarTablaUsuarios(TableColumn<Usuario, Integer> colId,
                                     TableColumn<Usuario, String> colUsername,
                                     TableColumn<Usuario, String> colNombre,
                                     TableColumn<Usuario, String> colApellido,
                                     TableColumn<Usuario, String> colMatricula){
    colId.setCellValueFactory(new PropertyValueFactory<>("id"));
    colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
    colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProfesor().getNombre()));
    colApellido.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProfesor().getApellido()));
    colMatricula.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProfesor().getMatricula()));
}

    public static void inicializarTablaEspacio(TableColumn<Aula, Integer> colId,
                                               TableColumn<Aula, Integer> colNum,
                                               TableColumn<Aula, Integer> colCapacidad,
                                               TableColumn<Aula, Boolean> colTieneProyector,
                                               TableColumn<Aula, Boolean> colTieneTV,
                                               TableColumn<Laboratorio, Integer> colComputadoras) {
        inicializarTablaAula(colId, colNum, colCapacidad, colTieneProyector, colTieneTV);
        // Configurar la columna de computadoras solo para Laboratorio
        colComputadoras.setCellValueFactory(cellData -> {
            if (cellData.getValue() instanceof Laboratorio lab) {
                return new SimpleObjectProperty<>(lab.getComputadoras());
            } else {
                return null;
            }
        });
        colComputadoras.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow().getItem() == null) {
                    setText(null);
                    setStyle("");
                } else if (item == null) {
                    setText("✗");
                    setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                } else {
                    setText(item.toString());
                    setStyle("");
                }
            }
        });
    }


    public void inicializarTablaAula(TableColumn<Aula, Integer> colId,
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

    public void inicializarTablaLaboratorio(TableColumn<Laboratorio, Integer> colId,
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

    public void inicializarTablaSolicitudes(TableColumn<SolicitudCambioAula,Integer> colId,
                                            TableColumn<SolicitudCambioAula,String> colReserva,
                                            TableColumn<SolicitudCambioAula,String> colAula,
                                            TableColumn<SolicitudCambioAula, String> colEstado,
                                            TableColumn<SolicitudCambioAula, String> colTipo,
                                            TableColumn<SolicitudCambioAula, LocalDate> colInicio,
                                            TableColumn<SolicitudCambioAula, LocalDate> colFin,
                                            TableColumn<SolicitudCambioAula, Map<DayOfWeek, Set<BloqueHorario>>> colDiaHorario,
                                            TableColumn<SolicitudCambioAula,String> colComenProfe,
                                            TableColumn<SolicitudCambioAula,String> colComenAdmin){
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

    public void inicializarTablaReserva(TableColumn<Reserva,Integer> colId,
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

    public void inicializarTablaInscripcion(TableColumn<Inscripcion, Integer> colId,
                                                   TableColumn<Inscripcion, Integer> colAlumnos,
                                                   TableColumn<Inscripcion, Integer> colMargenAlumnos,
                                                   TableColumn<Inscripcion, LocalDate> colFechaFinInscripcion,
                                                   TableColumn<Inscripcion, String> colAsignatura,
                                                   TableColumn<Inscripcion, String> colComision,
                                                   TableColumn<Inscripcion, String> colProfesor){
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colAlumnos.setCellValueFactory(new PropertyValueFactory<>("cantidadAlumnos"));
        colMargenAlumnos.setCellValueFactory(new PropertyValueFactory<>("margenAlumnos"));
        colFechaFinInscripcion.setCellValueFactory(new PropertyValueFactory<>("fechaFinInscripcion"));
        colAsignatura.setCellValueFactory(new PropertyValueFactory<>("asignatura"));
        colComision.setCellValueFactory(new PropertyValueFactory<>("comision"));
        colProfesor.setCellValueFactory(new PropertyValueFactory<>("profesor"));
    }

    private <T extends Aula> TableCell<T, Boolean> columnTic(TableColumn<T, Boolean> column) {
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
