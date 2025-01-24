package org.example.security;

import lombok.Getter;
import lombok.Setter;
import org.example.model.Usuario;
import org.springframework.stereotype.Component;

@Component
@Getter @Setter
public class SesionActual {
    private Usuario usuario;
}

