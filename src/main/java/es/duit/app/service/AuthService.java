package es.duit.app.service;

import es.duit.app.entity.AppUser;
import es.duit.app.repository.AppUserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

// ============================================================================
// SERVICIO DE AUTENTICACIÓN - GESTIONA USUARIOS AUTENTICADOS
// ============================================================================
@Service
public class AuthService {

    private final AppUserRepository appUserRepository;

    public AuthService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    // ============================================================================
    // OBTIENE EL USUARIO AUTENTICADO DESDE EL CONTEXTO DE SEGURIDAD
    // ============================================================================
    public AppUser getAuthenticatedUser(Authentication auth) {
        // Verificar si hay autenticación
        if (auth == null) {
            throw new IllegalStateException("Usuario no autenticado");
        }

        // Verificar si está autenticado
        boolean estaAutenticado = auth.isAuthenticated();
        if (!estaAutenticado) {
            throw new IllegalStateException("Usuario no autenticado");
        }

        // Obtener el nombre de usuario
        String username = auth.getName();

        // Verificar que no sea usuario anónimo
        if ("anonymousUser".equals(username)) {
            throw new IllegalStateException("Usuario no autenticado");
        }

        // Buscar usuario en la base de datos
        Optional<AppUser> userOptional = appUserRepository.findByUsername(username);

        // Verificar si existe
        if (userOptional.isEmpty()) {
            throw new IllegalStateException("Usuario no encontrado");
        }

        // Devolver el usuario
        return userOptional.get();
    }
}
