package es.duit.app.controller;

import es.duit.app.entity.AppUser;
import es.duit.app.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class UserControllerAdvice {

    @Autowired
    private AppUserRepository appUserRepository;

    @ModelAttribute("usuario")
    public AppUser addUserToModel() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                String username = auth.getName();
                return appUserRepository.findByUsername(username).orElse(null);
            }
        } catch (Exception e) {
            System.err.println("Error al obtener usuario: " + e.getMessage());
        }
        return null;
    }
}