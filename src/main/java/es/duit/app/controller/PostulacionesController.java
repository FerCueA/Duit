package es.duit.app.controller;

import es.duit.app.entity.AppUser;
import es.duit.app.entity.JobApplication;
import es.duit.app.entity.ServiceRequest;
import es.duit.app.repository.JobApplicationRepository;
import es.duit.app.repository.ServiceRequestRepository;
import es.duit.app.service.AuthService;
import es.duit.app.service.JobService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

// ============================================================================
// CONTROLADOR DE POSTULACIONES - GESTIONA POSTULACIONES Y TRABAJOS
// ============================================================================
@Controller
@RequestMapping({ "/jobs/applications" })
public class PostulacionesController {

    private final ServiceRequestRepository serviceRequestRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final AuthService authService;
    private final JobService jobService;

    public PostulacionesController(ServiceRequestRepository serviceRequestRepository,
            JobApplicationRepository jobApplicationRepository,
            AuthService authService,
            JobService jobService) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.jobApplicationRepository = jobApplicationRepository;
        this.authService = authService;
        this.jobService = jobService;
    }

    // ============================================================================
    // VER POSTULACIONES DE UNA SOLICITUD ESPECÍFICA
    // ============================================================================
    @GetMapping("/{id}")
    public String verPostulacionesSolicitud(@PathVariable Long id, Authentication auth, Model model) {
        try {
            // Obtener usuario logueado
            AppUser usuarioLogueado = authService.getAuthenticatedUser(auth);

            // Buscar y validar la solicitud
            ServiceRequest solicitudEncontrada = buscarYValidarSolicitud(id, usuarioLogueado);

            // Obtener postulaciones de la solicitud
            List<JobApplication> postulacionesEncontradas = jobApplicationRepository.findByRequest(solicitudEncontrada);

            // Preparar datos para la vista
            model.addAttribute("postulaciones", postulacionesEncontradas);
            model.addAttribute("solicitud", solicitudEncontrada);

            return "jobs/myaplication";

        } catch (IllegalArgumentException error) {
            // Manejar error en la visualización de postulaciones
            model.addAttribute("error", error.getMessage());
            return "redirect:/requests/my-requests";
        }
    }

    // ============================================================================
    // ACEPTA UNA POSTULACIÓN ESPECÍFICA
    // ============================================================================
    @PostMapping("/aceptar/{postulacionId}")
    public String aceptarPostulacionEspecifica(@PathVariable Long postulacionId,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        try {
            // Obtener usuario logueado
            AppUser usuarioLogueado = authService.getAuthenticatedUser(auth);

            // Buscar postulación para obtener ID de solicitud
            JobApplication postulacionEncontrada = buscarPostulacionPorId(postulacionId);

            // Procesar aceptación de postulación
            jobService.acceptApplication(postulacionId, usuarioLogueado);

            // Preparar respuesta exitosa
            String mensajeExito = "Postulación aceptada correctamente.";
            redirectAttributes.addFlashAttribute("success", mensajeExito);
            return "redirect:/jobs/applications/" + postulacionEncontrada.getRequest().getId();

        } catch (IllegalArgumentException error) {
            // Manejar error en la aceptación de postulaciones
            redirectAttributes.addFlashAttribute("error", error.getMessage());
            return "redirect:/requests/my-requests";
        }
    }

    // ============================================================================
    // RECHAZA UNA POSTULACIÓN ESPECÍFICA
    // ============================================================================
    @PostMapping("/rechazar/{postulacionId}")
    public String rechazarPostulacionEspecifica(@PathVariable Long postulacionId,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        try {
            // Obtener usuario logueado
            AppUser usuarioLogueado = authService.getAuthenticatedUser(auth);

            // Buscar postulación para obtener ID de solicitud
            JobApplication postulacionEncontrada = buscarPostulacionPorId(postulacionId);

            // Procesar rechazo de postulación
            jobService.rejectApplication(postulacionId, usuarioLogueado);

            // Preparar respuesta exitosa
            String mensajeExito = "Postulación rechazada correctamente.";
            redirectAttributes.addFlashAttribute("success", mensajeExito);
            return "redirect:/jobs/applications/" + postulacionEncontrada.getRequest().getId();

        } catch (IllegalArgumentException error) {
            // Manejar error en el rechazo de postulaciones
            redirectAttributes.addFlashAttribute("error", error.getMessage());
            return "redirect:/requests/my-requests";
        }
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
            jobService.completeJob(jobId, usuarioLogueado);

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
            RedirectAttributes redirectAttributes) {
        try {
            // Obtener usuario logueado
            AppUser usuarioLogueado = authService.getAuthenticatedUser(auth);

            // Procesar pausa del trabajo
            jobService.pauseJob(jobId, usuarioLogueado);

            // Preparar respuesta exitosa
            String mensajeExito = "Trabajo pausado correctamente.";
            redirectAttributes.addFlashAttribute("success", mensajeExito);
            return "redirect:/professional/applications";

        } catch (IllegalArgumentException error) {
            // Manejar error en la pausa de trabajos
            redirectAttributes.addFlashAttribute("error", error.getMessage());
            return "redirect:/professional/applications";
        }
    }

    // ============================================================================
    // REANUDA UN TRABAJO PAUSADO
    // ============================================================================
    @PostMapping("/reanudar/{jobId}")
    public String reanudarTrabajoPausado(@PathVariable Long jobId,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        try {
            // Obtener usuario logueado
            AppUser usuarioLogueado = authService.getAuthenticatedUser(auth);

            // Procesar reanudación del trabajo
            jobService.resumeJob(jobId, usuarioLogueado);

            // Preparar respuesta exitosa
            String mensajeExito = "Trabajo reanudado correctamente.";
            redirectAttributes.addFlashAttribute("success", mensajeExito);
            return "redirect:/professional/applications";

        } catch (IllegalArgumentException error) {
            // Manejar error en la reanudación de trabajos
            redirectAttributes.addFlashAttribute("error", error.getMessage());
            return "redirect:/professional/applications";
        }
    }

    // ============================================================================
    // CANCELA UN TRABAJO
    // ============================================================================
    @PostMapping("/cancelar/{jobId}")
    public String cancelarTrabajo(@PathVariable Long jobId,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        try {
            // Obtener usuario logueado
            AppUser usuarioLogueado = authService.getAuthenticatedUser(auth);

            // Procesar cancelación del trabajo
            jobService.cancelJob(jobId, usuarioLogueado);

            // Preparar respuesta exitosa
            String mensajeExito = "Trabajo cancelado correctamente.";
            redirectAttributes.addFlashAttribute("success", mensajeExito);
            return "redirect:/professional/applications";

        } catch (IllegalArgumentException error) {
            // Manejar error en la cancelación de trabajos
            redirectAttributes.addFlashAttribute("error", error.getMessage());
            return "redirect:/professional/applications";
        }
    }

    // ============================================================================
    // BUSCA Y VALIDA UNA SOLICITUD ESPECÍFICA
    // ============================================================================
    private ServiceRequest buscarYValidarSolicitud(Long id, AppUser usuario) {
        // Buscar la solicitud en la base de datos
        ServiceRequest solicitudEncontrada = serviceRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        // Validar que el usuario es el cliente de la solicitud
        boolean esClienteDeLaSolicitud = solicitudEncontrada.getClient().getId().equals(usuario.getId());
        if (!esClienteDeLaSolicitud) {
            throw new IllegalArgumentException("No tienes permiso para ver estas postulaciones");
        }

        return solicitudEncontrada;
    }

    // ============================================================================
    // BUSCA UNA POSTULACIÓN POR SU ID
    // ============================================================================
    private JobApplication buscarPostulacionPorId(Long postulacionId) {
        return jobApplicationRepository.findById(postulacionId)
                .orElseThrow(() -> new IllegalArgumentException("Postulación no encontrada"));
    }
}
