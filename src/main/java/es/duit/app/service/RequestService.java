package es.duit.app.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import es.duit.app.dto.RequestDTO;
import es.duit.app.entity.Address;
import es.duit.app.entity.AppUser;
import es.duit.app.entity.Category;
import es.duit.app.entity.ServiceRequest;
import es.duit.app.repository.CategoryRepository;
import es.duit.app.repository.ServiceRequestRepository;
import es.duit.app.service.validation.RequestAccessValidator;
import es.duit.app.service.validation.UserPreconditionsValidator;
import lombok.RequiredArgsConstructor;

// ============================================================================
// SERVICIO DE SOLICITUDES - GESTIONA SOLICITUDES DE SERVICIOS
// ============================================================================
@Service
@RequiredArgsConstructor
public class RequestService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final CategoryRepository categoryRepository;
    private final RequestAccessValidator requestAccessValidator;
    private final UserPreconditionsValidator userPreconditionsValidator;

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
            solicitud = requestAccessValidator.getEditableOwnedRequestOrThrow(editId, usuario);
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

    public ServiceRequest getUserRequestForEditing(Long solicitudId, AppUser usuario) {
        return requestAccessValidator.getEditableOwnedRequestOrThrow(solicitudId, usuario);
    }

    // ============================================================================
    // ESTADOS DE SOLICITUD -
    // ============================================================================
    public ServiceRequest publishRequest(Long solicitudId, AppUser usuario) {
        // Obtener la solicitud del usuario con validaciones
        ServiceRequest solicitud = requestAccessValidator.getOwnedRequestOrThrow(solicitudId, usuario);

        userPreconditionsValidator.requireAddress(usuario);

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

    // ============================================================================
    // despublicar, cancelar, reactivar y eliminar solicitudes (USUARIO AUTENTICADO)
    // ============================================================================

    public ServiceRequest unpublishRequest(Long solicitudId, AppUser usuario) {
        // Obtener la solicitud del usuario con validaciones
        ServiceRequest solicitud = requestAccessValidator.getOwnedRequestOrThrow(solicitudId, usuario);

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
        ServiceRequest solicitud = requestAccessValidator.getOwnedRequestOrThrow(solicitudId, usuario);

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
        ServiceRequest solicitud = requestAccessValidator.getOwnedRequestOrThrow(solicitudId, usuario);

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
        ServiceRequest solicitud = requestAccessValidator.getOwnedRequestOrThrow(solicitudId, usuario);

        // Eliminar de la base de datos
        serviceRequestRepository.delete(Objects.requireNonNull(solicitud));
    }

    public List<ServiceRequest> getMyRequests(AppUser usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario requerido");
        }

        List<ServiceRequest> userRequests = serviceRequestRepository.findByClient(usuario);
        if (userRequests == null) {
            return new ArrayList<>();
        }

        return userRequests;
    }

    public ServiceRequest getMyRequestById(Long requestId, AppUser usuario) {
        return requestAccessValidator.getOwnedRequestOrThrow(requestId, usuario);
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
        Category categoria = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

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

}
