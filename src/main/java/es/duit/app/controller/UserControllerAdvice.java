package es.duit.app.controller;

import es.duit.app.entity.AppUser;
import es.duit.app.repository.AppUserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Pone el usuario en todas las vistas
@ControllerAdvice
public class UserControllerAdvice {

    // Para loguear si algo falla
    private static final Logger logger = LoggerFactory.getLogger(UserControllerAdvice.class);

    // Repositorio para buscar usuarios
    private final AppUserRepository appUserRepository;

    public UserControllerAdvice(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    // Agregar usuario al modelo
    @ModelAttribute("usuario")
    public AppUser agregarUsuarioAlModelo() {
        try {
            // Obtener usuario autenticado
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
            return appUserRepository.findByUsername(nombreUsuario)
                    .orElseGet(() -> {
                        logger.warn("Usuario autenticado pero no encontrado en BD: {}", nombreUsuario);
                        return null;
                    });
        } catch (Exception error) {
            logger.error("Error al obtener el usuario autenticado: {}", error.getMessage());
            return null;
        }
    }
}