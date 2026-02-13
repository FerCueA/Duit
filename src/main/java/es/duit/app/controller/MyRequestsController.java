package es.duit.app.controller;

import es.duit.app.entity.AppUser;
import es.duit.app.entity.JobApplication;
import es.duit.app.entity.ServiceJob;
import es.duit.app.entity.ServiceRequest;
import es.duit.app.repository.ServiceJobRepository;
import es.duit.app.service.AuthService;
import es.duit.app.service.RequestService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

// ============================================================================
// CONTROLADOR DE MIS SOLICITUDES - GESTIONA LAS SOLICITUDES DEL USUARIO
// ============================================================================
@Controller
@RequestMapping("/requests")
public class MyRequestsController {

    private final RequestService serviceRequestService;
    private final ServiceJobRepository serviceJobRepository;
    private final AuthService authService;

    public MyRequestsController(RequestService serviceRequestService,
            ServiceJobRepository serviceJobRepository,
            AuthService authService) {
        this.serviceRequestService = serviceRequestService;
        this.serviceJobRepository = serviceJobRepository;
        this.authService = authService;
    }

    // ============================================================================
    // MUESTRA LA PÁGINA CON LAS SOLICITUDES DEL USUARIO
    // ============================================================================
    @GetMapping("/my-requests")
    public String showMyRequests(Authentication auth, Model model) {
        // Obtener usuario logueado
        AppUser usuarioLogueado = authService.getAuthenticatedUser(auth);

        List<ServiceRequest> solicitudesUsuario = serviceRequestService.getMyRequests();
        List<ServiceJob> trabajosUsuario = serviceJobRepository.findByCliente(usuarioLogueado);

        // Preparar datos para la vista
        model.addAttribute("solicitudesExistentes", solicitudesUsuario);
        model.addAttribute("trabajosDelCliente", trabajosUsuario);

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
    // MARCA UN TRABAJO COMO COMPLETADO
    // ============================================================================
    @PostMapping("/complete/{jobId}")
    public String completeJob(@PathVariable Long jobId, Authentication auth) {
        // Obtener usuario logueado
        AppUser usuarioLogueado = authService.getAuthenticatedUser(auth);

        // Buscar y validar el trabajo
        ServiceJob trabajoEncontrado = buscarYValidarTrabajo(jobId, usuarioLogueado);
        if (trabajoEncontrado == null) {
            return "redirect:/requests/my-requests";
        }

        // Completar el trabajo
        completarTrabajoEspecifico(trabajoEncontrado);

        // Redirigir a valoraciones
        return "redirect:/shared/ratings?jobId=" + jobId;
    }

    // ============================================================================
    // PAUSA UN TRABAJO EN PROGRESO
    // ============================================================================
    @PostMapping("/pause/{jobId}")
    public String pauseJob(@PathVariable Long jobId, Authentication auth) {
        // Obtener usuario logueado
        AppUser usuarioLogueado = authService.getAuthenticatedUser(auth);

        // Buscar y validar el trabajo
        ServiceJob trabajoEncontrado = buscarYValidarTrabajo(jobId, usuarioLogueado);
        if (trabajoEncontrado == null) {
            return "redirect:/requests/my-requests";
        }

        // Pausar el trabajo
        pausarTrabajoEspecifico(trabajoEncontrado);

        return "redirect:/requests/my-requests";
    }

    // ============================================================================
    // CANCELA UN TRABAJO ESPECÍFICO
    // ============================================================================
    @PostMapping("/cancel-job/{jobId}")
    public String cancelJob(@PathVariable Long jobId, Authentication auth) {
        // Obtener usuario logueado
        AppUser usuarioLogueado = authService.getAuthenticatedUser(auth);

        // Buscar y validar el trabajo
        ServiceJob trabajoEncontrado = buscarYValidarTrabajo(jobId, usuarioLogueado);
        if (trabajoEncontrado == null) {
            return "redirect:/requests/my-requests";
        }

        // Cancelar el trabajo
        cancelarTrabajoEspecifico(trabajoEncontrado);

        return "redirect:/requests/my-requests";
    }

    // ============================================================================
    // BUSCA Y VALIDA UN TRABAJO ESPECÍFICO
    // ============================================================================
    private ServiceJob buscarYValidarTrabajo(Long jobId, AppUser usuario) {
        // Buscar el trabajo en la base de datos
        if (jobId == null) {
            return null;
        }
        ServiceJob trabajoEncontrado = serviceJobRepository.findById(jobId).orElse(null);

        // Verificar que existe el trabajo
        if (trabajoEncontrado == null) {
            return null;
        }

        // Validar que el usuario es el cliente del trabajo
        AppUser clienteDelTrabajo = trabajoEncontrado.getClient();
        boolean esClienteDelTrabajo = clienteDelTrabajo.getId().equals(usuario.getId());

        if (!esClienteDelTrabajo) {
            return null;
        }

        return trabajoEncontrado;
    }

    // ============================================================================
    // COMPLETA UN TRABAJO ESPECÍFICO
    // ============================================================================
    private void completarTrabajoEspecifico(ServiceJob trabajo) {
        trabajo.setStatus(ServiceJob.Status.COMPLETED);
        trabajo.setEndDate(LocalDateTime.now());
        serviceJobRepository.save(trabajo);
    }

    // ============================================================================
    // PAUSA UN TRABAJO ESPECÍFICO
    // ============================================================================
    private void pausarTrabajoEspecifico(ServiceJob trabajo) {
        trabajo.setStatus(ServiceJob.Status.PAUSED);
        serviceJobRepository.save(trabajo);
    }

    // ============================================================================
    // CANCELA UN TRABAJO ESPECÍFICO
    // ============================================================================
    private void cancelarTrabajoEspecifico(ServiceJob trabajo) {
        trabajo.setStatus(ServiceJob.Status.CANCELLED);
        serviceJobRepository.save(trabajo);
    }

    // ============================================================================
    // COMPLETAR UNA SOLICITUD DEL USUARIO AUTENTICADO
    // ============================================================================
    @PostMapping("/complete-request/{id}")
    public String completeRequest(@PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            serviceRequestService.completeRequest(id);

            redirectAttributes.addFlashAttribute("success", "Solicitud completada correctamente.");
            return "redirect:/requests/my-requests";

        } catch (RuntimeException error) {
            redirectAttributes.addFlashAttribute("error", error.getMessage());
            return "redirect:/requests/my-requests";
        }
    }

