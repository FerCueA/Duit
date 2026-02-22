package es.duit.app.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import es.duit.app.dto.RequestDTO;
import es.duit.app.entity.Address;
import es.duit.app.entity.AppUser;
import es.duit.app.entity.Category;
import es.duit.app.entity.JobApplication;
import es.duit.app.entity.ServiceRequest;
import es.duit.app.repository.AppUserRepository;
import es.duit.app.repository.CategoryRepository;
import es.duit.app.repository.ServiceRequestRepository;
import lombok.RequiredArgsConstructor;

// ============================================================================
// SERVICIO DE SOLICITUDES - GESTIONA SOLICITUDES DE SERVICIOS
// ============================================================================
@Service
@RequiredArgsConstructor
public class RequestService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final CategoryRepository categoryRepository;
    private final AppUserRepository appUserRepository;
    private final JobService jobService;

    // ============================================================================
    // FORMULARIO
    // ============================================================================
    public ServiceRequest saveRequest(RequestDTO form, AppUser usuario) {
        // Validar que existan los datos básicos
        if (form == null)
            throw new IllegalArgumentException("Faltan datos del formulario");
        if (usuario == null)
            throw new IllegalArgumentException("Usuario no válido");

        // Validar dirección habitual si se selecciona
        if ("habitual".equals(form.getAddressOption()) && usuario.getAddress() == null) {
            throw new IllegalArgumentException("No tienes dirección habitual. Especifica una dirección nueva");
        }

        // Validar nueva dirección si se selecciona la opción
        if ("new".equals(form.getAddressOption())) {
            if (form.getAddress() == null || form.getAddress().trim().isEmpty() ||
                    form.getCity() == null || form.getCity().trim().isEmpty() ||
                    form.getPostalCode() == null || form.getPostalCode().trim().isEmpty() ||
                    form.getProvince() == null || form.getProvince().trim().isEmpty() ||
                    form.getCountry() == null || form.getCountry().trim().isEmpty()) {
                throw new IllegalArgumentException("Todos los campos de dirección son obligatorios");
            }
        }

        // Determinar si es nueva solicitud o edición
        ServiceRequest solicitud;
        Long editId = form.getEditId();

        if (editId != null) {
            // Es edición: obtener solicitud existente con validaciones
            solicitud = getRequestForEditing(editId, usuario);
        } else {
            // Es nueva: crear solicitud vacía
            solicitud = new ServiceRequest();
            solicitud.setClient(usuario);
            boolean publishNow = Boolean.TRUE.equals(form.getPublishNow());
            solicitud.setStatus(publishNow ? ServiceRequest.Status.PUBLISHED : ServiceRequest.Status.DRAFT);
            solicitud.setRequestedAt(LocalDateTime.now());
        }

        // Copiar datos básicos del formulario
        String title = form.getTitle().trim();
        String description = form.getDescription().trim();
        solicitud.setTitle(title);
        solicitud.setDescription(description);

        // Buscar y asignar categoría
        Long categoryId = form.getCategoryId();
        Category categoria = findCategoryById(categoryId);
        solicitud.setCategory(categoria);

        // Convertir y asignar fecha límite para la solicitud
        LocalDate deadline = form.getDeadline();
        LocalDateTime deadlineDateTime = null;
        if (deadline != null) {
            deadlineDateTime = deadline.atStartOfDay();
        }
        solicitud.setDeadline(deadlineDateTime);

        // Asignar dirección del servicio
        assignServiceAddress(solicitud, form, usuario);

        if (editId != null) {
            boolean publishNow = Boolean.TRUE.equals(form.getPublishNow());
            if (publishNow && solicitud.getStatus() == ServiceRequest.Status.DRAFT) {
                solicitud.setStatus(ServiceRequest.Status.PUBLISHED);
            }
        }

        // Guardar en base de datos y devolver
        ServiceRequest saved = serviceRequestRepository.save(solicitud);

        return saved;
    }

    // ============================================================================
    // CATEGORIAS Y DIRECCIONES
    // ============================================================================

    public List<Category> getActiveCategories() {
        // Buscar todas las categorías que están activas
        List<Category> categoriasActivas = categoryRepository.findByActiveTrue();

        return categoriasActivas;
    }

    public ServiceRequest getUserRequestForEditing(Long solicitudId, AppUser usuario) {
        return getRequestForEditing(solicitudId, usuario);
    }

    // ============================================================================
    // ESTADOS DE SOLICITUD -
    // ============================================================================
    public ServiceRequest publishRequest(Long solicitudId, AppUser usuario) {
        // Obtener la solicitud del usuario con validaciones
        ServiceRequest solicitud = getUserRequest(solicitudId, usuario);

        validateUserHasAddress(usuario);

        // Verificar que esté en borrador
        ServiceRequest.Status estado = solicitud.getStatus();
        boolean esBorrador = estado == ServiceRequest.Status.DRAFT;
        if (!esBorrador) {
            throw new IllegalArgumentException("Solo se pueden publicar solicitudes en borrador");
        }

        // Cambiar estado a publicada
        solicitud.setStatus(ServiceRequest.Status.PUBLISHED);
        ServiceRequest guardada = serviceRequestRepository.save(solicitud);

        return guardada;
    }

    // =========================================================================
    // VALIDA QUE EL USUARIO TIENE UNA DIRECCION CONFIGURADA
    // =========================================================================
    private void validateUserHasAddress(AppUser usuario) {
        if (usuario.getAddress() == null) {
            throw new IllegalArgumentException("Necesitas configurar tu direccion antes de publicar");
        }
    }

    // ============================================================================
    // despublicar, cancelar, reactivar y eliminar solicitudes (USUARIO AUTENTICADO)
    // ============================================================================

    public ServiceRequest unpublishRequest(Long solicitudId, AppUser usuario) {
        // Obtener la solicitud del usuario con validaciones
        ServiceRequest solicitud = getUserRequest(solicitudId, usuario);

        // Verificar que esté publicada
        ServiceRequest.Status estado = solicitud.getStatus();
        boolean estaPublicada = estado == ServiceRequest.Status.PUBLISHED;
        if (!estaPublicada) {
            throw new IllegalArgumentException("Solo se pueden despublicar solicitudes publicadas");
        }

        // Cambiar estado a borrador
        solicitud.setStatus(ServiceRequest.Status.DRAFT);
        ServiceRequest guardada = serviceRequestRepository.save(solicitud);

        return guardada;
    }

    public ServiceRequest cancelRequest(Long solicitudId, AppUser usuario) {
        // Obtener la solicitud del usuario con validaciones
        ServiceRequest solicitud = getUserRequest(solicitudId, usuario);

        // Verificar que esté publicada
        ServiceRequest.Status estado = solicitud.getStatus();
        boolean estaPublicada = estado == ServiceRequest.Status.PUBLISHED;
        if (!estaPublicada) {
            throw new IllegalArgumentException("Solo se pueden cancelar solicitudes publicadas");
        }

        // Cambiar estado a cancelada
        solicitud.setStatus(ServiceRequest.Status.CANCELLED);
        ServiceRequest guardada = serviceRequestRepository.save(solicitud);

        return guardada;
    }

    public ServiceRequest reactivateRequest(Long solicitudId, AppUser usuario) {
        // Obtener la solicitud del usuario con validaciones
        ServiceRequest solicitud = getUserRequest(solicitudId, usuario);

        // Verificar que esté cancelada
        ServiceRequest.Status estado = solicitud.getStatus();
        boolean estaCancelada = estado == ServiceRequest.Status.CANCELLED;
        if (!estaCancelada) {
            throw new IllegalArgumentException("Solo se pueden reactivar solicitudes canceladas");
        }

        // Cambiar estado a borrador
        solicitud.setStatus(ServiceRequest.Status.DRAFT);
        ServiceRequest guardada = serviceRequestRepository.save(solicitud);

        return guardada;
    }

    public void deleteRequest(Long solicitudId, AppUser usuario) {
        // Obtener la solicitud del usuario con validaciones
        ServiceRequest solicitud = getUserRequest(solicitudId, usuario);

        // Eliminar de la base de datos
        if (solicitud != null) {
            serviceRequestRepository.delete(solicitud);
        }
    }

    // ============================================================================
    // ESTADOS DE SOLICITUD - COMPLETAR Y EDITAR (USUARIO AUTENTICADO)
    // ============================================================================
    public ServiceRequest completeRequest(Long requestId) {

        if (requestId == null) {
            throw new IllegalArgumentException("El ID de la solicitud es requerido");
        }

        // Obtener el usuario autenticado
        String username = getAuthenticatedUsername();

        // Buscar la solicitud
        ServiceRequest request = serviceRequestRepository.findById(requestId).orElse(null);

        if (request == null) {
            throw new RuntimeException("Solicitud no encontrada");
        }

        // Verificar que sea del cliente correcto
        if (!request.getClient().getUsername().equals(username)) {
            throw new RuntimeException("No tienes permisos para completar esta solicitud");
        }

        // Verificar que esté en progreso
        if (request.getStatus() != ServiceRequest.Status.IN_PROGRESS) {
            throw new RuntimeException("Solo se pueden completar solicitudes en progreso");
        }

        // Cambiar estado a completada
        request.setStatus(ServiceRequest.Status.COMPLETED);

        // Guardar cambios
        ServiceRequest savedRequest = serviceRequestRepository.save(request);

        return savedRequest;
    }

    // ============================================================================
    // ACTUALIZA LOS DATOS DE UNA SOLICITUD (SOLO SI ESTÁ EN BORRADOR O CANCELADA)
    // ============================================================================
    public ServiceRequest updateRequest(Long requestId, RequestDTO updateData) {

        if (requestId == null) {
            throw new IllegalArgumentException("El ID de la solicitud es requerido");
        }
        if (updateData == null) {
            throw new IllegalArgumentException("Los datos de actualización son requeridos");
        }

        // Obtener el usuario autenticado
        String username = getAuthenticatedUsername();

        // Buscar la solicitud
        ServiceRequest request = serviceRequestRepository.findById(requestId).orElse(null);

        if (request == null) {
            throw new RuntimeException("Solicitud no encontrada");
        }

        // Verificar que sea del cliente correcto
        if (!request.getClient().getUsername().equals(username)) {
            throw new RuntimeException("No tienes permisos para editar esta solicitud");
        }

        // Solo se pueden editar solicitudes en borrador o canceladas
        if (request.getStatus() != ServiceRequest.Status.DRAFT &&
                request.getStatus() != ServiceRequest.Status.CANCELLED) {
            System.out.println("Estado actual de la solicitud: " + request.getStatus());
            throw new RuntimeException("Solo se pueden editar solicitudes en borrador o canceladas. Estado actual: "
                    + request.getStatus());
        }

        // Actualizar solo los campos que vengan en el DTO
        if (updateData.getTitle() != null) {
            request.setTitle(updateData.getTitle());
        }
        if (updateData.getDescription() != null) {
            request.setDescription(updateData.getDescription());
        }

        // Guardar cambios
        return serviceRequestRepository.save(request);
    }

    // ============================================================================
    // VER SOLICITUDES DEL (USUARIO AUTENTICADO)
    // ============================================================================
    public List<ServiceRequest> getMyRequests() {
        // Obtener el usuario autenticado
        String username = getAuthenticatedUsername();

        // Buscar todas las solicitudes del usuario en la base de datos
        List<ServiceRequest> userRequests = serviceRequestRepository.findByClientUsername(username);

        // Verificar si el usuario tiene solicitudes
        if (userRequests == null) {
            userRequests = new ArrayList<>();
        }

        // Retornar la lista de solicitudes del usuario
        return userRequests;
    }

    // ============================================================================
    // OBTENER UNA SOLICITUD POR ID (SOLO SI PERTENECE AL USUARIO AUTENTICADO)
    // ============================================================================
    public ServiceRequest getMyRequestById(Long requestId) {

        if (requestId == null) {
            throw new IllegalArgumentException("El ID de la solicitud es requerido");
        }

        // Obtener el usuario autenticado
        String username = getAuthenticatedUsername();

        // Buscar la solicitud
        ServiceRequest request = serviceRequestRepository.findById(requestId).orElse(null);

        if (request == null) {
            throw new RuntimeException("Solicitud no encontrada");
        }

        // Verificar que sea del cliente correcto
        if (!request.getClient().getUsername().equals(username)) {
            throw new RuntimeException("No tienes permisos para ver esta solicitud");
        }

        return request;
    }

    // ============================================================================
    // OBTENER HISTORIAL DE SOLICITUDES Y TRABAJOS (USUARIO AUTENTICADO)
    // ============================================================================

    public List<JobApplication> getApplicationsForMyRequest(Long requestId) {

        if (requestId == null) {
            throw new IllegalArgumentException("El ID de la solicitud es requerido");
        }

        // Obtener el usuario autenticado
        String username = getAuthenticatedUsername();

        // Buscar la solicitud
        ServiceRequest request = serviceRequestRepository.findById(requestId).orElse(null);

        if (request == null) {
            throw new RuntimeException("Solicitud no encontrada");
        }

        // Verificar que sea del cliente correcto
        if (!request.getClient().getUsername().equals(username)) {
            throw new RuntimeException("No tienes permisos para ver las aplicaciones de esta solicitud");
        }

        // Forzar la carga de las aplicaciones y sus relaciones
        List<JobApplication> applications = request.getApplications();
        applications.size();

        // Forzar la carga de los profesionales relacionados
        for (JobApplication application : applications) {
            if (application.getProfessional() != null) {
                application.getProfessional().getUser().getFirstName();
            }
        }

        return applications;
    }

    // ============================================================================
    // ACCIONES - APLICACIONES (USUARIO AUTENTICADO)
    // ============================================================================
    public ServiceRequest acceptRequest(Long requestId, Long applicationId) {

        if (requestId == null) {
            throw new IllegalArgumentException("El ID de la solicitud es requerido");
        }
        if (applicationId == null) {
            throw new IllegalArgumentException("El ID de la aplicación es requerido");
        }

        // Obtener el usuario autenticado
        String username = getAuthenticatedUsername();

        AppUser usuario = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        ServiceRequest request = serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        boolean perteneceALaSolicitud = false;
        for (JobApplication app : request.getApplications()) {
            if (app.getId().equals(applicationId)) {
                perteneceALaSolicitud = true;
                break;
            }
        }

        if (!perteneceALaSolicitud) {
            throw new RuntimeException("Aplicación no encontrada en esta solicitud");
        }

        // Delegar al servicio de trabajos para crear el job
        jobService.acceptApplication(applicationId, usuario);

        return request;
    }

    public ServiceRequest rejectRequest(Long requestId, Long applicationId) {

        if (requestId == null) {
            throw new IllegalArgumentException("El ID de la solicitud es requerido");
        }
        if (applicationId == null) {
            throw new IllegalArgumentException("El ID de la aplicación es requerido");
        }

        String username = getAuthenticatedUsername();

        AppUser usuario = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        ServiceRequest request = serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        boolean perteneceALaSolicitud = false;
        for (JobApplication app : request.getApplications()) {
            if (app.getId().equals(applicationId)) {
                perteneceALaSolicitud = true;
                break;
            }
        }

        if (!perteneceALaSolicitud) {
            throw new RuntimeException("Aplicación no encontrada en esta solicitud");
        }

        jobService.rejectApplication(applicationId, usuario);

        return request;
    }

    // ============================================================================
    // VALIDACION de que la solicitud pertenece al usuario autenticado y que existe
    // ============================================================================
    private ServiceRequest getUserRequest(Long solicitudId, AppUser usuario) {
        // Validar parámetros
        if (solicitudId == null) {
            throw new IllegalArgumentException("ID de solicitud requerido");
        }
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario requerido");
        }

        // Buscar la solicitud en la base de datos
        ServiceRequest solicitud = serviceRequestRepository.findById(solicitudId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        // Verificar que el usuario sea el dueño
        Long clienteId = solicitud.getClient().getId();
        Long usuarioId = usuario.getId();
        if (!clienteId.equals(usuarioId)) {
            throw new IllegalArgumentException("No tienes permisos para acceder a esta solicitud");
        }

        return solicitud;
    }

    // ============================================================================
    // VALIDACION DE QUE LA SOLICITUD PERTENECE AL USUARIO AUTENTICADO Y QUE EXISTE
    // (SOBRECARGA PARA ID DE SOLICITUD)
    // ============================================================================

    private ServiceRequest getRequestForEditing(Long solicitudId, AppUser usuario) {
        // Validar que el ID no sea nulo
        if (solicitudId == null) {
            throw new IllegalArgumentException("ID de solicitud no válido");
        }

        // Buscar la solicitud en la base de datos
        ServiceRequest solicitud = null;
        if (serviceRequestRepository.findById(solicitudId).isEmpty()) {
            throw new IllegalArgumentException("Solicitud no encontrada");
        }
        solicitud = serviceRequestRepository.findById(solicitudId).get();

        // Verificar que el usuario sea el dueño de la solicitud
        Long clienteId = solicitud.getClient().getId();
        Long usuarioId = usuario.getId();
        if (!clienteId.equals(usuarioId)) {
            throw new IllegalArgumentException("No tienes permisos para editar esta solicitud");
        }

        // Verificar que el estado permita edición (solo DRAFT y CANCELLED)
        ServiceRequest.Status estado = solicitud.getStatus();
        boolean esBorrador = estado == ServiceRequest.Status.DRAFT;
        boolean estaCancelada = estado == ServiceRequest.Status.CANCELLED;

        if (!esBorrador && !estaCancelada) {
            throw new IllegalArgumentException(
                    "Solo se pueden editar solicitudes en borrador o canceladas. Estado actual: " + estado);
        }

        return solicitud;
    }

    // ============================================================================
    // BUSCAR CATEGORÍA POR ID CON VALIDACIONES DE EXISTENCIA Y ACTIVIDAD
    // ============================================================================
    private Category findCategoryById(Long categoryId) {
        // Validar que el ID no sea nulo
        if (categoryId == null) {
            throw new IllegalArgumentException("Debes seleccionar una categoría");
        }

        // Buscar la categoría en la base de datos
        Category categoria = null;
        if (categoryRepository.findById(categoryId).isEmpty()) {
            throw new IllegalArgumentException("Categoría no encontrada");
        }
        categoria = categoryRepository.findById(categoryId).get();

        // Verificar que la categoría esté activa
        boolean estaActiva = categoria.getActive();
        if (!estaActiva) {
            throw new IllegalArgumentException("La categoría seleccionada no está disponible");
        }

        return categoria;
    }

    // ============================================================================
    // ASIGNAR DIRECCIÓN DE SERVICIO SEGÚN OPCIÓN SELECCIONADA EN EL FORMULARIO
    // (DIRECCIÓN HABITUAL O NUEVA)
    // ============================================================================

    private void assignServiceAddress(ServiceRequest solicitud, RequestDTO form, AppUser usuario) {
        // Obtener la opción de dirección seleccionada
        String opcionDireccion = form.getAddressOption();

        // Verificar si es dirección habitual del usuario
        boolean esHabitual = "habitual".equals(opcionDireccion);
        if (esHabitual) {
            Address direccionUsuario = usuario.getAddress();
            solicitud.setServiceAddress(direccionUsuario);
            return;
        }

        // Verificar si es nueva dirección
        boolean esNueva = "new".equals(opcionDireccion);
        if (esNueva) {
            Address nuevaDireccion = buildAddress(form);
            solicitud.setServiceAddress(nuevaDireccion);
            return;
        }

        // Si no es ninguna opción válida, lanzar error
        throw new IllegalArgumentException("Debes seleccionar una opción de dirección válida");
    }

    // ============================================================================
    // CONSTRUYE UN OBJETO DE DIRECCIÓN A PARTIR DE LOS CAMPOS DEL FORMULARIO
    // ============================================================================
    private Address buildAddress(RequestDTO form) {
        // Crear objeto de dirección vacío
        Address direccion = new Address();

        // Asignar cada campo limpiando espacios en blanco
        String calle = form.getAddress().trim();
        String ciudad = form.getCity().trim();
        String codigoPostal = form.getPostalCode().trim();
        String provincia = form.getProvince().trim();
        String pais = form.getCountry().trim();

        direccion.setAddress(calle);
        direccion.setCity(ciudad);
        direccion.setPostalCode(codigoPostal);
        direccion.setProvince(provincia);
        direccion.setCountry(pais);

        return direccion;
    }

    // ============================================================================
    // UTILIDAD PARA OBTENER EL USUARIO AUTENTICADO
    // ============================================================================
    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Usuario no autenticado");
        }

        return authentication.getName();
    }
}
