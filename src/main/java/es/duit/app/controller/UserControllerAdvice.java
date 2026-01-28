package es.duit.app.controller;

import es.duit.app.entity.AppUser;
import es.duit.app.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Esta clase pone el usuario actual en todas las vistas automáticamente
@ControllerAdvice
public class UserControllerAdvice {

    // Para loguear si algo falla
    private static final Logger logger = LoggerFactory.getLogger(UserControllerAdvice.class);

    // Repositorio para buscar usuarios
    @Autowired
    private AppUserRepository appUserRepository;

    // Este método se ejecuta en cada página y pone el usuario en el modelo
    @ModelAttribute("usuario")
    public AppUser agregarUsuarioAlModelo() {
        try {
            // Obtengo el usuario que está logueado en Spring Security
            Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();

            // Verifico que esté autenticado y no sea anónimo
            if (autenticacion == null || !autenticacion.isAuthenticated()) {
                return null;
            }

            String nombreUsuario = autenticacion.getName();
            if ("anonymousUser".equals(nombreUsuario)) {
                return null;
            }

            // Busco el usuario en la base de datos
            List<AppUser> usuariosEncontrados = appUserRepository.findByUsername(nombreUsuario);
            if (usuariosEncontrados.isEmpty()) {
                logger.warn("Usuario autenticado pero no encontrado en BD: {}", nombreUsuario);
                return null;
            }

            // Devuelvo el usuario encontrado
            return usuariosEncontrados.get(0);

        } catch (Exception error) {
            logger.error("Error al obtener el usuario autenticado: {}", error.getMessage());
            return null;
        }
    }
}