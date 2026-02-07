package es.duit.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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

}