package es.duit.app.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.duit.app.entity.AccessLog;
import es.duit.app.entity.AppUser;
import es.duit.app.repository.AccessLogRepository;
import es.duit.app.repository.AppUserRepository;
import es.duit.app.service.util.IdentityNormalizer;

// ============================================================================
// SERVICIO PARA REGISTRAR LOS ACCESOS DE USUARIOS (LOGINS EXITOSOS Y FALLIDOS)
// ============================================================================
@Service
@Transactional
@RequiredArgsConstructor
public class AccessLogService {

    private static final Logger LOG = LoggerFactory.getLogger(AccessLogService.class);

    private final AccessLogRepository accessLogRepository;
    private final AppUserRepository appUserRepository;
    private final IdentityNormalizer identityNormalizer;

    // ============================================================================
    // REGISTRA UN LOGIN EXITOSO DE UN USUARIO
    // ============================================================================
    public void saveSuccessfulLogin(String emailUsuario, HttpServletRequest request) {
        try {
            String emailNormalizado = identityNormalizer.normalizeEmail(emailUsuario);

            // Buscar el usuario en la BD
            AppUser usuario = appUserRepository.findByUsername(emailNormalizado)
                    .orElse(null);

            // Si no existe, salir
            if (usuario == null) {
                return;
            }

            // Crear registro de acceso exitoso
            AccessLog registroAcceso = new AccessLog();
            registroAcceso.setUser(usuario);
            registroAcceso.setSuccess(true);
            registroAcceso.setSourceIp(getUserIp(request));

            // Guardar en la BD
            accessLogRepository.save(registroAcceso);

            // Actualizar ultimo login del usuario
            usuario.setLastLoginAt(java.time.LocalDateTime.now());
            appUserRepository.save(usuario);

        } catch (Exception error) {
            LOG.warn("No se pudo registrar login exitoso para email='{}': {}", emailUsuario, error.getMessage());
        }
    }

    // ============================================================================
    // REGISTRA UN LOGIN FALLIDO (CREDENCIALES INCORRECTAS, ETC)
    // ============================================================================
    public void saveFailedLogin(String emailUsuario, HttpServletRequest request) {
        try {
            String emailNormalizado = identityNormalizer.normalizeEmail(emailUsuario);

            // Buscar el usuario en la BD
            AppUser usuario = appUserRepository.findByUsername(emailNormalizado).orElse(null);

            // Solo guardar si el usuario existe
            if (usuario != null) {
                // Crear registro de acceso fallido
                AccessLog registroAcceso = new AccessLog();
                registroAcceso.setUser(usuario);
                registroAcceso.setSuccess(false);
                registroAcceso.setSourceIp(getUserIp(request));

                // Guardar en la BD
                accessLogRepository.save(registroAcceso);
            }

        } catch (Exception error) {
            LOG.warn("No se pudo registrar login fallido para email='{}': {}", emailUsuario, error.getMessage());
        }
    }

    // ============================================================================
    // OBTIENE LA DIRECCIÓN IP REAL DEL USUARIO (CONSIDERANDO PROXIES)
    // ============================================================================
    private String getUserIp(HttpServletRequest request) {
        String ip = null;

        // Intentar obtener IP de X-Forwarded-For
        ip = request.getHeader("X-Forwarded-For");

        // Si no es válida, intentar Proxy-Client-IP
        if (isInvalidIp(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        // Si no es válida, intentar WL-Proxy-Client-IP
        if (isInvalidIp(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        // Si sigue inválida, usar IP directa
        if (isInvalidIp(ip)) {
            ip = request.getRemoteAddr();
        }

        // Si tiene múltiples IPs, tomar la primera
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

    // ============================================================================
    // VERIFICA SI UNA IP ES VÁLIDA
    // ============================================================================
    private boolean isInvalidIp(String ip) {
        return ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip);
    }

}