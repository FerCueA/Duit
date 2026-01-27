package es.duit.app.controller;

import es.duit.app.entity.AppUser;
import es.duit.app.entity.Address;
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

/**
 * Controlador para manejar todas las operaciones relacionadas con solicitudes
 * de trabajo.
 * Permite a los usuarios crear, editar, publicar, cancelar y eliminar
 * solicitudes.
 */
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
            List<AppUser> usuarios = appUserRepository.findByUsername(auth.getName());
            if (usuarios.isEmpty()) {
                throw new IllegalStateException("Usuario no encontrado");
            }
            return usuarios.get(0);
        }
        throw new IllegalStateException("Usuario no autenticado");
    }

    // Método auxiliar para cargar categorías ordenadas
    private List<Category> cargarCategorias() {
        List<Category> categorias = categoryRepository.findByActiveTrue();
        categorias.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        return categorias;
    }

    // Método auxiliar para agregar datos comunes al modelo para formularios
    private void anadirDatosFormularioAlModelo(Model model, AppUser usuario, ServiceRequest solicitud,
            boolean esEdicion, Long idEdicion) {
        model.addAttribute("user", usuario);
        model.addAttribute("serviceRequest", solicitud);
        model.addAttribute("categories", cargarCategorias());
        if (esEdicion) {
            model.addAttribute("isEditing", true);
            model.addAttribute("editingId", idEdicion);
        }
    }

    // Método auxiliar para validar que la dirección tiene todos los campos
    // requeridos
    private boolean direccionEsValida(String street, String city, String postalCode, String province) {
        return street != null && !street.trim().isEmpty() &&
                city != null && !city.trim().isEmpty() &&
                postalCode != null && !postalCode.trim().isEmpty() &&
                province != null && !province.trim().isEmpty();
    }

    // Método auxiliar para crear un objeto Address con todos los datos
    private Address crearDireccion(String street, String city, String postalCode, String province, String country) {
        Address direccion = new Address();
        direccion.setAddress(street.trim());
        direccion.setCity(city.trim());
        direccion.setPostalCode(postalCode.trim());
        direccion.setProvince(province.trim());
        direccion.setCountry(country != null ? country.trim() : "España");
        return direccion;
    }

    // Mostrar solicitudes del usuario
    @GetMapping("/mis-solicitudes")
    public String mostrarMisSolicitudes(Authentication auth, Model model) {

        AppUser usuarioLogueado = obtenerUsuarioAutenticado(auth);

        // Buscar solicitudes del usuario
        List<ServiceRequest> todasLasSolicitudes = serviceRequestRepository
                .findByClient(usuarioLogueado);

        // Ordenar por fecha (las más recientes primero)
        // Manejamos null en getCreatedAt() de forma segura
        todasLasSolicitudes.sort((a, b) -> {
            LocalDateTime fechaA = a.getCreatedAt();
            LocalDateTime fechaB = b.getCreatedAt();

            // Si ambas tienen fecha, comparar normalmente
            if (fechaA != null && fechaB != null) {
                return fechaB.compareTo(fechaA);
            }
            // Si solo una tiene fecha, la que la tiene va primero
            if (fechaA != null) {
                return -1;
            }
            if (fechaB != null) {
                return 1;
            }
            // Si ninguna tiene fecha, mantener orden
            return 0;
        });

        model.addAttribute("solicitudesExistentes", todasLasSolicitudes);

        return "jobs/mis-solicitudes";
    }

    // Mostrar formulario para crear una nueva solicitud
    @GetMapping("/crear")
    public String mostrarFormularioCrear(Authentication auth, Model model) {
        AppUser usuarioLogueado = obtenerUsuarioAutenticado(auth);
        ServiceRequest solicitudVacia = new ServiceRequest();

        anadirDatosFormularioAlModelo(model, usuarioLogueado, solicitudVacia, false, null);

        return "jobs/crear";
    }

    @PostMapping("/crear")
    public String crearSolicitud(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(required = false) String deadline,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String addressOption,
            @RequestParam(required = false, name = "serviceAddress.address") String serviceAddressStreet,
            @RequestParam(required = false, name = "serviceAddress.city") String serviceAddressCity,
            @RequestParam(required = false, name = "serviceAddress.postalCode") String serviceAddressPostalCode,
            @RequestParam(required = false, name = "serviceAddress.province") String serviceAddressProvince,
            @RequestParam(required = false, name = "serviceAddress.country") String serviceAddressCountry,
            @RequestParam(required = false, name = "serviceAddress.additionalInfo") String serviceAddressAdditionalInfo,
            Authentication auth,
            Model model,
            RedirectAttributes redirectAttributes) {

        AppUser usuarioLogueado = obtenerUsuarioAutenticado(auth);

        // Validar que los campos obligatorios no están vacíos
        boolean titleVacio = title == null || title.trim().isEmpty();
        boolean descriptionVacio = description == null || description.trim().isEmpty();
        boolean categoryVacia = categoryId == null;

        if (titleVacio || descriptionVacio || categoryVacia) {
            model.addAttribute("error", "Todos los campos obligatorios deben estar completos");
            ServiceRequest solicitudConErrores = new ServiceRequest();
            solicitudConErrores.setTitle(title);
            solicitudConErrores.setDescription(description);
            anadirDatosFormularioAlModelo(model, usuarioLogueado, solicitudConErrores, false, null);
            return "jobs/crear";
        }

        try {
            // Buscar la categoría seleccionada
            Category categoriaSeleccionada = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

            // Crear la nueva solicitud
            ServiceRequest solicitudNueva = new ServiceRequest();
            solicitudNueva.setTitle(title.trim());
            solicitudNueva.setDescription(description.trim());
            solicitudNueva.setClient(usuarioLogueado);
            solicitudNueva.setCategory(categoriaSeleccionada);
            solicitudNueva.setStatus(ServiceRequest.Status.DRAFT);

            // Procesar la fecha si se proporciona
            if (deadline != null && !deadline.trim().isEmpty()) {
                try {
                    solicitudNueva.setDeadline(LocalDateTime.parse(deadline));
                } catch (Exception e) {
                    // Si no se puede parsear la fecha, simplemente no la asignamos
                }
            }

            // Procesar la dirección específica si se seleccionó
            if ("specific".equals(addressOption) && direccionEsValida(serviceAddressStreet, serviceAddressCity,
                    serviceAddressPostalCode, serviceAddressProvince)) {
                Address direccionEspecifica = crearDireccion(serviceAddressStreet, serviceAddressCity,
                        serviceAddressPostalCode, serviceAddressProvince, serviceAddressCountry);
                solicitudNueva.setServiceAddress(direccionEspecifica);
            }

            // Guardar la solicitud en la base de datos
            serviceRequestRepository.save(solicitudNueva);

            redirectAttributes.addFlashAttribute("success",
                    "Solicitud creada exitosamente. Puedes publicarla cuando esté lista.");
            return "redirect:/jobs/mis-solicitudes";

        } catch (Exception errorGeneral) {
            model.addAttribute("error", "Error al crear solicitud: " + errorGeneral.getMessage());
            ServiceRequest solicitudConError = new ServiceRequest();
            solicitudConError.setTitle(title);
            solicitudConError.setDescription(description);
            anadirDatosFormularioAlModelo(model, usuarioLogueado, solicitudConError, false, null);
            return "jobs/crear";
        }
    }

    // Publicar una solicitud de borrador a publicada
    @PostMapping("/publicar/{id}")
    public String publicarSolicitud(@PathVariable Long id,
            Authentication auth,
            RedirectAttributes redirectAttributes) {

        AppUser usuarioLogueado = obtenerUsuarioAutenticado(auth);
        try {
            // Buscar la solicitud del usuario
            List<ServiceRequest> solicitudes = serviceRequestRepository.findByIdAndClient(id, usuarioLogueado);
            if (solicitudes.isEmpty()) {
                throw new IllegalArgumentException("Solicitud no encontrada");
            }
            ServiceRequest solicitudAPublicar = solicitudes.get(0);

            // Verificar que está en borrador
            if (solicitudAPublicar.getStatus() != ServiceRequest.Status.DRAFT) {
                throw new IllegalStateException("Solo se pueden publicar solicitudes en borrador");
            }

            // Cambiar el estado a publicada
            solicitudAPublicar.setStatus(ServiceRequest.Status.PUBLISHED);
            serviceRequestRepository.save(solicitudAPublicar);

            redirectAttributes.addFlashAttribute("success", "Solicitud publicada exitosamente");
            return "redirect:/jobs/mis-solicitudes";
        } catch (Exception errorPublicar) {
            redirectAttributes.addFlashAttribute("error", "Error al publicar: " + errorPublicar.getMessage());
            return "redirect:/jobs/mis-solicitudes";
        }
    }

    // Mostrar formulario para editar una solicitud existente
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id,
            Authentication auth,
            Model model,
            RedirectAttributes redirectAttributes) {

        AppUser usuarioLogueado = obtenerUsuarioAutenticado(auth);
        try {
            // Buscar la solicitud del usuario
            List<ServiceRequest> solicitudes = serviceRequestRepository.findByIdAndClient(id, usuarioLogueado);
            if (solicitudes.isEmpty()) {
                throw new IllegalArgumentException("Solicitud no encontrada");
            }
            ServiceRequest solicitudParaEditar = solicitudes.get(0);

            // Verificar que la solicitud se puede editar
            boolean puedeEditarse = solicitudParaEditar.getStatus() == ServiceRequest.Status.DRAFT ||
                    (solicitudParaEditar.getStatus() == ServiceRequest.Status.PUBLISHED &&
                            solicitudParaEditar.getApplicationCount() == 0);

            if (!puedeEditarse) {
                throw new IllegalStateException(
                        "Solo se pueden editar solicitudes en borrador o publicadas sin postulaciones");
            }

            anadirDatosFormularioAlModelo(model, usuarioLogueado, solicitudParaEditar, true, id);
            return "jobs/crear";

        } catch (Exception errorEdicion) {
            redirectAttributes.addFlashAttribute("error", "Error: " + errorEdicion.getMessage());
            return "redirect:/jobs/mis-solicitudes";
        }
    }

    // Procesar la actualización de una solicitud existente
    @PostMapping("/editar/{id}")
    public String actualizarSolicitud(@PathVariable Long id,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(required = false) String deadline,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String addressOption,
            @RequestParam(required = false, name = "serviceAddress.address") String serviceAddressStreet,
            @RequestParam(required = false, name = "serviceAddress.city") String serviceAddressCity,
            @RequestParam(required = false, name = "serviceAddress.postalCode") String serviceAddressPostalCode,
            @RequestParam(required = false, name = "serviceAddress.province") String serviceAddressProvince,
            @RequestParam(required = false, name = "serviceAddress.country") String serviceAddressCountry,
            @RequestParam(required = false, name = "serviceAddress.additionalInfo") String serviceAddressAdditionalInfo,
            Authentication auth,
            Model model,
            RedirectAttributes redirectAttributes) {

        AppUser usuarioLogueado = obtenerUsuarioAutenticado(auth);

        // Validar que los campos obligatorios están completos
        boolean titleVacio = title == null || title.trim().isEmpty();
        boolean descriptionVacio = description == null || description.trim().isEmpty();
        boolean categoryVacia = categoryId == null;

        if (titleVacio || descriptionVacio || categoryVacia) {
            model.addAttribute("error", "Todos los campos son obligatorios");
            ServiceRequest solicitudConErrores = new ServiceRequest();
            solicitudConErrores.setTitle(title);
            solicitudConErrores.setDescription(description);
            anadirDatosFormularioAlModelo(model, usuarioLogueado, solicitudConErrores, true, id);
            return "jobs/crear";
        }

        try {
            // Buscar la solicitud del usuario
            List<ServiceRequest> solicitudes = serviceRequestRepository.findByIdAndClient(id, usuarioLogueado);
            if (solicitudes.isEmpty()) {
                throw new IllegalArgumentException("Solicitud no encontrada");
            }
            ServiceRequest solicitudAActualizar = solicitudes.get(0);

            // Verificar que se puede editar
            boolean puedeEditarse = solicitudAActualizar.getStatus() == ServiceRequest.Status.DRAFT ||
                    (solicitudAActualizar.getStatus() == ServiceRequest.Status.PUBLISHED &&
                            solicitudAActualizar.getApplicationCount() == 0);

            if (!puedeEditarse) {
                throw new IllegalStateException(
                        "Solo se pueden editar solicitudes en borrador o publicadas sin postulaciones");
            }

            // Buscar la nueva categoría
            Category categoriaNueva = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

            // Actualizar los campos principales
            solicitudAActualizar.setTitle(title.trim());
            solicitudAActualizar.setDescription(description.trim());
            solicitudAActualizar.setCategory(categoriaNueva);

            // Procesar la fecha si se proporciona
            if (deadline != null && !deadline.trim().isEmpty()) {
                try {
                    solicitudAActualizar.setDeadline(LocalDateTime.parse(deadline));
                } catch (Exception e) {
                    // Si no se puede parsear, ignoramos la fecha
                }
            }

            // Procesar la dirección específica
            if ("specific".equals(addressOption) &&
                    direccionEsValida(serviceAddressStreet, serviceAddressCity,
                            serviceAddressPostalCode, serviceAddressProvince)) {
                Address direccionEspecifica = solicitudAActualizar.getServiceAddress();
                if (direccionEspecifica == null) {
                    direccionEspecifica = new Address();
                }
                direccionEspecifica.setAddress(serviceAddressStreet.trim());
                direccionEspecifica.setCity(serviceAddressCity.trim());
                direccionEspecifica.setPostalCode(serviceAddressPostalCode.trim());
                direccionEspecifica.setProvince(serviceAddressProvince.trim());
                direccionEspecifica.setCountry(serviceAddressCountry != null ? serviceAddressCountry.trim() : "España");
                solicitudAActualizar.setServiceAddress(direccionEspecifica);
            } else if ("habitual".equals(addressOption)) {
                // Si se cambió a dirección habitual, eliminar la dirección específica
                solicitudAActualizar.setServiceAddress(null);
            }

            // Guardar la solicitud actualizada
            serviceRequestRepository.save(solicitudAActualizar);

            redirectAttributes.addFlashAttribute("success", "Solicitud actualizada exitosamente.");
            return "redirect:/jobs/mis-solicitudes";

        } catch (Exception errorActualizacion) {
            model.addAttribute("error", "Error al actualizar: " + errorActualizacion.getMessage());
            ServiceRequest solicitudConError = new ServiceRequest();
            solicitudConError.setTitle(title);
            solicitudConError.setDescription(description);
            anadirDatosFormularioAlModelo(model, usuarioLogueado, solicitudConError, true, id);
            return "jobs/crear";
        }
    }

    // Eliminar una solicitud
    @PostMapping("/eliminar/{id}")
    public String eliminarSolicitud(@PathVariable Long id,
            Authentication auth,
            RedirectAttributes redirectAttributes) {

        AppUser usuarioLogueado = obtenerUsuarioAutenticado(auth);
        try {
            // Buscar la solicitud del usuario
            List<ServiceRequest> solicitudes = serviceRequestRepository.findByIdAndClient(id, usuarioLogueado);
            if (solicitudes.isEmpty()) {
                throw new IllegalArgumentException("Solicitud no encontrada");
            }
            ServiceRequest solicitudAEliminar = solicitudes.get(0);

            // Verificar que se puede eliminar
            boolean enProgreso = solicitudAEliminar.getStatus() == ServiceRequest.Status.IN_PROGRESS;
            boolean completada = solicitudAEliminar.getStatus() == ServiceRequest.Status.COMPLETED;

            if (enProgreso || completada) {
                throw new IllegalStateException("No se pueden eliminar solicitudes en progreso o completadas");
            }

            // Eliminar la solicitud
            serviceRequestRepository.delete(solicitudAEliminar);

            redirectAttributes.addFlashAttribute("success", "Solicitud eliminada exitosamente");
            return "redirect:/jobs/mis-solicitudes";
        } catch (Exception errorEliminacion) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar: " + errorEliminacion.getMessage());
            return "redirect:/jobs/mis-solicitudes";
        }
    }

    // Cancelar una solicitud publicada
    @PostMapping("/cancelar/{id}")
    public String cancelarSolicitud(@PathVariable Long id,
            Authentication auth,
            RedirectAttributes redirectAttributes) {

        AppUser usuarioLogueado = obtenerUsuarioAutenticado(auth);
        try {
            // Usar el servicio para cancelar la solicitud
            String mensajeResultado = serviceRequestService.cancelarSolicitud(id, usuarioLogueado.getUsername());

            redirectAttributes.addFlashAttribute("success", mensajeResultado);
            return "redirect:/jobs/mis-solicitudes";
        } catch (Exception errorCancelacion) {
            redirectAttributes.addFlashAttribute("error", "Error al cancelar: " + errorCancelacion.getMessage());
            return "redirect:/jobs/mis-solicitudes";
        }
    }
}