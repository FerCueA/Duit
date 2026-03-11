package es.duit.app.controller;

import es.duit.app.entity.AppUser;
import es.duit.app.service.AuthService;
import es.duit.app.service.JobLifecycleService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// ============================================================================
// CONTROLADOR DE POSTULACIONES - GESTIONA POSTULACIONES Y TRABAJOS
// ============================================================================
@Controller
@RequestMapping({ "/jobs/applications" })
public class PostulacionesController {

    private final AuthService authService;
    private final JobLifecycleService jobLifecycleService;

    public PostulacionesController(AuthService authService,
            JobLifecycleService jobLifecycleService) {
        this.authService = authService;
        this.jobLifecycleService = jobLifecycleService;
    }

    // ============================================================================
    // FINALIZA UN TRABAJO COMPLETADO
    // ============================================================================
    @PostMapping("/finalizar/{jobId}")
    public String finalizarTrabajoCompletado(@PathVariable Long jobId,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        try {
            // Obtener usuario logueado
            AppUser usuarioLogueado = authService.getAuthenticatedUser(auth);

            // Procesar finalización del trabajo
            jobLifecycleService.completeJob(jobId, usuarioLogueado);

            // Preparar respuesta exitosa - redireccionar a valoraciones
            String mensajeExito = "Trabajo finalizado. Procede a valorar.";
            redirectAttributes.addFlashAttribute("success", mensajeExito);
            return "redirect:/shared/ratings?jobId=" + jobId;

        } catch (IllegalArgumentException error) {
            // Manejar error en la finalización de trabajos
            redirectAttributes.addFlashAttribute("error", error.getMessage());
            return "redirect:/professional/applications";
        }
    }

    // ============================================================================
    // PAUSA UN TRABAJO EN PROGRESO
    // ============================================================================
    @PostMapping("/pausar/{jobId}")
    public String pausarTrabajoEnProgreso(@PathVariable Long jobId,
            Authentication auth,
            RedirectAttributes redirectAttributes,
            @RequestParam(required = false) String redirect) {
        try {
            // Obtener usuario logueado
            AppUser usuarioLogueado = authService.getAuthenticatedUser(auth);

            // Procesar pausa del trabajo
            jobLifecycleService.pauseJob(jobId, usuarioLogueado);

            // Preparar respuesta exitosa
            String mensajeExito = "Trabajo pausado correctamente.";
            redirectAttributes.addFlashAttribute("success", mensajeExito);
            return buildRedirect(redirect, "/professional/applications");

        } catch (IllegalArgumentException error) {
            // Manejar error en la pausa de trabajos
            redirectAttributes.addFlashAttribute("error", error.getMessage());
            return buildRedirect(redirect, "/professional/applications");
        }
    }

    // ============================================================================
    // REANUDA UN TRABAJO PAUSADO
    // ============================================================================
    @PostMapping("/reanudar/{jobId}")
    public String reanudarTrabajoPausado(@PathVariable Long jobId,
            Authentication auth,
            RedirectAttributes redirectAttributes,
            @RequestParam(required = false) String redirect) {
        try {
            // Obtener usuario logueado
            AppUser usuarioLogueado = authService.getAuthenticatedUser(auth);

            // Procesar reanudación del trabajo
            jobLifecycleService.resumeJob(jobId, usuarioLogueado);

            // Preparar respuesta exitosa
            String mensajeExito = "Trabajo reanudado correctamente.";
            redirectAttributes.addFlashAttribute("success", mensajeExito);
            return buildRedirect(redirect, "/professional/applications");

        } catch (IllegalArgumentException error) {
            // Manejar error en la reanudación de trabajos
            redirectAttributes.addFlashAttribute("error", error.getMessage());
            return buildRedirect(redirect, "/professional/applications");
        }
    }

    // =========================================================================
    // INICIA UN TRABAJO CREADO
    // =========================================================================
    @PostMapping("/iniciar/{jobId}")
    public String iniciarTrabajoCreado(@PathVariable Long jobId,
            Authentication auth,
            RedirectAttributes redirectAttributes,
            @RequestParam(required = false) String redirect) {
        try {
            AppUser usuarioLogueado = authService.getAuthenticatedUser(auth);
            jobLifecycleService.startJob(jobId, usuarioLogueado);

            String mensajeExito = "Trabajo iniciado correctamente.";
            redirectAttributes.addFlashAttribute("success", mensajeExito);
            return buildRedirect(redirect, "/professional/applications");

        } catch (IllegalArgumentException error) {
            redirectAttributes.addFlashAttribute("error", error.getMessage());
            return buildRedirect(redirect, "/professional/applications");
        }
    }

    // ============================================================================
    // CANCELA UN TRABAJO
    // ============================================================================
    @PostMapping("/cancelar/{jobId}")
    public String cancelarTrabajo(@PathVariable Long jobId,
            Authentication auth,
            RedirectAttributes redirectAttributes,
            @RequestParam(required = false) String redirect) {
        try {
            // Obtener usuario logueado
            AppUser usuarioLogueado = authService.getAuthenticatedUser(auth);

            // Procesar cancelación del trabajo
            jobLifecycleService.cancelJob(jobId, usuarioLogueado);

            // Preparar respuesta exitosa
            String mensajeExito = "Trabajo cancelado correctamente.";
            redirectAttributes.addFlashAttribute("success", mensajeExito);
            return buildRedirect(redirect, "/professional/applications");

        } catch (IllegalArgumentException error) {
            // Manejar error en la cancelación de trabajos
            redirectAttributes.addFlashAttribute("error", error.getMessage());
            return buildRedirect(redirect, "/professional/applications");
        }
    }

    private String buildRedirect(String redirect, String fallback) {
        if (redirect == null || redirect.trim().isEmpty()) {
            return "redirect:" + fallback;
        }
        return "redirect:" + redirect;
    }

}
