package es.duit.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

/// ============================================================================
//  CONFIGURACIÓN DE JPA AUDITING - GESTIONA LOS CAMPOS DE AUDITORÍA createdBy/updatedBy
// ============================================================================
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfig {

    // ============================================================================
    // PROVEEDOR DE AUDITORÍA - OBTIENE EL USUARIO ACTUAL PARA LOS CAMPOS
    // createdBy/updatedBy
    // ============================================================================
    @Bean
    public AuditorAware<String> auditorProvider() {

        return new AuditorAware<String>() {

            @Override
            @NonNull
            @SuppressWarnings("null")
            public Optional<String> getCurrentAuditor() {
                try {
                    // Verificar para el obtener el usuario autenticado
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                    // Si no hay autenticación o no está autenticada
                    if (authentication == null || !authentication.isAuthenticated()) {
                        // No hay usuario logueado, probablemente operación del sistema
                        return Optional.of("system");
                    }

                    // Obtener el nombre de usuario autenticado (en nuestro caso, el email)
                    String nombreUsuario = authentication.getName();

                    // Verificar que no sea usuario anónimo para el caso de operaciones sin autenticación
                    // Esto puede ocurrir en el Registro de nuevos usuarios
                    if ("anonymousUser".equals(nombreUsuario)) {
                        // Intentar recuperar el email del formulario (registro o login)
                        String emailFormulario = getEmailFromRequest();
                        if (emailFormulario != null && !emailFormulario.isEmpty()) {
                            return Optional.of(emailFormulario);
                        }

                        return Optional.of("anonymous");
                    }

                    return Optional.of(nombreUsuario);

                } catch (Exception error) {
                    System.err.println("Error obteniendo usuario para auditoría: " + error.getMessage());
                    return Optional.of("unknown");
                }
            }
        };
    }

    // ==========================================================================
    // INTENTA LEER EL EMAIL DESDE EL REQUEST ACTUAL
    // ==========================================================================
    private String getEmailFromRequest() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) {
                return "";
            }

            HttpServletRequest request = attrs.getRequest();
            if (request == null) {
                return "";
            }

            // En registro viene como "email"; en login como "username"
            String email = request.getParameter("email");
            if (email == null || email.trim().isEmpty()) {
                email = request.getParameter("username");
            }

            if (email == null) {
                return "";
            }

            return email.trim().toLowerCase();

        } catch (Exception error) {
            return "";
        }
    }

}