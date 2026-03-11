package es.duit.app.controller;

import es.duit.app.entity.AppUser;
import es.duit.app.entity.JobApplication;
import es.duit.app.entity.ServiceRequest;
import es.duit.app.service.ApplicationDecisionService;
import es.duit.app.service.AuthService;
import es.duit.app.service.JobApplicationService;
import es.duit.app.service.JobLifecycleService;
import es.duit.app.service.RequestService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

// ============================================================================
// CONTROLADOR DE MIS SOLICITUDES - GESTIONA LAS SOLICITUDES DEL USUARIO
// ============================================================================
@Controller
@RequestMapping("/requests")
public class MyRequestsController {

    private final RequestService serviceRequestService;
    private final JobApplicationService jobApplicationService;
    private final ApplicationDecisionService applicationDecisionService;
    private final JobLifecycleService jobLifecycleService;
    private final AuthService authService;

    public MyRequestsController(RequestService serviceRequestService,
            JobApplicationService jobApplicationService,
            ApplicationDecisionService applicationDecisionService,
            JobLifecycleService jobLifecycleService,
            AuthService authService) {
        this.serviceRequestService = serviceRequestService;
        this.jobApplicationService = jobApplicationService;
        this.applicationDecisionService = applicationDecisionService;
        this.jobLifecycleService = jobLifecycleService;
        this.authService = authService;
    }

    // ============================================================================
    // MUESTRA LA PÁGINA CON LAS SOLICITUDES DEL USUARIO
    // ============================================================================
    @GetMapping("/my-requests")
    public String showMyRequests(Authentication auth, Model model) {
        // Obtener usuario logueado
        AppUser usuarioLogueado = authService.getAuthenticatedUser(auth);

        List<ServiceRequest> solicitudesUsuario = serviceRequestService.getMyRequests(usuarioLogueado);

        // Preparar datos para la vista
        model.addAttribute("solicitudesExistentes", solicitudesUsuario);
        model.addAttribute("trabajosDelCliente", jobLifecycleService.getJobsForClient(usuarioLogueado));

        return "jobs/myrequest";
    }

    // ============================================================================
    // PUBLICA UNA SOLICITUD ESPECÍFICA
    // ============================================================================
    @PostMapping("/publish/{id}")
    public String publishRequest(@PathVariable Long id,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        try {
            // Obtener usuario logueado
            AppUser usuarioLogueado = authService.getAuthenticatedUser(auth);

            // Publicar la solicitud usando el servicio
            serviceRequestService.publishRequest(id, usuarioLogueado);

            // Preparar respuesta exitosa
            String mensajeExito = "Solicitud publicada correctamente.";
            redirectAttributes.addFlashAttribute("success", mensajeExito);
            return "redirect:/requests/my-requests";

        } catch (IllegalArgumentException error) {
            // Manejar errores de publicación
            redirectAttributes.addFlashAttribute("error", error.getMessage());
            return "redirect:/requests/my-requests";
        }
    }

    // ============================================================================
    // DESPUBLICA UNA SOLICITUD ESPECÍFICA
    // ============================================================================
    @PostMapping("/unpublish/{id}")
    public String unpublishRequest(@PathVariable Long id,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        try {
            // Obtener usuario logueado
            AppUser usuarioLogueado = authService.getAuthenticatedUser(auth);

            // Despublicar la solicitud usando el servicio
            serviceRequestService.unpublishRequest(id, usuarioLogueado);

            // Preparar respuesta exitosa
            String mensajeExito = "Solicitud despublicada correctamente.";
            redirectAttributes.addFlashAttribute("success", mensajeExito);
            return "redirect:/requests/my-requests";

        } catch (IllegalArgumentException error) {
            // Manejar errores de despublicación
            redirectAttributes.addFlashAttribute("error", error.getMessage());
            return "redirect:/requests/my-requests";
        }
    }

    // ============================================================================
    // CANCELA UNA SOLICITUD ESPECÍFICA
    // ============================================================================
    @PostMapping("/cancel/{id}")
    public String cancelRequest(@PathVariable Long id,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        try {
            // Obtener usuario logueado
            AppUser usuarioLogueado = authService.getAuthenticatedUser(auth);

            // Cancelar la solicitud usando el servicio
            serviceRequestService.cancelRequest(id, usuarioLogueado);

            // Preparar respuesta exitosa
            String mensajeExito = "Solicitud cancelada correctamente.";
            redirectAttributes.addFlashAttribute("success", mensajeExito);
            return "redirect:/requests/my-requests";

        } catch (IllegalArgumentException error) {
            // Manejar errores de cancelación
            redirectAttributes.addFlashAttribute("error", error.getMessage());
            return "redirect:/requests/my-requests";
        }
    }

