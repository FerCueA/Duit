package es.duit.app.config;

import es.duit.app.service.AccessLogService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final AccessLogService accessLogService;

    public LoginFailureHandler(AccessLogService accessLogService) {
        this.accessLogService = accessLogService;
        setDefaultFailureUrl("/login?error=true");
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest peticion, HttpServletResponse respuesta,
            AuthenticationException excepcion) throws IOException, ServletException {

        String emailUsuario = peticion.getParameter("username");

        if (emailUsuario != null && !emailUsuario.trim().isEmpty()) {
            accessLogService.guardarLoginFallido(emailUsuario, peticion);
        }

        super.onAuthenticationFailure(peticion, respuesta, excepcion);
    }
}