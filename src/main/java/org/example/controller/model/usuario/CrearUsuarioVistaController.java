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
import org.example.exception.JsonNotFoundException;
import org.example.exception.NotFoundException;
import org.example.model.Profesor;
import org.example.model.Usuario;
import org.example.service.ProfesorService;
import org.example.service.RolService;
import org.example.service.UsuarioService;
import org.example.utils.TableUtils;
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
    private TableColumn<Profesor,Integer> colId;
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
    public void initialize() {
        try {
            var roles = rolService.listar();
            tipoRolComboBox.getItems().setAll(roles.stream().map(Object::toString).toList());

            TableUtils.inicializarTablaProfesores(colId,colNombre,colApellido,colMatricula);

            var profesores = profesorService.listar();
            ObservableList<Profesor> profesorObservableList = FXCollections.observableArrayList();
            profesorObservableList.addAll(profesores);
            tblProfesor.setItems(profesorObservableList);

            btnCrear.disableProperty().bind(tblProfesor.getSelectionModel().selectedItemProperty().isNull());
        }catch (JsonNotFoundException e){
            globalExceptionHandler.handleJsonNotFoundException(e);
        }
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
            usuarioService.guardar(new Usuario(0,username,password,rol,profesor));
            vistaUtils.mostrarAlerta("El usuario ha sido creado con éxito", Alert.AlertType.INFORMATION);
            vistaUtils.cerrarVentana(btnCrear);
        } catch (BadRequestException e) {
            globalExceptionHandler.handleBadRequestException(e);
        } catch (JsonNotFoundException e) {
            globalExceptionHandler.handleJsonNotFoundException(e);
        } catch (NotFoundException e) {
            globalExceptionHandler.handleNotFoundException(e);
        }
    }

    private Optional<List<String>> validarCampos() {
        List<String> errores = new ArrayList<>();

        // Validar username
        vistaUtils.validarTexto(usernameField, "Debes ingresar un nombre de usuario valido.", errores);

        // Validar password
        vistaUtils.validarPassword(contraseniaField, errores);

        if (!contraseniaField.getText().equals(repetirContraseniaField.getText())) {
            contraseniaField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            repetirContraseniaField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            errores.add("Las contraseñas deben coincidir.");
        } else{
            repetirContraseniaField.setStyle("-fx-border-color: transparent;");
        }

        vistaUtils.validarTabla(tblProfesor,"Debes seleccionar un profesor",errores);

        vistaUtils.validarComboBox(tipoRolComboBox,"Debes seleccionar un rol",errores);

        return errores.isEmpty() ? Optional.empty() : Optional.of(errores);
    }
}