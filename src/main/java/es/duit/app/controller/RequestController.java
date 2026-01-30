package es.duit.app.controller;

import es.duit.app.entity.AppUser;
import es.duit.app.entity.ServiceJob;
import es.duit.app.entity.ServiceRequest;
import es.duit.app.repository.ServiceJobRepository;
import es.duit.app.service.AuthService;
import es.duit.app.service.ServiceRequestService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/requests")
public class RequestController {

    private final ServiceRequestService serviceRequestService;
    private final ServiceJobRepository serviceJobRepository;
    private final AuthService authService;

    public RequestController(
            ServiceRequestService serviceRequestService,
            ServiceJobRepository serviceJobRepository,
            AuthService authService) {
        this.serviceRequestService = serviceRequestService;
        this.serviceJobRepository = serviceJobRepository;
        this.authService = authService;
    }

    // Mostrar formulario crear solicitud
    @GetMapping("/crear")
    public String mostrarFormularioCrear(Authentication auth, Model model,
            @RequestParam(required = false) Long edit,
            RedirectAttributes redirectAttributes) {
        try {
            AppUser usuario = authService.obtenerUsuarioAutenticado(auth);
            model.addAttribute("habitualAddress", usuario.getAddress());
            model.addAttribute("categorias", serviceRequestService.obtenerCategoriasActivas());

            // Cargar solicitud si viene edit
            if (edit != null) {
                ServiceRequest solicitud = serviceRequestService.obtenerSolicitudDelUsuario(edit, usuario);
                model.addAttribute("solicitud", solicitud);
                model.addAttribute("modoEdicion", true);
            }

            return "jobs/crear";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/requests/mis-solicitudes";
        }
    }

    // Ver mis solicitudes
    @GetMapping("/mis-solicitudes")
    public String mostrarMisSolicitudes(Authentication auth, Model model) {
        AppUser usuario = authService.obtenerUsuarioAutenticado(auth);
        List<ServiceRequest> solicitudes = serviceRequestService.obtenerSolicitudesDelUsuario(usuario);
        List<ServiceJob> trabajos = serviceJobRepository.findByCliente(usuario);

        model.addAttribute("solicitudesExistentes", solicitudes);
        model.addAttribute("trabajosDelCliente", trabajos);
        return "jobs/mis-solicitudes";
    }

