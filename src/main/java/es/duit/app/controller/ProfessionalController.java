
package es.duit.app.controller;

import es.duit.app.entity.AppUser;
import es.duit.app.entity.ServiceRequest;
import es.duit.app.repository.AppUserRepository;
import es.duit.app.repository.ServiceRequestRepository;
import org.springframework.security.core.Authentication;
import es.duit.app.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import es.duit.app.entity.JobApplication;

import es.duit.app.repository.JobApplicationRepository;
import es.duit.app.repository.ProfessionalProfileRepository;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.util.Optional;

@Controller
@RequestMapping("/professional")
public class ProfessionalController {

    private final ServiceRequestRepository serviceRequestRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final AuthService authService;

    public ProfessionalController(AppUserRepository appUserRepository,
            ServiceRequestRepository serviceRequestRepository,
            JobApplicationRepository jobApplicationRepository,
            ProfessionalProfileRepository professionalProfileRepository,
            AuthService authService) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.jobApplicationRepository = jobApplicationRepository;
        this.authService = authService;
    }

    // Mostrar página para buscar trabajos
    @GetMapping("/buscar")
    public String buscarTrabajos(Authentication auth, Model model) {
        authService.obtenerUsuarioAutenticado(auth);
        List<ServiceRequest> ofertas = serviceRequestRepository.findAll();
        model.addAttribute("ofertas", ofertas);
        return "jobs/buscar";
    }

    // Mostrar postulaciones del profesional
    @GetMapping("/postulaciones")
    public String verPostulaciones(Authentication auth, Model model) {
        AppUser usuario = authService.obtenerUsuarioAutenticado(auth);
        List<JobApplication> postulaciones = jobApplicationRepository.findAll()
                .stream()
                .filter(p -> p.getProfessional() != null && p.getProfessional().getId().equals(usuario.getId()))
                .toList();
        model.addAttribute("postulaciones", postulaciones);
        return "jobs/postular";
    }

    // Procesar postulación a una oferta
    @PostMapping("/postular/{ofertaId}")
    public String postularse(
            @PathVariable Long ofertaId,
            @RequestParam(name = "mensaje", required = false) String mensaje,
            @RequestParam(name = "precio") BigDecimal precio,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        // Buscar la oferta
        Optional<ServiceRequest> optOferta = serviceRequestRepository.findById(ofertaId);
        if (optOferta.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La oferta no existe.");
            return "redirect:/professional/buscar";
        }
        ServiceRequest oferta = optOferta.get();
        // Crear y guardar la postulación
        JobApplication postulacion = new JobApplication();
        postulacion.setRequest(oferta);
        postulacion.setProfessional(null); 
        postulacion.setMessage(mensaje);
        postulacion.setProposedPrice(precio);
        postulacion.setStatus(JobApplication.Status.PENDING);
        jobApplicationRepository.save(postulacion);
        redirectAttributes.addFlashAttribute("success", "¡Postulación enviada correctamente!");
        return "redirect:/professional/buscar";
    }

}
