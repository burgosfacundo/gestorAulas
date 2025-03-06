package org.example.exception;

import javafx.scene.control.Alert;
import lombok.RequiredArgsConstructor;
import org.example.utils.VistaUtils;
import org.springframework.stereotype.Component;

import javax.naming.AuthenticationException;

@Component
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final VistaUtils vistaUtils;
    
    public void handleBadRequestException(BadRequestException e) {
        vistaUtils.mostrarAlerta( e.getMessage(),Alert.AlertType.ERROR);
    }
    public void handleNotFoundException(NotFoundException e) {
        vistaUtils.mostrarAlerta( e.getMessage(),Alert.AlertType.ERROR);
    }

    public void handleAuthenticationException(AuthenticationException e) {
        vistaUtils.mostrarAlerta( e.getMessage(),Alert.AlertType.ERROR);
    }

    public void handleConflictException(ConflictException e){
        vistaUtils.mostrarAlerta( e.getMessage(),Alert.AlertType.ERROR);
    }

    public void handleJsonNotFoundException(JsonNotFoundException e) {
        vistaUtils.mostrarAlerta( e.getMessage(),Alert.AlertType.ERROR);
    }
}

