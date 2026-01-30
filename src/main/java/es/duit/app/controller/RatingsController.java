package es.duit.app.controller;

import es.duit.app.entity.AppUser;
import es.duit.app.service.AuthService;
import es.duit.app.service.RatingService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ratings")
public class RatingsController {

    private final RatingService ratingService;
    private final AuthService authService;

    public RatingsController(RatingService ratingService, AuthService authService) {
        this.ratingService = ratingService;
        this.authService = authService;
    }

    // Crear valoración
    @PostMapping("/crear")
    public String crearValoracion(
            @RequestParam Long jobId,
            @RequestParam Integer score,
            @RequestParam(required = false) String comment,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        
        try {
            AppUser usuario = authService.obtenerUsuarioAutenticado(auth);
            ratingService.crearValoracion(jobId, score, comment, usuario);
            redirectAttributes.addFlashAttribute("success", "Valoración registrada correctamente.");
            return "redirect:/shared/valoraciones";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/shared/valoraciones";
        }
    }
}
