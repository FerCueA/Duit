package es.duit.app.service;

import es.duit.app.entity.AccessLog;
import es.duit.app.entity.AppUser;
import es.duit.app.repository.AccessLogRepository;
import es.duit.app.repository.AppUserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AccessLogService {

    private static final Logger logger = LoggerFactory.getLogger(AccessLogService.class);

    private final AccessLogRepository accessLogRepository;
    private final AppUserRepository appUserRepository;

    public AccessLogService(AccessLogRepository accessLogRepository, AppUserRepository appUserRepository) {
        this.accessLogRepository = accessLogRepository;
        this.appUserRepository = appUserRepository;
    }

    public void guardarLoginExitoso(String emailUsuario, HttpServletRequest request) {
        try {

            AppUser usuario = appUserRepository.findByUsername(emailUsuario)
                    .orElse(null);

            if (usuario == null) {
                logger.warn("No se pudo registrar login: usuario no encontrado: {}", emailUsuario);
                return;
            }

            AccessLog registroAcceso = new AccessLog();
            registroAcceso.setUser(usuario);
            registroAcceso.setSuccess(true);
            registroAcceso.setSourceIp(obtenerIpDelUsuario(request));

            accessLogRepository.save(registroAcceso);

            logger.info("Usuario {} hizo login exitoso desde IP: {}", emailUsuario, registroAcceso.getSourceIp());

        } catch (Exception error) {

            logger.error("Error guardando login exitoso para {}: {}", emailUsuario, error.getMessage());
        }
    }

    public void guardarLoginFallido(String emailUsuario, HttpServletRequest request) {
        try {

            AppUser usuario = appUserRepository.findByUsername(emailUsuario).orElse(null);

            if (usuario != null) {

                AccessLog registroAcceso = new AccessLog();
                registroAcceso.setUser(usuario);
                registroAcceso.setSuccess(false);
                registroAcceso.setSourceIp(obtenerIpDelUsuario(request));

                accessLogRepository.save(registroAcceso);

                logger.warn("Login fallido para usuario {} desde IP: {}", emailUsuario, registroAcceso.getSourceIp());
            }

        } catch (Exception error) {
            logger.error("Error guardando login fallido para {}: {}", emailUsuario, error.getMessage());
        }
    }

    private String obtenerIpDelUsuario(HttpServletRequest request) {
        String ip = null;

        ip = request.getHeader("X-Forwarded-For");

        if (ipEsInvalida(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if (ipEsInvalida(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ipEsInvalida(ip)) {
            ip = request.getRemoteAddr();
        }

        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

    private boolean ipEsInvalida(String ip) {
        return ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip);
    }
}