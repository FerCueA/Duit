package es.duit.app.controller;

import es.duit.app.entity.AppUser;
import es.duit.app.entity.Rating;
import es.duit.app.entity.ServiceJob;
import es.duit.app.dto.RatingDTO;
import es.duit.app.service.HistoryService;
import es.duit.app.service.AuthService;
import es.duit.app.service.RatingService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

// ============================================================================
// CONTROLADOR COMPARTIDO - GESTIONA PÁGINAS ACCESIBLES POR TODOS LOS USUARIOS
// ============================================================================
@Controller
@RequestMapping("/shared")
public class SharedController {

    private final AuthService authService;
    private final HistoryService historyService;
    private final RatingService ratingService;

    public SharedController(AuthService authService,
            HistoryService historyService,
            RatingService ratingService) {
        this.authService = authService;
        this.historyService = historyService;
        this.ratingService = ratingService;
    }

    // ============================================================================
    // PÁGINA DE HISTORIAL DE TRABAJOS
    // ============================================================================
    @GetMapping({ "/history" })
    public String mostrarHistorial(Authentication auth, Model model) {
        AppUser usuarioLogueado = authService.getAuthenticatedUser(auth);
        model.addAttribute("historial", historyService.getHistoryForUser(usuarioLogueado));
        model.addAttribute("currentUserId", usuarioLogueado.getId());
        return "shared/history";
    }

    // ============================================================================
    // PÁGINA DE VALORACIONES - MUESTRA VALORACIONES PENDIENTES Y FINALIZADAS
    // ============================================================================
    @GetMapping({ "/ratings" })
    public String mostrarValoraciones(Authentication auth, Model model, @RequestParam(required = false) Long jobId) {
        // Obtener el usuario actualmente logueado
        AppUser usuarioLogueado = authService.getAuthenticatedUser(auth);

        List<ServiceJob> trabajosPendientes = ratingService.getPendingRatingJobs(usuarioLogueado);
        List<ServiceJob> trabajosFinalizadas = ratingService.getFinishedRatingJobs(usuarioLogueado);

        boolean esProfesional = usuarioLogueado.getProfessionalProfile() != null;
        model.addAttribute("esProfesional", esProfesional);

        if (esProfesional) {
            List<Rating> valoracionesProfesional = ratingService.getProfessionalRatings(usuarioLogueado);
            double notaMedia = ratingService.calculateAverageScore(valoracionesProfesional);

            model.addAttribute("valoracionesProfesional", valoracionesProfesional);
            model.addAttribute("notaMediaProfesional", notaMedia);
            model.addAttribute("totalValoracionesProfesional", valoracionesProfesional.size());
        }

        // Agregar listas al modelo para la vista
        model.addAttribute("trabajosPendientes", trabajosPendientes);
        model.addAttribute("trabajosFinalizadas", trabajosFinalizadas);
        model.addAttribute("ratingForm", new RatingDTO());

        ServiceJob trabajoSeleccionado = ratingService.getJobForRatingsView(jobId, usuarioLogueado);
        if (trabajoSeleccionado != null) {
            model.addAttribute("trabajoSeleccionado", trabajoSeleccionado);
        }

        return "shared/ratings";
    }
}