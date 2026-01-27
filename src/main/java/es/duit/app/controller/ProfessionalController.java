package es.duit.app.controller;

import es.duit.app.entity.AppUser;
import es.duit.app.repository.AppUserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para profesionales que buscan trabajos.
 * Maneja búsqueda de trabajos y postulaciones.
 */
@Controller
@RequestMapping("/professional")
public class ProfessionalController {

    private final AppUserRepository appUserRepository;

    public ProfessionalController(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    // Obtener usuario autenticado
    private AppUser obtenerUsuarioAutenticado(Authentication auth) {
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            List<AppUser> usuarios = appUserRepository.findByUsername(auth.getName());
            if (usuarios.isEmpty()) {
                throw new IllegalStateException("Usuario no encontrado");
            }
            return usuarios.get(0);
        }
        throw new IllegalStateException("Usuario no autenticado");
    }

    // Mostrar página para buscar trabajos
    @GetMapping("/buscar")
    public String buscarTrabajos(Authentication auth) {
        obtenerUsuarioAutenticado(auth);
        return "jobs/buscar";
    }

    // Mostrar postulaciones del profesional
    @GetMapping("/postulaciones")
    public String verPostulaciones(Authentication auth) {
        obtenerUsuarioAutenticado(auth);
        return "jobs/postular";
    }
}
