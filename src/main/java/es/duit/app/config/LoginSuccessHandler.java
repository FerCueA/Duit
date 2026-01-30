package es.duit.app.config;

import es.duit.app.service.AccessLogService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final AccessLogService accessLogService;

    public LoginSuccessHandler(AccessLogService accessLogService) {
        this.accessLogService = accessLogService;
        setDefaultTargetUrl("/home");
        setAlwaysUseDefaultTargetUrl(true);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest peticion, HttpServletResponse respuesta,
            Authentication usuarioAutenticado) throws ServletException, IOException {

        String emailUsuario = usuarioAutenticado.getName();
        accessLogService.guardarLoginExitoso(emailUsuario, peticion);

        super.onAuthenticationSuccess(peticion, respuesta, usuarioAutenticado);
    }
}