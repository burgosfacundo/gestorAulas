package org.example.exception;

import javafx.scene.control.Alert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.utils.VistaUtils;
import org.springframework.stereotype.Component;

import javax.naming.AuthenticationException;
import java.io.IOException;

@Slf4j
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

    public void handleIOException(IOException e) {
        vistaUtils.mostrarAlerta( "Ocurrió un problema, inténtelo en unos minutos.",Alert.AlertType.ERROR);
        log.error( e.getMessage(),e);
    }
}

