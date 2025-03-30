package org.example.security;


import org.example.enums.Permisos;
import org.example.model.Usuario;
import org.example.service.UsuarioService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.naming.AuthenticationException;

/**
 * Clase de seguridad de la app
 * Se encarga del inicio de Sesión y de validar permisos
 */
@Component
public class Seguridad {
    private final UsuarioService usuarioService;

    @Autowired
    public Seguridad(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }


    /**
     * Método para iniciar sesión
     * @param username nombre de usuario
     * @param password contraseña
     * @return Usuario que inicia sesión
     * @throws AuthenticationException si el username o password son incorrectos
     */
    public Usuario autenticar(String username, String password) throws AuthenticationException {
        //Verificamos si existe un usuario con ese username y password a través de un stream
        return usuarioService.listar().stream()
                .filter(usuario -> usuario.getUsername().equals(username) &&
                        BCrypt.checkpw(password, usuario.getPassword())) // decodifico la contraseña para validar
                .findFirst()
                .orElseThrow(() -> new AuthenticationException("Usuario o contraseña incorrecto."));
    }

    /**
     * Método para validar permisos del usuario
     * @param usuario usuario que quiere realizar una acción
     * @param permisos enum que indica que es lo que quiere hacer el usuario
     * @return boolean que indica si tiene permisos o no
     */
    public boolean verificarPermiso(Usuario usuario, Permisos permisos) {
        //Llamamos al método que devuelve si el usuario tiene permiso según su Rol
        return usuario.getRol().tienePermiso(permisos);
    }
}