    // Crear o actualizar solicitud
    @PostMapping("/crear")
    public String crearSolicitud(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam Long categoryId,
            @RequestParam(required = false) String deadline,
            @RequestParam String addressOption,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String postalCode,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) Long editId,
            Authentication auth,
            RedirectAttributes redirectAttributes) {

        try {
            AppUser usuario = authService.obtenerUsuarioAutenticado(auth);
            ServiceRequest solicitud = serviceRequestService.crearOActualizarSolicitud(
                    title, description, categoryId, deadline, addressOption,
                    address, city, postalCode, province, country, editId, usuario);

            String mensaje = (editId != null)
                    ? "Solicitud actualizada correctamente."
                    : "Solicitud creada correctamente.";
            redirectAttributes.addFlashAttribute("success", mensaje);
            return "redirect:/requests/mis-solicitudes";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/requests/crear";
        }
    }

    // Publicar solicitud
    @PostMapping("/publicar/{id}")
    public String publicarSolicitud(@PathVariable Long id, Authentication auth,
            RedirectAttributes redirectAttributes) {
        try {
            AppUser usuario = authService.obtenerUsuarioAutenticado(auth);
            serviceRequestService.publicarSolicitud(id, usuario);
            redirectAttributes.addFlashAttribute("success", "Solicitud publicada correctamente.");
            return "redirect:/requests/mis-solicitudes";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/requests/mis-solicitudes";
        }
    }

    // Despublicar solicitud
    @PostMapping("/despublicar/{id}")
    public String despublicarSolicitud(@PathVariable Long id, Authentication auth,
            RedirectAttributes redirectAttributes) {
        try {
            AppUser usuario = authService.obtenerUsuarioAutenticado(auth);
            serviceRequestService.despublicarSolicitud(id, usuario);
            redirectAttributes.addFlashAttribute("success", "Solicitud despublicada correctamente.");
            return "redirect:/requests/mis-solicitudes";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/requests/mis-solicitudes";
        }
    }

    // Cancelar solicitud
    @PostMapping("/cancelar/{id}")
    public String cancelarSolicitud(@PathVariable Long id, Authentication auth,
            RedirectAttributes redirectAttributes) {
        try {
            AppUser usuario = authService.obtenerUsuarioAutenticado(auth);
            serviceRequestService.cancelarSolicitud(id, usuario);
            redirectAttributes.addFlashAttribute("success", "Solicitud cancelada correctamente.");
            return "redirect:/requests/mis-solicitudes";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/requests/mis-solicitudes";
        }
    }

    // Reactivar solicitud
    @PostMapping("/reactivar/{id}")
    public String reactivarSolicitud(@PathVariable Long id, Authentication auth,
            RedirectAttributes redirectAttributes) {
        try {
            AppUser usuario = authService.obtenerUsuarioAutenticado(auth);
            serviceRequestService.reactivarSolicitud(id, usuario);
            redirectAttributes.addFlashAttribute("success", "Solicitud reactivada correctamente.");
            return "redirect:/requests/mis-solicitudes";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/requests/mis-solicitudes";
        }
    }

    // Eliminar solicitud
    @PostMapping("/eliminar/{id}")
    public String eliminarSolicitud(@PathVariable Long id, Authentication auth,
            RedirectAttributes redirectAttributes) {
        try {
            AppUser usuario = authService.obtenerUsuarioAutenticado(auth);
            serviceRequestService.eliminarSolicitud(id, usuario);
            redirectAttributes.addFlashAttribute("success", "Solicitud eliminada correctamente.");
            return "redirect:/requests/mis-solicitudes";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/requests/mis-solicitudes";
        }
    }

    // Finalizar un trabajo (para cliente)
    @PostMapping("/finalizar/{jobId}")
    public String finalizarTrabajo(@PathVariable Long jobId, Authentication auth) {
        AppUser usuarioLogueado = authService.obtenerUsuarioAutenticado(auth);
        ServiceJob trabajo = serviceJobRepository.findById(jobId).orElse(null);

        if (trabajo == null) {
            return "redirect:/requests/mis-solicitudes";
        }

        // Verificar que sea el cliente de la solicitud
        AppUser cliente = trabajo.getClient();
        if (!cliente.getId().equals(usuarioLogueado.getId())) {
            return "redirect:/requests/mis-solicitudes";
        }

        // Cambiar estado a COMPLETED
        trabajo.setStatus(ServiceJob.Status.COMPLETED);
        trabajo.setEndDate(java.time.LocalDateTime.now());
        serviceJobRepository.save(trabajo);

        return "redirect:/shared/valoraciones?jobId=" + jobId;
    }

    // Pausar un trabajo (para cliente)
    @PostMapping("/pausar/{jobId}")
    public String pausarTrabajo(@PathVariable Long jobId, Authentication auth) {
        AppUser usuarioLogueado = authService.obtenerUsuarioAutenticado(auth);
        ServiceJob trabajo = serviceJobRepository.findById(jobId).orElse(null);

        if (trabajo == null) {
            return "redirect:/requests/mis-solicitudes";
        }

        // Verificar que sea el cliente de la solicitud
        AppUser cliente = trabajo.getClient();
        if (!cliente.getId().equals(usuarioLogueado.getId())) {
            return "redirect:/requests/mis-solicitudes";
        }

        // Cambiar estado a PAUSED
        trabajo.setStatus(ServiceJob.Status.PAUSED);
        serviceJobRepository.save(trabajo);

        return "redirect:/requests/mis-solicitudes";
    }

    // Cancelar un trabajo (para cliente)
    @PostMapping("/cancelar/{jobId}")
    public String cancelarTrabajo(@PathVariable Long jobId, Authentication auth) {
        AppUser usuarioLogueado = authService.obtenerUsuarioAutenticado(auth);
        ServiceJob trabajo = serviceJobRepository.findById(jobId).orElse(null);

        if (trabajo == null) {
            return "redirect:/requests/mis-solicitudes";
        }

        // Verificar que sea el cliente de la solicitud
        AppUser cliente = trabajo.getClient();
        if (!cliente.getId().equals(usuarioLogueado.getId())) {
            return "redirect:/requests/mis-solicitudes";
        }

        // Cambiar estado a CANCELLED
        trabajo.setStatus(ServiceJob.Status.CANCELLED);
        serviceJobRepository.save(trabajo);

        return "redirect:/requests/mis-solicitudes";
    }

}
