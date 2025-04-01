package org.example.controller.model.usuario;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.BadRequestException;
import org.example.exception.GlobalExceptionHandler;
import org.example.exception.NotFoundException;
import org.example.model.Profesor;
import org.example.model.dto.UsuarioDTO;
import org.example.service.ProfesorService;
import org.example.service.RolService;
import org.example.service.UsuarioService;
import org.example.utils.TableUtils;
import org.example.utils.Utils;
import org.example.utils.VistaUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class CrearUsuarioVistaController {
    private final VistaUtils vistaUtils;
    private final UsuarioService usuarioService;
    private final RolService rolService;
    private final GlobalExceptionHandler globalExceptionHandler;
    private final ProfesorService profesorService;
    @FXML
    private TableView<Profesor> tblProfesor;
    @FXML
    private TableColumn<Profesor,String>  colNombre;
    @FXML
    private TableColumn<Profesor,String>  colApellido;
    @FXML
    private TableColumn<Profesor,String>  colMatricula;
    @FXML
    private ComboBox<String> tipoRolComboBox;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField contraseniaField;
    @FXML
    private PasswordField repetirContraseniaField;
    @FXML
    private Button btnCrear;
    @FXML
    private Pagination pagination;
    private List<Profesor> profesores;
    private static final int PAGE_SIZE = 10;

    @FXML
    public void initialize() {
        var roles = rolService.listar();
        tipoRolComboBox.getItems().setAll(roles.stream().map(Object::toString).toList());

        TableUtils.inicializarTablaProfesores(colNombre,colApellido,colMatricula);
        btnCrear.disableProperty().bind(tblProfesor.getSelectionModel().selectedItemProperty().isNull());

        profesores = profesorService.listar();
        int totalPages = (int) Math.ceil((double) profesores.size() / PAGE_SIZE);
            pagination.setPageCount(Math.max(totalPages, 1));

            pagination.currentPageIndexProperty().addListener(
                    (obs, oldIndex, newIndex) -> cargarPagina(newIndex.intValue()));
        cargarPagina(0);
    }

    private void cargarPagina(int pageIndex) {
        int fromIndex = pageIndex * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, profesores.size());

        ObservableList<Profesor> profesoresObservableList = FXCollections.observableArrayList();
        profesoresObservableList.addAll(profesores.subList(fromIndex, toIndex));
        tblProfesor.setItems(profesoresObservableList);
    }

    @FXML
    private void crear(ActionEvent actionEvent) {
        try {
            var errores = validarCampos();
            if (errores.isPresent()) {
                vistaUtils.mostrarAlerta(String.join("\n", errores.get()), Alert.AlertType.ERROR);
                return;
            }

            var username = usernameField.getText();
            var password = contraseniaField.getText();
            var rol = rolService.validarRolPorNombre(tipoRolComboBox.getSelectionModel().getSelectedItem());
            var profesor = tblProfesor.getSelectionModel().getSelectedItem();
            usuarioService.guardar(new UsuarioDTO(null,username,password,rol.getId(),profesor.getId()));
            vistaUtils.mostrarAlerta("El usuario ha sido creado con éxito", Alert.AlertType.INFORMATION);
            vistaUtils.cerrarVentana(btnCrear);
        } catch (BadRequestException e) {
            globalExceptionHandler.handleBadRequestException(e);
        } catch (NotFoundException e) {
            globalExceptionHandler.handleNotFoundException(e);
        }
    }

    private Optional<List<String>> validarCampos() {
        List<String> errores = new ArrayList<>();

        // Validar username
        Utils.validarTexto(usernameField, "Debes ingresar un nombre de usuario valido.", errores);

        // Validar password
        Utils.validarPassword(contraseniaField, errores);

        if (!contraseniaField.getText().equals(repetirContraseniaField.getText())) {
            contraseniaField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            repetirContraseniaField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            errores.add("Las contraseñas deben coincidir.");
        } else{
            repetirContraseniaField.setStyle("-fx-border-color: transparent;");
        }

        Utils.validarTabla(tblProfesor,"Debes seleccionar un profesor",errores);

        Utils.validarComboBox(tipoRolComboBox,"Debes seleccionar un rol",errores);

        return errores.isEmpty() ? Optional.empty() : Optional.of(errores);
    }
}