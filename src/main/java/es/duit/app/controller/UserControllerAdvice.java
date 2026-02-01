package es.duit.app.controller;

import es.duit.app.entity.AppUser;
import es.duit.app.repository.AppUserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

// ============================================================================
// ASESOR GLOBAL DE CONTROLADORES - AGREGA DATOS COMUNES A TODAS LAS VISTAS
// ============================================================================
@ControllerAdvice
public class UserControllerAdvice {

    private final AppUserRepository appUserRepository;

    public UserControllerAdvice(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    // ============================================================================
    // AGREGA EL USUARIO AUTENTICADO AL MODELO DE TODAS LAS VISTAS
    // ============================================================================
    @ModelAttribute("usuario")
    public AppUser agregarUsuarioAutenticadoAlModelo() {
        try {
            // Obtener información de autenticación del contexto de seguridad
            Authentication autenticacionActual = obtenerAutenticacionActual();

            // Validar que exista autenticación válida
            boolean tieneAutenticacionValida = validarAutenticacionExistente(autenticacionActual);
            if (!tieneAutenticacionValida) {
                return null;
            }

            // Obtener el nombre de usuario autenticado
            String nombreUsuarioAutenticado = extraerNombreUsuario(autenticacionActual);

            // Validar que no sea usuario anónimo
            boolean esUsuarioAnonimo = verificarUsuarioAnonimo(nombreUsuarioAutenticado);
            if (esUsuarioAnonimo) {
                return null;
            }

            // Buscar y retornar usuario desde la base de datos
            return buscarUsuarioEnBaseDatos(nombreUsuarioAutenticado);

        } catch (Exception error) {
            // Manejar errores inesperados en la obtención del usuario
            return null;
        }
    }

    // ============================================================================
    // OBTIENE LA AUTENTICACIÓN ACTUAL DEL CONTEXTO DE SEGURIDAD
    // ============================================================================
    private Authentication obtenerAutenticacionActual() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    // ============================================================================
    // VALIDA QUE EXISTA UNA AUTENTICACIÓN VÁLIDA
    // ============================================================================
    private boolean validarAutenticacionExistente(Authentication autenticacion) {
        // Verificar que la autenticación no sea nula
        if (autenticacion == null) {
            return false;
        }

        // Verificar que el usuario esté realmente autenticado
        return autenticacion.isAuthenticated();
    }

    // ============================================================================
    // EXTRAE EL NOMBRE DE USUARIO DE LA AUTENTICACIÓN
    // ============================================================================
    private String extraerNombreUsuario(Authentication autenticacion) {
        return autenticacion.getName();
    }

    // ============================================================================
    // VERIFICA SI EL USUARIO ES ANÓNIMO
    // ============================================================================
    private boolean verificarUsuarioAnonimo(String nombreUsuario) {
        return "anonymousUser".equals(nombreUsuario);
    }

    // ============================================================================
    // BUSCA EL USUARIO EN LA BASE DE DATOS
    // ============================================================================
    private AppUser buscarUsuarioEnBaseDatos(String nombreUsuario) {
        return appUserRepository.findByUsername(nombreUsuario).orElse(null);
    }
}