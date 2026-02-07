package es.duit.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * CONFIGURACIÓN DE AUDITORÍA JPA
 * Habilita el seguimiento automático de fechas de creación y modificación
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}