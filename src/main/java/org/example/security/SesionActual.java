package org.example.security;

import org.example.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class SesionActual {
    private Usuario usuario;

    public Usuario getUsuarioActual() {
        return usuario;
    }

    public void setUsuarioActual(Usuario usuario) {
        this.usuario = usuario;
    }
}

