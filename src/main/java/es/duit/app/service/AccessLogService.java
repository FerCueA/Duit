package es.duit.app.service;

import es.duit.app.entity.AccessLog;
import es.duit.app.entity.AppUser;
import es.duit.app.repository.AccessLogRepository;
import es.duit.app.repository.AppUserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// ============================================================================
// SERVICIO PARA REGISTRAR LOS ACCESOS DE USUARIOS (LOGINS EXITOSOS Y FALLIDOS)
// ============================================================================
@Service
@Transactional
public class AccessLogService {

    private final AccessLogRepository accessLogRepository;
    private final AppUserRepository appUserRepository;

    public AccessLogService(AccessLogRepository accessLogRepository, AppUserRepository appUserRepository) {
        this.accessLogRepository = accessLogRepository;
        this.appUserRepository = appUserRepository;
    }

    // ============================================================================
    // REGISTRA UN LOGIN EXITOSO DE UN USUARIO
    // ============================================================================
    public void saveSuccessfulLogin(String emailUsuario, HttpServletRequest request) {
        try {
            String emailNormalizado = normalizeEmail(emailUsuario);

            System.out.println("[AccessLog] Login exitoso recibido para: " + emailNormalizado);

            // Buscar el usuario en la BD
            AppUser usuario = appUserRepository.findByUsername(emailNormalizado)
                    .orElse(null);

            // Si no existe, salir
            if (usuario == null) {
                System.out.println("[AccessLog] Usuario no encontrado, no se guarda log: " + emailNormalizado);
                return;
            }

            // Crear registro de acceso exitoso
            AccessLog registroAcceso = new AccessLog();
            registroAcceso.setUser(usuario);
            registroAcceso.setSuccess(true);
            registroAcceso.setSourceIp(getUserIp(request));

            // Guardar en la BD
            accessLogRepository.save(registroAcceso);

            System.out.println("[AccessLog] Log guardado para usuario ID: " + usuario.getId());

            // Actualizar ultimo login del usuario
            usuario.setLastLoginAt(java.time.LocalDateTime.now());
            appUserRepository.save(usuario);

            System.out.println("[AccessLog] lastLoginAt actualizado para usuario ID: " + usuario.getId());

        } catch (Exception error) {
            // Si hay error, no hacer nada
            System.err.println("Error guardando login exitoso: " + error.getMessage());
        }
    }

    // ============================================================================
    // REGISTRA UN LOGIN FALLIDO (CREDENCIALES INCORRECTAS, ETC)
    // ============================================================================
    public void saveFailedLogin(String emailUsuario, HttpServletRequest request) {
        try {
            String emailNormalizado = normalizeEmail(emailUsuario);

            System.out.println("[AccessLog] Login fallido recibido para: " + emailNormalizado);

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

                System.out.println("[AccessLog] Log fallido guardado para usuario ID: " + usuario.getId());
            } else {
                System.out.println("[AccessLog] Usuario no encontrado, no se guarda log: " + emailNormalizado);
            }

        } catch (Exception error) {
            // Si hay error, no hacer nada
            System.err.println("Error guardando login fallido: " + error.getMessage());
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

    // ==========================================================================
    // NORMALIZA EMAIL PARA BUSQUEDAS CONSISTENTES
    // ==========================================================================
    private String normalizeEmail(String email) {
        if (email == null) {
            return "";
        }
        return email.trim().toLowerCase();
    }
}