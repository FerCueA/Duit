package es.duit.app.controller;

import es.duit.app.dto.SearchRequestDTO;
import es.duit.app.entity.AppUser;
import es.duit.app.entity.JobApplication;
import es.duit.app.entity.ServiceJob;
import es.duit.app.repository.JobApplicationRepository;
import es.duit.app.repository.ServiceJobRepository;
import es.duit.app.service.AuthService;
import es.duit.app.service.SearchService;
import es.duit.app.service.JobApplicationService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

// CONTROLADOR DE PROFESIONALES
@Controller
@RequestMapping("/professional")
@RequiredArgsConstructor
public class ProfessionalController {

    private final JobApplicationRepository jobApplicationRepository;
    private final ServiceJobRepository serviceJobRepository;
    private final AuthService authService;
    private final JobApplicationService jobApplicationService;
    private final SearchService searchService;

    // Mostrar página de búsqueda
    @GetMapping("/search")
    public String searchJobs(
            @RequestParam(required = false) String textoBusqueda,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) String codigoPostal,
            Authentication auth,
            Model model) {

        // Crear DTO a partir de parámetros GET
        SearchRequestDTO filters = new SearchRequestDTO();
        filters.setTextoBusqueda(textoBusqueda);
        filters.setCategoriaId(categoriaId);
        filters.setCodigoPostal(codigoPostal);

        AppUser currentUser = authService.getAuthenticatedUser(auth);
        searchService.prepareSearchPageData(filters, currentUser, model);

        return "jobs/search";
    }

    // Buscar con filtros
    @PostMapping("/search")
    public String searchJobsWithForm(
            @Valid @ModelAttribute SearchRequestDTO filters,
            BindingResult bindingResult,
            Authentication auth,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Si hay errores, redirigir con mensaje de error
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Revisa los datos del formulario");
            return "redirect:/professional/search";
        }

        AppUser currentUser = authService.getAuthenticatedUser(auth);
        searchService.prepareSearchPageData(filters, currentUser, model);
        model.addAttribute("filtrosAplicados", filters);

        return "jobs/search";
    }

    // Ver mis postulaciones
    @GetMapping({ "/applications" })
    public String verPostulaciones(Authentication auth, Model model) {
        AppUser usuario = authService.getAuthenticatedUser(auth);

        // Postulaciones del profesional
        List<JobApplication> postulaciones = jobApplicationRepository
                .findByProfessional(usuario.getProfessionalProfile());

        // Obtener trabajos en progreso
        List<ServiceJob> trabajosEnProgreso = serviceJobRepository.findByProfesional(usuario);

        model.addAttribute("postulaciones", postulaciones);
        model.addAttribute("trabajosEnProgreso", trabajosEnProgreso);
        return "jobs/myaplication";
    }

    // =========================================================================
    // EDITAR UNA POSTULACION PROPIA (SOLO PENDING)
    // =========================================================================
    @PostMapping("/applications/edit/{postulacionId}")
    public String editarPostulacion(
            @PathVariable Long postulacionId,
            @RequestParam(name = "mensaje", required = false) String mensaje,
            @RequestParam(name = "precio", required = false) String precioStr,
            Authentication auth,
            RedirectAttributes redirectAttributes) {

        AppUser usuario = authService.getAuthenticatedUser(auth);

        BigDecimal precio = null;
        if (precioStr != null && !precioStr.trim().isEmpty()) {
            try {
                String precioLimpio = precioStr.replace(",", ".");
                precio = new BigDecimal(precioLimpio);
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Precio no válido");
                return "redirect:/professional/applications";
            }
        }

        try {
            jobApplicationService.editarPostulacion(postulacionId, usuario, precio, mensaje);
            redirectAttributes.addFlashAttribute("success", "Postulación actualizada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/professional/applications";
    }

    // =========================================================================
    // RETIRAR UNA POSTULACION PROPIA (SOLO PENDING)
    // =========================================================================
    @PostMapping("/applications/withdraw/{postulacionId}")
    public String retirarPostulacion(
            @PathVariable Long postulacionId,
            Authentication auth,
            RedirectAttributes redirectAttributes) {

        AppUser usuario = authService.getAuthenticatedUser(auth);

        try {
            jobApplicationService.retirarPostulacion(postulacionId, usuario);
            redirectAttributes.addFlashAttribute("success", "Postulación retirada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/professional/applications";
    }

    // Enviar postulación
    @PostMapping("/postular/{ofertaId}")
    public String postularse(
            @PathVariable Long ofertaId,
            @RequestParam(name = "mensaje", required = false) String mensaje,
            @RequestParam(name = "precio") String precioStr,
            Authentication auth,
            RedirectAttributes redirectAttributes) {

        // Validar precio
        BigDecimal precio;
        try {
            String precioLimpio = precioStr.replace(",", ".");
            precio = new BigDecimal(precioLimpio);

            if (precio.compareTo(BigDecimal.ONE) < 0 || precio.compareTo(new BigDecimal("99999")) > 0) {
                redirectAttributes.addFlashAttribute("error", "El precio debe estar entre 1€ y 99.999€");
                return "redirect:/professional/search";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Precio no válido");
            return "redirect:/professional/search";
        }

        AppUser usuario = authService.getAuthenticatedUser(auth);

        try {
            jobApplicationService.postularseAOferta(ofertaId, precio, mensaje, usuario);
            redirectAttributes.addFlashAttribute("success", "Postulación enviada correctamente");
            return "redirect:/professional/search";

        } catch (Exception e) {
            System.err.println("Error al enviar postulación: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/professional/search";
        }
    }
}
