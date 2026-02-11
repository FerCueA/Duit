package es.duit.app.config;

import es.duit.app.service.AccessLogService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

// ============================================================================
// ESTA CLASE MANEJA LO QUE PASA CUANDO UN USUARIO HACE LOGIN CORRECTAMENTE
// ============================================================================
@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final AccessLogService accessLogService;

    public LoginSuccessHandler(AccessLogService accessLogService) {
        this.accessLogService = accessLogService;
        setDefaultTargetUrl("/home");
        setAlwaysUseDefaultTargetUrl(true);
    }

    // Este método se llama automáticamente cuando el login es EXITOSO
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest peticion,
            HttpServletResponse respuesta,
            Authentication usuarioAutenticado) throws ServletException, IOException {

        // ====== EXTRAER EL EMAIL DEL USUARIO ======
        String emailDelUsuario = usuarioAutenticado.getName();

        System.out.println("[LoginSuccessHandler] Login OK para: " + emailDelUsuario);

        // ====== GUARDAR EN LA BD QUE ESTE USUARIO HIZO LOGIN ======
        accessLogService.saveSuccessfulLogin(emailDelUsuario, peticion);

        // ====== DEJAR QUE SPRING HAGA LO SUYO ======
        super.onAuthenticationSuccess(peticion, respuesta, usuarioAutenticado);
    }
}