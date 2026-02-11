package es.duit.app.config;

import es.duit.app.service.AccessLogService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

// ============================================================================
// ESTA CLASE MANEJA LO QUE PASA CUANDO UN USUARIO FALLA AL HACER LOGIN
// ============================================================================
@Component
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final AccessLogService accessLogService;

    public LoginFailureHandler(AccessLogService accessLogService) {
        this.accessLogService = accessLogService;
        setDefaultFailureUrl("/login?error=true");
    }

    // Este método se llama automáticamente cuando el login FALLA
    @Override
    public void onAuthenticationFailure(
            HttpServletRequest peticion,
            HttpServletResponse respuesta,
            AuthenticationException excepcion) throws IOException, ServletException {

        // ====== EXTRAER EL EMAIL QUE EL USUARIO INTENTÓ USAR ======
        String emailIntentado = peticion.getParameter("username");

        System.out.println("[LoginFailureHandler] Login FALLIDO para: " + emailIntentado);

        // ====== VALIDAR QUE EL EMAIL NO ESTÉ VACÍO ======
        boolean emailEsValido = (emailIntentado != null && !emailIntentado.trim().isEmpty());

        // ====== SI EL EMAIL ES VÁLIDO, GUARDAR EL INTENTO FALLIDO ======
        if (emailEsValido) {
            accessLogService.saveFailedLogin(emailIntentado, peticion);
        }

        // ====== DEJAR QUE SPRING HAGA LO SUYO ======
        super.onAuthenticationFailure(peticion, respuesta, excepcion);
    }
}