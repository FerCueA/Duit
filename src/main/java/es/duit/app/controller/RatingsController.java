package es.duit.app.controller;

import es.duit.app.entity.AppUser;
import es.duit.app.entity.Rating;
import es.duit.app.entity.ServiceJob;
import es.duit.app.repository.RatingRepository;
import es.duit.app.repository.ServiceJobRepository;
import es.duit.app.service.AuthService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@Controller
@RequestMapping("/ratings")
public class RatingsController {

    private final RatingRepository ratingRepository;
    private final ServiceJobRepository serviceJobRepository;
    private final AuthService authService;

    public RatingsController(
            RatingRepository ratingRepository,
            ServiceJobRepository serviceJobRepository,
            AuthService authService) {
        this.ratingRepository = ratingRepository;
        this.serviceJobRepository = serviceJobRepository;
        this.authService = authService;
    }

    @PostMapping("/crear")
    public String crearValoracion(
            @RequestParam Long jobId,
            @RequestParam Integer score,
            @RequestParam(required = false) String comment,
            Authentication auth) {
        
        AppUser usuarioLogueado = authService.obtenerUsuarioAutenticado(auth);
        ServiceJob trabajo = serviceJobRepository.findById(jobId).orElse(null);

        if (trabajo == null) {
            return "redirect:/shared/valoraciones";
        }

        // Determinar el tipo de valoración según quien sea el usuario
        Rating.Type type;

        if (trabajo.getClient().getId().equals(usuarioLogueado.getId())) {
            // Cliente valorando al profesional
            type = Rating.Type.CLIENT_TO_PROFESSIONAL;
        } else if (trabajo.getProfessional().getId().equals(usuarioLogueado.getId())) {
            // Profesional valorando al cliente
            type = Rating.Type.PROFESSIONAL_TO_CLIENT;
        } else {
            // El usuario no está autorizado para valorar este trabajo
            return "redirect:/shared/valoraciones";
        }

        // Crear la valoración
        Rating nuevaValoracion = new Rating();
        nuevaValoracion.setJob(trabajo);
        nuevaValoracion.setType(type);
        nuevaValoracion.setScore(score);
        nuevaValoracion.setComment(comment != null && !comment.trim().isEmpty() ? comment : null);
        nuevaValoracion.setStatus(Rating.Status.PUBLISHED);
        ratingRepository.save(nuevaValoracion);

        return "redirect:/shared/valoraciones";
    }
}
