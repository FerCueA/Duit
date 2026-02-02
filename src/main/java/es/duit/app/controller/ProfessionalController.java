package es.duit.app.controller;

import es.duit.app.entity.AppUser;
import es.duit.app.entity.Category;
import es.duit.app.entity.JobApplication;
import es.duit.app.entity.ServiceJob;
import es.duit.app.entity.ServiceRequest;
import es.duit.app.repository.CategoryRepository;
import es.duit.app.repository.JobApplicationRepository;
import es.duit.app.repository.ServiceJobRepository;
import es.duit.app.repository.ServiceRequestRepository;
import es.duit.app.service.AuthService;
import es.duit.app.service.JobApplicationService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// Controlador para profesionales
@Controller
@RequestMapping("/professional")
public class ProfessionalController {

    private final ServiceRequestRepository serviceRequestRepository;
    private final CategoryRepository categoryRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final ServiceJobRepository serviceJobRepository;
    private final AuthService authService;
    private final JobApplicationService jobApplicationService;

    public ProfessionalController(
            ServiceRequestRepository serviceRequestRepository,
            CategoryRepository categoryRepository,
            JobApplicationRepository jobApplicationRepository,
            ServiceJobRepository serviceJobRepository,
            AuthService authService,
            JobApplicationService jobApplicationService) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.categoryRepository = categoryRepository;
        this.jobApplicationRepository = jobApplicationRepository;
        this.serviceJobRepository = serviceJobRepository;
        this.authService = authService;
        this.jobApplicationService = jobApplicationService;
    }

    // Buscar trabajos
    @GetMapping({"/search"})
    public String buscarTrabajos(Authentication auth, Model model) {
        authService.getAuthenticatedUser(auth);
        
        // Obtener solo solicitudes publicadas
        List<ServiceRequest> ofertas = serviceRequestRepository.findByStatus(ServiceRequest.Status.PUBLISHED);
        
        // Obtener categorías activas
        List<Category> categorias = categoryRepository.findByActiveTrue();
        
        // Obtener códigos postales únicos de las ofertas
        Set<String> codigosPostales = ofertas.stream()
            .map(o -> o.getEffectiveServiceAddress() != null ? o.getEffectiveServiceAddress().getPostalCode() : null)
            .filter(cp -> cp != null && !cp.isEmpty())
            .collect(Collectors.toSet());
        
        model.addAttribute("ofertas", ofertas);
        model.addAttribute("categorias", categorias);
        model.addAttribute("codigosPostales", codigosPostales);
        return "jobs/search";
    }

    // Ver mis postulaciones
    @GetMapping({"/applications"})
    public String verPostulaciones(Authentication auth, Model model) {
        AppUser usuario = authService.getAuthenticatedUser(auth);
        
        // Postulaciones del profesional
        List<JobApplication> postulaciones = jobApplicationRepository.findByProfessional(usuario.getProfessionalProfile());
        
        // Obtener trabajos en progreso
        List<ServiceJob> trabajosEnProgreso = serviceJobRepository.findByProfesional(usuario);
        
        model.addAttribute("postulaciones", postulaciones);
        model.addAttribute("trabajosEnProgreso", trabajosEnProgreso);
        return "jobs/myaplication";
    }

    // Postularse a una oferta
    @PostMapping("/postular/{ofertaId}")
    public String postularse(
            @PathVariable Long ofertaId,
            @RequestParam(name = "mensaje", required = false) String mensaje,
            @RequestParam(name = "precio") BigDecimal precio,
            Authentication auth,
            RedirectAttributes redirectAttributes) {

        try {
            AppUser usuario = authService.getAuthenticatedUser(auth);
            jobApplicationService.postularseAOferta(ofertaId, precio, mensaje, usuario);
            redirectAttributes.addFlashAttribute("success", "¡Postulación enviada correctamente!");
            return "redirect:/professional/search";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/professional/search";
        }
    }
}
