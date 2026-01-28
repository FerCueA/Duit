package es.duit.app.service;

import es.duit.app.entity.AppUser;
import es.duit.app.repository.AppUserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AuthService {
    private final AppUserRepository appUserRepository;

    public AuthService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public AppUser obtenerUsuarioAutenticado(Authentication auth) {
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            List<AppUser> usuarios = appUserRepository.findByUsername(auth.getName());
            if (usuarios.isEmpty()) {
                throw new IllegalStateException("Usuario no encontrado");
            }
            return usuarios.get(0);
        }
        throw new IllegalStateException("Usuario no autenticado");
    }
}
