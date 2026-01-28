
package es.duit.app.controller;

import es.duit.app.entity.Address;
import es.duit.app.entity.AppUser;
import es.duit.app.entity.Category;
import es.duit.app.entity.ServiceRequest;
import es.duit.app.repository.AppUserRepository;
import es.duit.app.repository.CategoryRepository;
import es.duit.app.repository.JobApplicationRepository;
import es.duit.app.repository.ServiceRequestRepository;
import es.duit.app.service.AuthService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/requests")
public class RequestController {
    private final ServiceRequestRepository serviceRequestRepository;
    private final CategoryRepository categoryRepository;
    private final AuthService authService;

    public RequestController(
            ServiceRequestRepository serviceRequestRepository,
            CategoryRepository categoryRepository,
            AppUserRepository appUserRepository,
            JobApplicationRepository jobApplicationRepository,
            AuthService authService) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.categoryRepository = categoryRepository;
        this.authService = authService;
    }

    // Mostrar formulario
    @GetMapping("/crear")
    public String mostrarFormularioCrear(Authentication auth, Model model,
            @RequestParam(required = false) Long edit,
            RedirectAttributes redirectAttributes) {
        AppUser usuarioLogueado = authService.obtenerUsuarioAutenticado(auth);
        model.addAttribute("habitualAddress", usuarioLogueado.getAddress());

        // Si viene con parámetro edit, cargar datos de la solicitud
        if (edit != null) {
            List<ServiceRequest> solicitudes = serviceRequestRepository.findByIdAndClient(edit, usuarioLogueado);
            if (solicitudes.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Solicitud no encontrada o sin permisos");
                return "redirect:/requests/mis-solicitudes";
            }
            model.addAttribute("solicitud", solicitudes.get(0));
            model.addAttribute("modoEdicion", true);
        }

        // Añadir categorías activas al modelo
        List<Category> categorias = categoryRepository.findByActiveTrue();
        model.addAttribute("categorias", categorias);
        return "jobs/crear";
    }

    // Mostrar las solicitudes
    @GetMapping("/mis-solicitudes")
    public String mostrarMisSolicitudes(Authentication auth, Model model) {
        AppUser usuarioLogueado = authService.obtenerUsuarioAutenticado(auth);
        List<ServiceRequest> todasLasSolicitudes = serviceRequestRepository.findByClient(usuarioLogueado);
        model.addAttribute("solicitudesExistentes", todasLasSolicitudes);
        return "jobs/mis-solicitudes";
    }

    // Crear nueva solicitud o editar existente
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

        AppUser usuario = authService.obtenerUsuarioAutenticado(auth);
        ServiceRequest solicitud;

        // Si editId está presente, buscar la solicitud existente
        if (editId != null) {
            List<ServiceRequest> solicitudes = serviceRequestRepository.findByIdAndClient(editId, usuario);
            if (solicitudes.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Solicitud no encontrada o sin permisos");
                return "redirect:/requests/mis-solicitudes";
            }
            solicitud = solicitudes.get(0);
        } else {
            solicitud = new ServiceRequest();
            solicitud.setClient(usuario);
            solicitud.setStatus(ServiceRequest.Status.DRAFT);
            solicitud.setRequestedAt(java.time.LocalDateTime.now());
        }

        solicitud.setTitle(title);
        solicitud.setDescription(description);
        solicitud.setDeadline(parseFechaHora(deadline));

        // Asignar categoría
        Category categoria = categoryRepository.findById(categoryId).orElse(null);
        solicitud.setCategory(categoria);

        // Dirección
        if ("habitual".equals(addressOption)) {
            solicitud.setServiceAddress(usuario.getAddress());
        } else if ("new".equals(addressOption)) {
            solicitud.setServiceAddress(crearDireccion(address, city, postalCode, province, country));
        }

        serviceRequestRepository.save(solicitud);
        String mensaje = (editId != null) ? "Solicitud actualizada correctamente." : "Solicitud creada correctamente.";
        redirectAttributes.addFlashAttribute("success", mensaje);
        return "redirect:/requests/mis-solicitudes";
    }

    // Mostrar formulario de edición
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Authentication auth, Model model,
            RedirectAttributes redirectAttributes) {
        AppUser usuarioLogueado = authService.obtenerUsuarioAutenticado(auth);
        List<ServiceRequest> solicitudes = serviceRequestRepository.findByIdAndClient(id, usuarioLogueado);
        if (solicitudes.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Solicitud no encontrada o sin permisos");
            return "redirect:/requests/mis-solicitudes";
        }

        ServiceRequest solicitud = solicitudes.get(0);
        if (solicitud.getStatus() != ServiceRequest.Status.DRAFT) {
            redirectAttributes.addFlashAttribute("error", "Solo se pueden editar solicitudes en borrador");
            return "redirect:/requests/mis-solicitudes";
        }

        model.addAttribute("solicitud", solicitud);
        List<Category> categorias = categoryRepository.findByActiveTrue();
        model.addAttribute("categorias", categorias);
        return "jobs/editar";
    }

    // Cancelar una solicitud publicada
    @PostMapping("/cancelar/{id}")
    public String cancelarSolicitud(@PathVariable Long id, Authentication auth, RedirectAttributes redirectAttributes) {
        AppUser usuarioLogueado = authService.obtenerUsuarioAutenticado(auth);
        List<ServiceRequest> solicitudes = serviceRequestRepository.findByIdAndClient(id, usuarioLogueado);
        if (solicitudes.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Solicitud no encontrada o sin permisos");
            return "redirect:/requests/mis-solicitudes";
        }

        ServiceRequest solicitud = solicitudes.get(0);
        if (solicitud.getStatus() != ServiceRequest.Status.PUBLISHED) {
            redirectAttributes.addFlashAttribute("error", "Solo se pueden cancelar solicitudes publicadas");
            return "redirect:/requests/mis-solicitudes";
        }

        solicitud.setStatus(ServiceRequest.Status.CANCELLED);
        serviceRequestRepository.save(solicitud);
        redirectAttributes.addFlashAttribute("success", "Solicitud cancelada correctamente.");
        return "redirect:/requests/mis-solicitudes";
    }

    // Publicar o Despublicar
    @PostMapping("/publicar/{id}")
    public String publicarODespublicarSolicitud(@PathVariable Long id, Authentication auth,
            RedirectAttributes redirectAttributes) {
        AppUser usuarioLogueado = authService.obtenerUsuarioAutenticado(auth);
        List<ServiceRequest> solicitudes = serviceRequestRepository.findByIdAndClient(id, usuarioLogueado);
        if (solicitudes.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Solicitud no encontrada o sin permisos");
            return "redirect:/requests/mis-solicitudes";
        }
        ServiceRequest solicitud = solicitudes.get(0);
        if (solicitud.getStatus() == ServiceRequest.Status.DRAFT) {
            solicitud.setStatus(ServiceRequest.Status.PUBLISHED);
            serviceRequestRepository.save(solicitud);
            redirectAttributes.addFlashAttribute("success", "Solicitud publicada correctamente.");
        } else if (solicitud.getStatus() == ServiceRequest.Status.PUBLISHED) {
            solicitud.setStatus(ServiceRequest.Status.DRAFT);
            serviceRequestRepository.save(solicitud);
            redirectAttributes.addFlashAttribute("success", "Solicitud despublicada correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("error",
                    "Solo se pueden publicar/despublicar solicitudes en estado borrador o publicadas.");
        }
        return "redirect:/requests/mis-solicitudes";
    }

    // Eliminar una solicitud (
    @PostMapping("/eliminar/{id}")
    public String eliminarSolicitud(@PathVariable Long id, Authentication auth, RedirectAttributes redirectAttributes) {
        AppUser usuarioLogueado = authService.obtenerUsuarioAutenticado(auth);
        List<ServiceRequest> solicitudes = serviceRequestRepository.findByIdAndClient(id, usuarioLogueado);
        if (solicitudes.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Solicitud no encontrada o sin permisos");
            return "redirect:/requests/mis-solicitudes";
        }
        serviceRequestRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Solicitud eliminada correctamente.");
        return "redirect:/requests/mis-solicitudes";
    }

    // Reactivar una solicitud cancelada
    @PostMapping("/reactivar/{id}")
    public String reactivarSolicitud(@PathVariable Long id, Authentication auth,
            RedirectAttributes redirectAttributes) {
        AppUser usuarioLogueado = authService.obtenerUsuarioAutenticado(auth);
        List<ServiceRequest> solicitudes = serviceRequestRepository.findByIdAndClient(id, usuarioLogueado);
        if (solicitudes.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Solicitud no encontrada o sin permisos");
            return "redirect:/requests/mis-solicitudes";
        }

        ServiceRequest solicitud = solicitudes.get(0);
        if (solicitud.getStatus() != ServiceRequest.Status.CANCELLED) {
            redirectAttributes.addFlashAttribute("error", "Solo se pueden reactivar solicitudes canceladas");
            return "redirect:/requests/mis-solicitudes";
        }

        solicitud.setStatus(ServiceRequest.Status.DRAFT);
        serviceRequestRepository.save(solicitud);
        redirectAttributes.addFlashAttribute("success",
                "Solicitud reactivada correctamente. Ahora está en estado borrador.");
        return "redirect:/requests/mis-solicitudes";
    }

    // Editar una solicitud
    @PostMapping("/editar/{id}")
    public String editarSolicitud(@PathVariable Long id,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam Long categoryId,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        AppUser usuarioLogueado = authService.obtenerUsuarioAutenticado(auth);
        List<ServiceRequest> solicitudes = serviceRequestRepository.findByIdAndClient(id, usuarioLogueado);
        if (solicitudes.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Solicitud no encontrada o sin permisos");
            return "redirect:/requests/mis-solicitudes";
        }
        ServiceRequest solicitud = solicitudes.get(0);

        // Buscar la categoría
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category == null) {
            redirectAttributes.addFlashAttribute("error", "Categoría no válida");
            return "redirect:/requests/mis-solicitudes";
        }

        solicitud.setTitle(title);
        solicitud.setDescription(description);
        solicitud.setCategory(category);
        serviceRequestRepository.save(solicitud);
        redirectAttributes.addFlashAttribute("success", "Solicitud editada correctamente.");
        return "redirect:/requests/mis-solicitudes";
    }

    // Métodos auxiliares para parsear fechas y crear direcciones
    private java.time.LocalDateTime parseFechaHora(String fecha) {
        if (fecha == null || fecha.isBlank())
            return null;
        try {
            return java.time.LocalDate.parse(fecha).atStartOfDay();
        } catch (Exception e) {
            return null;
        }
    }

    // Método auxiliar para crear un objeto Address con todos los datos
    private Address crearDireccion(String address, String city, String postalCode, String province,
            String country) {
        if (address == null || city == null || postalCode == null || province == null || country == null)
            return null;
        Address dir = new Address();
        dir.setAddress(address);
        dir.setCity(city);
        dir.setPostalCode(postalCode);
        dir.setProvince(province);
        dir.setCountry(country);
        return dir;
    }

}
