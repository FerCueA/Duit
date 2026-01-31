package es.duit.app.controller;

import es.duit.app.dto.CrearSolicitudDTO;
import es.duit.app.entity.AppUser;
import es.duit.app.entity.ServiceJob;
import es.duit.app.entity.ServiceRequest;
import es.duit.app.repository.ServiceJobRepository;
import es.duit.app.service.AuthService;
import es.duit.app.service.ServiceRequestService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
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

    // Inicializar formulario
    @ModelAttribute("form")
    public CrearSolicitudDTO initForm() {
        CrearSolicitudDTO form = new CrearSolicitudDTO();
        form.setAddressOption("habitual");
        form.setCountry("España");
        return form;
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

            CrearSolicitudDTO form = new CrearSolicitudDTO();

            if (edit != null) {
                ServiceRequest solicitud = serviceRequestService.obtenerSolicitudDelUsuario(edit, usuario);

                // Rellenar formulario con datos existentes
                form.setEditId(solicitud.getId());
                form.setTitle(solicitud.getTitle());
                form.setDescription(solicitud.getDescription());
                form.setCategoryId(solicitud.getCategory().getId());

                if (solicitud.getDeadline() != null) {
                    form.setDeadline(solicitud.getDeadline().toLocalDate());
                }

                // Determinar tipo de dirección
                if (solicitud.hasSpecificServiceAddress()) {
                    form.setAddressOption("new");
                    form.setAddress(solicitud.getServiceAddress().getAddress());
                    form.setCity(solicitud.getServiceAddress().getCity());
                    form.setPostalCode(solicitud.getServiceAddress().getPostalCode());
                    form.setProvince(solicitud.getServiceAddress().getProvince());
                    form.setCountry(solicitud.getServiceAddress().getCountry());
                } else {
                    form.setAddressOption("habitual");
                }

                model.addAttribute("modoEdicion", true);
            } else {
                // Valores por defecto para nueva solicitud
                form.setAddressOption("habitual");
                form.setCountry("España");
            }

            model.addAttribute("form", form);
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
    public String crearSolicitud(@Valid @ModelAttribute("form") CrearSolicitudDTO form,
            BindingResult bindingResult,
            Authentication auth,
            Model model,
            RedirectAttributes redirectAttributes) {

        AppUser usuario = authService.obtenerUsuarioAutenticado(auth);

        try {
            // Si hay errores de validación básica, volver al formulario
            if (bindingResult.hasErrors()) {
                cargarDatosFormulario(model, usuario, form.isEditing());
                return "jobs/crear";
            }

            // Crear o actualizar la solicitud (las validaciones adicionales están en el
            // servicio)
            ServiceRequest solicitud = serviceRequestService.crearOActualizarSolicitud(form, usuario);

            String mensaje = form.isEditing()
                    ? "Solicitud actualizada correctamente"
                    : "Solicitud creada correctamente";

            redirectAttributes.addFlashAttribute("success", mensaje);
            return "redirect:/requests/mis-solicitudes";

        } catch (IllegalArgumentException e) {

            bindingResult.reject("error.global", e.getMessage());
            cargarDatosFormulario(model, usuario, form.isEditing());
            return "jobs/crear";

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute("error", "Error inesperado. Inténtalo de nuevo.");
            return "redirect:/requests/crear";
        }
    }

    // Cargar datos comunes para el formulario
    private void cargarDatosFormulario(Model model, AppUser usuario, boolean esEdicion) {
        model.addAttribute("habitualAddress", usuario.getAddress());
        model.addAttribute("categorias", serviceRequestService.obtenerCategoriasActivas());
        model.addAttribute("modoEdicion", esEdicion);
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
