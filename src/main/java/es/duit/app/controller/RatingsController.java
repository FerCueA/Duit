package es.duit.app.controller;

import es.duit.app.entity.AppUser;
import es.duit.app.entity.Rating;
import es.duit.app.service.AuthService;
import es.duit.app.service.RatingService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// ============================================================================
// CONTROLADOR DE VALORACIONES - GESTIONA CREACIÓN Y GESTIÓN DE VALORACIONES
// ============================================================================
@Controller
@RequestMapping("/ratings")
public class RatingsController {

    private final RatingService ratingService;
    private final AuthService authService;

    public RatingsController(RatingService ratingService, AuthService authService) {
        this.ratingService = ratingService;
        this.authService = authService;
    }

    // ============================================================================
    // CREA UNA NUEVA VALORACIÓN PARA UN TRABAJO COMPLETADO
    // ============================================================================
    @PostMapping("/crear")
    public String crearNuevaValoracion(@RequestParam Long jobId,
            @RequestParam Integer score,
            @RequestParam(required = false) String comment,
            Authentication auth,
            RedirectAttributes redirectAttributes) {

        try {
            // Obtener el usuario actualmente logueado
            AppUser usuarioLogueado = authService.getAuthenticatedUser(auth);

            // Normalizar el comentario si existe
            String comentarioNormalizado = (comment == null || comment.trim().isEmpty()) ? null : comment.trim();

            // Crear la valoración usando el servicio
            Rating valoracionCreada = ratingService.createRating(jobId, score, comentarioNormalizado, usuarioLogueado);

            // Preparar mensaje de éxito y redirigir
            String mensajeExito = "Valoración registrada correctamente.";
            redirectAttributes.addFlashAttribute("success", mensajeExito);
            return "redirect:/shared/ratings";

        } catch (IllegalArgumentException error) {
            // Manejar errores de validación
            redirectAttributes.addFlashAttribute("error", error.getMessage());
            return "redirect:/shared/ratings";
        } catch (Exception error) {
            // Manejar errores inesperados
            String mensajeErrorGenerico = "Error inesperado al crear la valoración. Inténtalo de nuevo.";
            redirectAttributes.addFlashAttribute("error", mensajeErrorGenerico);
            return "redirect:/shared/ratings";
        }
    }
}
