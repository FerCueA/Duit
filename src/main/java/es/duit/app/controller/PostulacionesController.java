package es.duit.app.controller;

import es.duit.app.entity.AppUser;
import es.duit.app.entity.JobApplication;
import es.duit.app.entity.ServiceRequest;
import es.duit.app.repository.JobApplicationRepository;
import es.duit.app.repository.ServiceRequestRepository;
import es.duit.app.service.AuthService;
import es.duit.app.service.ServiceJobService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

// Controlador para postulaciones y trabajos
@Controller
@RequestMapping("/jobs/postulaciones")
public class PostulacionesController {

    private final ServiceRequestRepository serviceRequestRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final AuthService authService;
    private final ServiceJobService serviceJobService;

    public PostulacionesController(
            ServiceRequestRepository serviceRequestRepository,
            JobApplicationRepository jobApplicationRepository,
            AuthService authService,
            ServiceJobService serviceJobService) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.jobApplicationRepository = jobApplicationRepository;
        this.authService = authService;
        this.serviceJobService = serviceJobService;
    }

    // Ver postulaciones de una solicitud
    @GetMapping("/{id}")
    public String verPostulaciones(@PathVariable Long id, Authentication auth, Model model) {
        try {
            AppUser usuario = authService.obtenerUsuarioAutenticado(auth);
            ServiceRequest solicitud = serviceRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

            // Validar que el usuario es el cliente
            if (!solicitud.getClient().getId().equals(usuario.getId())) {
                throw new IllegalArgumentException("No tienes permiso para ver estas postulaciones");
            }

            List<JobApplication> postulaciones = jobApplicationRepository.findByRequest(solicitud);
            model.addAttribute("postulaciones", postulaciones);
            model.addAttribute("solicitud", solicitud);
            return "jobs/ver-postulaciones";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/jobs/mis-solicitudes";
        }
    }

    // Aceptar postulación
    @PostMapping("/aceptar/{postulacionId}")
    public String aceptarPostulacion(
            @PathVariable Long postulacionId,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        try {
            AppUser usuario = authService.obtenerUsuarioAutenticado(auth);
            
            // Buscar postulación
            JobApplication postulacion = jobApplicationRepository.findById(postulacionId)
                .orElseThrow(() -> new IllegalArgumentException("Postulación no encontrada"));
            
            serviceJobService.aceptarPostulacion(postulacionId, usuario);
            redirectAttributes.addFlashAttribute("success", "Postulación aceptada correctamente.");
            return "redirect:/jobs/postulaciones/" + postulacion.getRequest().getId();
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/jobs/mis-solicitudes";
        }
    }

    // Rechazar postulación
    @PostMapping("/rechazar/{postulacionId}")
    public String rechazarPostulacion(
            @PathVariable Long postulacionId,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        try {
            AppUser usuario = authService.obtenerUsuarioAutenticado(auth);
            
            // Buscar postulación
            JobApplication postulacion = jobApplicationRepository.findById(postulacionId)
                .orElseThrow(() -> new IllegalArgumentException("Postulación no encontrada"));
            
            serviceJobService.rechazarPostulacion(postulacionId, usuario);
            redirectAttributes.addFlashAttribute("success", "Postulación rechazada correctamente.");
            return "redirect:/jobs/postulaciones/" + postulacion.getRequest().getId();
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/jobs/mis-solicitudes";
        }
    }

    // Finalizar trabajo
    @PostMapping("/finalizar/{jobId}")
    public String finalizarTrabajo(
            @PathVariable Long jobId,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        try {
            AppUser usuario = authService.obtenerUsuarioAutenticado(auth);
            serviceJobService.finalizarTrabajo(jobId, usuario);
            redirectAttributes.addFlashAttribute("success", "Trabajo finalizado. Procede a valorar.");
            return "redirect:/shared/valoraciones?jobId=" + jobId;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/professional/postulaciones";
        }
    }

    // Pausar trabajo
    @PostMapping("/pausar/{jobId}")
    public String pausarTrabajo(
            @PathVariable Long jobId,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        try {
            AppUser usuario = authService.obtenerUsuarioAutenticado(auth);
            serviceJobService.pausarTrabajo(jobId, usuario);
            redirectAttributes.addFlashAttribute("success", "Trabajo pausado correctamente.");
            return "redirect:/professional/postulaciones";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/professional/postulaciones";
        }
    }

    // Reanudar trabajo pausado
    @PostMapping("/reanudar/{jobId}")
    public String reanudarTrabajo(
            @PathVariable Long jobId,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        try {
            AppUser usuario = authService.obtenerUsuarioAutenticado(auth);
            serviceJobService.reanudarTrabajo(jobId, usuario);
            redirectAttributes.addFlashAttribute("success", "Trabajo reanudado correctamente.");
            return "redirect:/professional/postulaciones";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/professional/postulaciones";
        }
    }

    // Cancelar trabajo
    @PostMapping("/cancelar/{jobId}")
    public String cancelarTrabajo(
            @PathVariable Long jobId,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        try {
            AppUser usuario = authService.obtenerUsuarioAutenticado(auth);
            serviceJobService.cancelarTrabajo(jobId, usuario);
            redirectAttributes.addFlashAttribute("success", "Trabajo cancelado correctamente.");
            return "redirect:/professional/postulaciones";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/professional/postulaciones";
        }
    }
}