    // ============================================================================
    // REACTIVA UNA SOLICITUD CANCELADA
    // ============================================================================
    @PostMapping("/reactivate/{id}")
    public String reactivateRequest(@PathVariable Long id,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        try {
            // Obtener usuario logueado
            AppUser usuarioLogueado = authService.getAuthenticatedUser(auth);

            // Reactivar la solicitud usando el servicio
            serviceRequestService.reactivateRequest(id, usuarioLogueado);

            // Preparar respuesta exitosa
            String mensajeExito = "Solicitud reactivada correctamente.";
            redirectAttributes.addFlashAttribute("success", mensajeExito);
            return "redirect:/requests/my-requests";

        } catch (IllegalArgumentException error) {
            // Manejar errores de reactivación
            redirectAttributes.addFlashAttribute("error", error.getMessage());
            return "redirect:/requests/my-requests";
        }
    }

    // ============================================================================
    // ELIMINA UNA SOLICITUD ESPECÍFICA
    // ============================================================================
    @PostMapping("/delete/{id}")
    public String deleteRequest(@PathVariable Long id,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        try {
            // Obtener usuario logueado
            AppUser usuarioLogueado = authService.getAuthenticatedUser(auth);

            // Eliminar la solicitud usando el servicio
            serviceRequestService.deleteRequest(id, usuarioLogueado);

            // Preparar respuesta exitosa
            String mensajeExito = "Solicitud eliminada correctamente.";
            redirectAttributes.addFlashAttribute("success", mensajeExito);
            return "redirect:/requests/my-requests";

        } catch (IllegalArgumentException error) {
            // Manejar errores de eliminación
            redirectAttributes.addFlashAttribute("error", error.getMessage());
            return "redirect:/requests/my-requests";
        }
    }

    // ============================================================================
    // ACEPTAR UNA APLICACIÓN/PROPUESTA ESPECÍFICA 
    // ============================================================================
    @PostMapping("/accept-application")
    public String acceptApplicationWithParams(@RequestParam Long requestId,
            @RequestParam Long applicationId,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        try {
            AppUser usuarioLogueado = authService.getAuthenticatedUser(auth);
            serviceRequestService.getMyRequestById(requestId, usuarioLogueado);
            applicationDecisionService.acceptApplication(applicationId, usuarioLogueado);

            redirectAttributes.addFlashAttribute("success", "Aplicación aceptada correctamente.");
            return "redirect:/requests/my-requests";

        } catch (RuntimeException error) {
            redirectAttributes.addFlashAttribute("error", error.getMessage());
            return "redirect:/requests/my-requests";
        }
    }

    @PostMapping("/reject-application")
    public String rejectApplicationWithParams(@RequestParam Long requestId,
            @RequestParam Long applicationId,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        try {
            AppUser usuarioLogueado = authService.getAuthenticatedUser(auth);
            serviceRequestService.getMyRequestById(requestId, usuarioLogueado);
            applicationDecisionService.rejectApplication(applicationId, usuarioLogueado);

            redirectAttributes.addFlashAttribute("success", "Aplicación rechazada correctamente.");
            return "redirect:/requests/applications/" + requestId;

        } catch (RuntimeException error) {
            redirectAttributes.addFlashAttribute("error", error.getMessage());
            return "redirect:/requests/applications/" + requestId;
        }
    }

    // ============================================================================
    // VER APLICACIONES RECIBIDAS PARA UNA SOLICITUD
    // ============================================================================
    @GetMapping("/applications/{requestId}")
    public String viewApplications(@PathVariable Long requestId, Model model,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        try {
            AppUser usuarioLogueado = authService.getAuthenticatedUser(auth);
            List<JobApplication> aplicaciones = jobApplicationService.getApplicationsForUserRequest(requestId,
                    usuarioLogueado);
            ServiceRequest solicitud = serviceRequestService.getMyRequestById(requestId, usuarioLogueado);

            model.addAttribute("aplicaciones", aplicaciones);
            model.addAttribute("solicitud", solicitud);

            return "jobs/applications";

        } catch (RuntimeException error) {
            redirectAttributes.addFlashAttribute("error", error.getMessage());
            return "redirect:/requests/my-requests";
        }
    }
}