    // ============================================================================
    // ACEPTAR UNA APLICACIÓN/PROPUESTA ESPECÍFICA (VERSIÓN CON PARÁMETROS POST)
    // ============================================================================
    @PostMapping("/accept-application")
    public String acceptApplicationWithParams(@RequestParam Long requestId,
            @RequestParam Long applicationId,
            RedirectAttributes redirectAttributes) {
        try {
            serviceRequestService.acceptRequest(requestId, applicationId);

            redirectAttributes.addFlashAttribute("success", "Aplicación aceptada correctamente.");
            return "redirect:/requests/my-requests";

        } catch (RuntimeException error) {
            redirectAttributes.addFlashAttribute("error", error.getMessage());
            return "redirect:/requests/my-requests";
        }
    }

    // ============================================================================
    // VER APLICACIONES RECIBIDAS PARA UNA SOLICITUD
    // ============================================================================
    @GetMapping("/applications/{requestId}")
    public String viewApplications(@PathVariable Long requestId, Model model,
            RedirectAttributes redirectAttributes) {
        try {
            List<JobApplication> aplicaciones = serviceRequestService.getApplicationsForMyRequest(requestId);
            ServiceRequest solicitud = serviceRequestService.getMyRequestById(requestId);

            model.addAttribute("aplicaciones", aplicaciones);
            model.addAttribute("solicitud", solicitud);

            return "jobs/applications";

        } catch (RuntimeException error) {
            redirectAttributes.addFlashAttribute("error", error.getMessage());
            return "redirect:/requests/my-requests";
        }
    }
}
