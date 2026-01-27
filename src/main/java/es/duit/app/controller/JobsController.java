package es.duit.app.controller;

import es.duit.app.entity.AppUser;
import es.duit.app.entity.Category;
import es.duit.app.entity.ServiceRequest;
import es.duit.app.service.ServiceRequestService;
import es.duit.app.repository.CategoryRepository;
import es.duit.app.repository.ServiceRequestRepository;
import es.duit.app.repository.AppUserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/jobs")
public class JobsController {

    private final ServiceRequestService serviceRequestService;
    private final ServiceRequestRepository serviceRequestRepository;
    private final CategoryRepository categoryRepository;
    private final AppUserRepository appUserRepository;

    public JobsController(ServiceRequestService serviceRequestService,
            ServiceRequestRepository serviceRequestRepository,
            CategoryRepository categoryRepository,
            AppUserRepository appUserRepository) {
        this.serviceRequestService = serviceRequestService;
        this.serviceRequestRepository = serviceRequestRepository;
        this.categoryRepository = categoryRepository;
        this.appUserRepository = appUserRepository;
    }

    // Método auxiliar para obtener usuario autenticado
    private AppUser obtenerUsuarioAutenticado(Authentication auth) {
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return appUserRepository.findByUsername(auth.getName())
                    .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));
        }
        throw new IllegalStateException("Usuario no autenticado");
    }

    // Mostrar solicitudes del usuario
    @GetMapping("/mis-solicitudes")
    public String mostrarMisSolicitudes(Authentication auth, Model model) {

        AppUser usuarioLogueado = obtenerUsuarioAutenticado(auth);

        // Buscar solicitudes del usuario
        List<ServiceRequest> todasLasSolicitudes = serviceRequestRepository
                .findByClientOrderByCreatedAtDesc(usuarioLogueado);
        model.addAttribute("solicitudesExistentes", todasLasSolicitudes);

        return "jobs/mis-solicitudes";
    }

    // Mostrar formulario crear solicitud
    @GetMapping("/crear")
    public String mostrarFormularioCrear(Authentication auth, Model model) {

        AppUser usuarioLogueado = obtenerUsuarioAutenticado(auth);
        ServiceRequest solicitudVacia = new ServiceRequest();
        model.addAttribute("serviceRequest", solicitudVacia);

        // Obtener categorías
        List<Category> categoriasDisponibles = categoryRepository.findAllActiveCategories();
        model.addAttribute("categories", categoriasDisponibles);

        return "jobs/crear";
    }

    @PostMapping("/crear")
    public String crearSolicitud(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(required = false) String deadline,
            @RequestParam(required = false) Long categoryId,
            Authentication auth,
            Model model,
            RedirectAttributes redirectAttributes) {

        AppUser usuarioLogueado = obtenerUsuarioAutenticado(auth);

        // Validaciones básicas
        if ((title == null || title.trim().isEmpty()) ||
                (description == null || description.trim().isEmpty()) ||
                categoryId == null) {

            model.addAttribute("error", "Todos los campos obligatorios deben estar completos");

            // Preparar formulario de vuelta
            ServiceRequest solicitudConErrores = new ServiceRequest();
            solicitudConErrores.setTitle(title);
            solicitudConErrores.setDescription(description);
            model.addAttribute("serviceRequest", solicitudConErrores);

            List<Category> categorias = categoryRepository.findAllActiveCategories();
            model.addAttribute("categories", categorias);
            return "jobs/crear";
        }

        try {
            // Buscar categoría
            Category categoriaSeleccionada = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

            // Crear solicitud
            ServiceRequest solicitudNueva = new ServiceRequest();
            solicitudNueva.setTitle(title.trim());
            solicitudNueva.setDescription(description.trim());
            solicitudNueva.setClient(usuarioLogueado);
            solicitudNueva.setCategory(categoriaSeleccionada);
            solicitudNueva.setStatus(ServiceRequest.Status.DRAFT);

            // Procesar fecha opcional
            if (deadline != null && !deadline.trim().isEmpty()) {
                try {
                    solicitudNueva.setDeadline(LocalDateTime.parse(deadline));
                } catch (Exception e) {

                }
            }

            // Guardar
            serviceRequestRepository.save(solicitudNueva);

            // Redirigir con mensaje
            redirectAttributes.addFlashAttribute("success",
                    "Solicitud creada exitosamente. Puedes publicarla cuando esté lista.");
            return "redirect:/jobs/mis-solicitudes";

        } catch (Exception errorGeneral) {
            // Manejar error simple
            model.addAttribute("error", "Error al crear solicitud: " + errorGeneral.getMessage());

            ServiceRequest solicitudConError = new ServiceRequest();
            solicitudConError.setTitle(title);
            solicitudConError.setDescription(description);
            model.addAttribute("serviceRequest", solicitudConError);

            List<Category> categorias = categoryRepository.findAllActiveCategories();
            model.addAttribute("categories", categorias);
            return "jobs/crear";
        }
    }

    // Publicar solicitud
    @PostMapping("/publicar/{id}")
    public String publicarSolicitud(@PathVariable Long id,
            Authentication auth,
            RedirectAttributes redirectAttributes) {

        AppUser usuarioLogueado = obtenerUsuarioAutenticado(auth);
        try {
            // Buscar solicitud del usuario
            ServiceRequest solicitudAPublicar = serviceRequestRepository.findByIdAndClient(id, usuarioLogueado)
                    .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

            // Verificar que está en borrador
            if (solicitudAPublicar.getStatus() != ServiceRequest.Status.DRAFT) {
                throw new IllegalStateException("Solo se pueden publicar solicitudes en borrador");
            }

            // Publicar
            solicitudAPublicar.setStatus(ServiceRequest.Status.PUBLISHED);
            serviceRequestRepository.save(solicitudAPublicar);

            // Redirigir
            redirectAttributes.addFlashAttribute("success", "Solicitud publicada exitosamente");
            return "redirect:/jobs/mis-solicitudes";
        } catch (Exception errorPublicar) {
            redirectAttributes.addFlashAttribute("error", "Error al publicar: " + errorPublicar.getMessage());
            return "redirect:/jobs/mis-solicitudes";
        }
    }

    // Mostrar formulario editar
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id,
            Authentication auth,
            Model model,
            RedirectAttributes redirectAttributes) {

        AppUser usuarioLogueado = obtenerUsuarioAutenticado(auth);
        try {
            // Buscar solicitud del usuario
            ServiceRequest solicitudParaEditar = serviceRequestRepository.findByIdAndClient(id, usuarioLogueado)
                    .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

            // Verificar que se puede editar
            if (solicitudParaEditar.getStatus() != ServiceRequest.Status.DRAFT) {
                throw new IllegalStateException("Solo se pueden editar solicitudes en borrador");
            }

            // Preparar formulario
            model.addAttribute("serviceRequest", solicitudParaEditar);
            List<Category> categoriasDisponibles = categoryRepository.findAllActiveCategories();
            model.addAttribute("categories", categoriasDisponibles);
            model.addAttribute("isEditing", true);
            model.addAttribute("editingId", id);

            return "jobs/crear";
        } catch (Exception errorEdicion) {
            redirectAttributes.addFlashAttribute("error", "Error: " + errorEdicion.getMessage());
            return "redirect:/jobs/mis-solicitudes";
        }
    }

    // Metodo para procesar la actualización de una solicitud existente
    @PostMapping("/editar/{id}")
    public String actualizarSolicitud(@PathVariable Long id,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(required = false) String deadline,
            @RequestParam(required = false) Long categoryId,
            Authentication auth,
            Model model,
            RedirectAttributes redirectAttributes) {

        AppUser usuarioLogueado = obtenerUsuarioAutenticado(auth);

        // Validación simple
        if ((title == null || title.trim().isEmpty()) ||
                (description == null || description.trim().isEmpty()) ||
                categoryId == null) {
            model.addAttribute("error", "Todos los campos son obligatorios");

            ServiceRequest solicitudConErrores = new ServiceRequest();
            solicitudConErrores.setTitle(title);
            solicitudConErrores.setDescription(description);
            model.addAttribute("serviceRequest", solicitudConErrores);

            List<Category> categorias = categoryRepository.findAllActiveCategories();
            model.addAttribute("categories", categorias);
            model.addAttribute("isEditing", true);
            model.addAttribute("editingId", id);
            return "jobs/crear";
        }

        try {
            // Buscar solicitud
            ServiceRequest solicitudAActualizar = serviceRequestRepository.findByIdAndClient(id, usuarioLogueado)
                    .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

            // Verificar que se puede editar
            if (solicitudAActualizar.getStatus() != ServiceRequest.Status.DRAFT) {
                throw new IllegalStateException("Solo se pueden editar solicitudes en borrador");
            }

            // Buscar categoría
            Category categoriaNueva = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

            // Actualizar campos (sin tocar el client/usuario)
            solicitudAActualizar.setTitle(title.trim());
            solicitudAActualizar.setDescription(description.trim());
            solicitudAActualizar.setCategory(categoriaNueva);

            // Procesar fecha opcional
            if (deadline != null && !deadline.trim().isEmpty()) {
                try {
                    solicitudAActualizar.setDeadline(LocalDateTime.parse(deadline));
                } catch (Exception e) {
                    // Ignorar si no se puede parsear
                }
            }

            serviceRequestRepository.save(solicitudAActualizar);

            // Redirigir
            redirectAttributes.addFlashAttribute("success", "Solicitud actualizada exitosamente.");
            return "redirect:/jobs/mis-solicitudes";

        } catch (Exception errorActualizacion) {
            // Si hay error, volver al formulario con mensaje de error
            model.addAttribute("error", "Error al actualizar: " + errorActualizacion.getMessage());

            ServiceRequest solicitudConError = new ServiceRequest();
            solicitudConError.setTitle(title);
            solicitudConError.setDescription(description);
            model.addAttribute("serviceRequest", solicitudConError);

            // Obtener categorías
            List<Category> categoriasParaError = categoryRepository.findAllActiveCategories();
            model.addAttribute("categories", categoriasParaError);
            model.addAttribute("isEditing", true);
            model.addAttribute("editingId", id);
            return "jobs/crear";
        }
    }

    // Eliminar solicitud
    @PostMapping("/eliminar/{id}")
    public String eliminarSolicitud(@PathVariable Long id,
            Authentication auth,
            RedirectAttributes redirectAttributes) {

        AppUser usuarioLogueado = obtenerUsuarioAutenticado(auth);
        try {
            // Buscar solicitud
            ServiceRequest solicitudAEliminar = serviceRequestRepository.findByIdAndClient(id, usuarioLogueado)
                    .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

            // Verificar que se puede eliminar
            if (solicitudAEliminar.getStatus() == ServiceRequest.Status.IN_PROGRESS ||
                    solicitudAEliminar.getStatus() == ServiceRequest.Status.COMPLETED) {
                throw new IllegalStateException("No se pueden eliminar solicitudes en progreso o completadas");
            }

            // Eliminar
            serviceRequestRepository.delete(solicitudAEliminar);

            // Redirigir con mensaje
            redirectAttributes.addFlashAttribute("success", "Solicitud eliminada exitosamente");
            return "redirect:/jobs/mis-solicitudes";
        } catch (Exception errorEliminacion) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar: " + errorEliminacion.getMessage());
            return "redirect:/jobs/mis-solicitudes";
        }
    }

    // Metodo para cancelar una solicitud
    @PostMapping("/cancelar/{id}")
    public String cancelarSolicitud(@PathVariable Long id,
            Authentication auth,
            RedirectAttributes redirectAttributes) {

        AppUser usuarioLogueado = obtenerUsuarioAutenticado(auth);
        try {

            String mensajeResultado = serviceRequestService.cancelarSolicitud(id, usuarioLogueado.getUsername());

            // Redirigir
            redirectAttributes.addFlashAttribute("success", mensajeResultado);
            return "redirect:/jobs/mis-solicitudes";
        } catch (Exception errorCancelacion) {
            redirectAttributes.addFlashAttribute("error", "Error al cancelar: " + errorCancelacion.getMessage());
            return "redirect:/jobs/mis-solicitudes";
        }
    }

    // Página buscar trabajos
    @GetMapping("/buscar")
    public String buscar() {
        return "jobs/buscar";
    }

    // Página postular a trabajos
    @GetMapping("/postular")
    public String postular() {
        return "jobs/postular";
    }
}