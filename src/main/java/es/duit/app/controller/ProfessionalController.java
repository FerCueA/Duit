
package es.duit.app.controller;

import es.duit.app.entity.AppUser;
import es.duit.app.entity.ServiceRequest;
import es.duit.app.entity.ServiceJob;
import es.duit.app.repository.AppUserRepository;
import es.duit.app.repository.ServiceRequestRepository;
import es.duit.app.repository.ServiceJobRepository;
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
    private final ServiceJobRepository serviceJobRepository;
    private final AuthService authService;

    public ProfessionalController(AppUserRepository appUserRepository,
            ServiceRequestRepository serviceRequestRepository,
            JobApplicationRepository jobApplicationRepository,
            ServiceJobRepository serviceJobRepository,
            ProfessionalProfileRepository professionalProfileRepository,
            AuthService authService) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.jobApplicationRepository = jobApplicationRepository;
        this.serviceJobRepository = serviceJobRepository;
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
        
        // Obtener todas las postulaciones del profesional
        List<JobApplication> postulaciones = jobApplicationRepository.findByProfessional(usuario.getProfessionalProfile());
        
        // Obtener trabajos en progreso del profesional
        List<ServiceJob> trabajosEnProgreso = serviceJobRepository.findByProfesional(usuario);
        
        model.addAttribute("postulaciones", postulaciones);
        model.addAttribute("trabajosEnProgreso", trabajosEnProgreso);
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

        try {
            // Buscar la oferta
            Optional<ServiceRequest> optOferta = serviceRequestRepository.findById(ofertaId);
            if (optOferta.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "La oferta no existe.");
                return "redirect:/professional/buscar";
            }
            ServiceRequest oferta = optOferta.get();

            // Obtener el usuario autenticado y su perfil profesional
            AppUser usuario = authService.obtenerUsuarioAutenticado(auth);
            if (usuario.getProfessionalProfile() == null) {
                redirectAttributes.addFlashAttribute("error", "No tienes un perfil profesional configurado.");
                return "redirect:/professional/buscar";
            }

            // Verificar si ya se ha postulado
            List<JobApplication> todasLasPostulaciones = jobApplicationRepository.findByRequest(oferta);
            boolean yaSePostulo = todasLasPostulaciones.stream()
                    .anyMatch(app -> app.getProfessional() != null &&
                            app.getProfessional().getUser() != null &&
                            app.getProfessional().getUser().getId().equals(usuario.getId()));

            if (yaSePostulo) {
                redirectAttributes.addFlashAttribute("error", "Ya te has postulado a esta oferta.");
                return "redirect:/professional/buscar";
            }

            // Crear y guardar la postulación
            JobApplication postulacion = new JobApplication();
            postulacion.setRequest(oferta);
            postulacion.setProfessional(usuario.getProfessionalProfile());
            postulacion.setMessage(mensaje);
            postulacion.setProposedPrice(precio);
            postulacion.setStatus(JobApplication.Status.PENDING);
            jobApplicationRepository.save(postulacion);

            redirectAttributes.addFlashAttribute("success", "¡Postulación enviada correctamente!");
            return "redirect:/professional/buscar";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al enviar la postulación. Intenta de nuevo.");
            return "redirect:/professional/buscar";
        }
    }

}
