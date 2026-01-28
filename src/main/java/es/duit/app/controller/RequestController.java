
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
    public String mostrarFormularioCrear(Authentication auth, Model model) {
        AppUser usuarioLogueado = authService.obtenerUsuarioAutenticado(auth);
        model.addAttribute("habitualAddress", usuarioLogueado.getAddress());
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

    // Crear nueva solicitud
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
            Authentication auth,
            RedirectAttributes redirectAttributes) {

        AppUser usuario = authService.obtenerUsuarioAutenticado(auth);
        ServiceRequest solicitud = new ServiceRequest();

        solicitud.setTitle(title);
        solicitud.setDescription(description);
        solicitud.setDeadline(parseFechaHora(deadline));
        solicitud.setClient(usuario);
        solicitud.setStatus(ServiceRequest.Status.DRAFT);
        solicitud.setRequestedAt(java.time.LocalDateTime.now());

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
        redirectAttributes.addFlashAttribute("success", "Solicitud creada correctamente.");
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
            return "redirect:/jobs/mis-solicitudes";
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
        return "redirect:/jobs/mis-solicitudes";
    }

    // Eliminar una solicitud (
    @PostMapping("/eliminar/{id}")
    public String eliminarSolicitud(@PathVariable Long id, Authentication auth, RedirectAttributes redirectAttributes) {
        AppUser usuarioLogueado = authService.obtenerUsuarioAutenticado(auth);
        List<ServiceRequest> solicitudes = serviceRequestRepository.findByIdAndClient(id, usuarioLogueado);
        if (solicitudes.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Solicitud no encontrada o sin permisos");
            return "redirect:/jobs/mis-solicitudes";
        }
        serviceRequestRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Solicitud eliminada correctamente.");
        return "redirect:/jobs/mis-solicitudes";
    }

    // Editar una solicitud
    @PostMapping("/editar/{id}")
    public String editarSolicitud(@PathVariable Long id,
            @RequestParam String title,
            @RequestParam String description,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        AppUser usuarioLogueado = authService.obtenerUsuarioAutenticado(auth);
        List<ServiceRequest> solicitudes = serviceRequestRepository.findByIdAndClient(id, usuarioLogueado);
        if (solicitudes.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Solicitud no encontrada o sin permisos");
            return "redirect:/jobs/mis-solicitudes";
        }
        ServiceRequest solicitud = solicitudes.get(0);
        solicitud.setTitle(title);
        solicitud.setDescription(description);
        serviceRequestRepository.save(solicitud);
        redirectAttributes.addFlashAttribute("success", "Solicitud editada correctamente.");
        return "redirect:/jobs/mis-solicitudes";
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
