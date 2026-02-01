package es.duit.app.service;

import es.duit.app.dto.RequestDTO;
import es.duit.app.entity.Address;
import es.duit.app.entity.AppUser;
import es.duit.app.entity.Category;
import es.duit.app.entity.ServiceRequest;
import es.duit.app.repository.CategoryRepository;
import es.duit.app.repository.ServiceRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// ============================================================================
// SERVICIO DE SOLICITUDES - GESTIONA SOLICITUDES DE SERVICIOS
// ============================================================================
@Service
@Transactional
public class RequestService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final CategoryRepository categoryRepository;

    public RequestService(ServiceRequestRepository serviceRequestRepository,
            CategoryRepository categoryRepository) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.categoryRepository = categoryRepository;
    }

    // ============================================================================
    // GUARDA UNA SOLICITUD - CREAR O ACTUALIZAR EXISTENTE
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
            solicitud.setStatus(ServiceRequest.Status.DRAFT);
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

        // Guardar en base de datos y devolver
        ServiceRequest saved = serviceRequestRepository.save(solicitud);

        return saved;
    }

    // ============================================================================
    // BUSCA UNA CATEGORÍA POR ID CON VALIDACIONES
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
    // ASIGNA DIRECCIÓN DEL SERVICIO - HABITUAL O NUEVA
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
    // OBTIENE SOLICITUD PARA EDITAR CON VALIDACIONES
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

        // Verificar que el estado permita edición
        ServiceRequest.Status estado = solicitud.getStatus();
        boolean esBorrador = estado == ServiceRequest.Status.DRAFT;
        boolean estaPublicada = estado == ServiceRequest.Status.PUBLISHED;

        if (!esBorrador && !estaPublicada) {
            throw new IllegalArgumentException("Esta solicitud no se puede editar en su estado actual");
        }

        // Si está publicada, verificar que no tenga postulaciones
        if (estaPublicada) {
            int numeroPostulaciones = solicitud.getApplicationCount();
            if (numeroPostulaciones > 0) {
                throw new IllegalArgumentException("No se puede editar una solicitud que ya tiene postulaciones");
            }
        }

        return solicitud;
    }

    // ============================================================================
    // DESPUBLICA UNA SOLICITUD PUBLICADA
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

    // ============================================================================
    // CANCELA UNA SOLICITUD PUBLICADA
    // ============================================================================
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

    // ============================================================================
    // REACTIVA UNA SOLICITUD CANCELADA
    // ============================================================================
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

    // ============================================================================
    // PUBLICA UNA SOLICITUD EN BORRADOR
    // ============================================================================
    public ServiceRequest publishRequest(Long solicitudId, AppUser usuario) {
        // Obtener la solicitud del usuario con validaciones
        ServiceRequest solicitud = getUserRequest(solicitudId, usuario);

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
    // ELIMINA UNA SOLICITUD DEL USUARIO
    // ============================================================================
    public void deleteRequest(Long solicitudId, AppUser usuario) {
        // Obtener la solicitud del usuario con validaciones
        ServiceRequest solicitud = getUserRequest(solicitudId, usuario);

        // Eliminar de la base de datos
        serviceRequestRepository.delete(solicitud);
    }

    // ============================================================================
    // OBTIENE TODAS LAS SOLICITUDES DEL USUARIO AUTENTICADO
    // ============================================================================
    public List<ServiceRequest> getUserRequests(AppUser usuario) {
        // Buscar todas las solicitudes del usuario en la base de datos
        List<ServiceRequest> solicitudes = serviceRequestRepository.findByClient(usuario);

        return solicitudes;
    }

    // ============================================================================
    // OBTIENE UNA SOLICITUD DEL USUARIO CON VALIDACIONES
    // ============================================================================
    public ServiceRequest getUserRequest(Long solicitudId, AppUser usuario) {
        // Buscar la solicitud en la base de datos
        ServiceRequest solicitud = null;
        if (serviceRequestRepository.findById(solicitudId).isEmpty()) {
            throw new IllegalArgumentException("Solicitud no encontrada");
        }
        solicitud = serviceRequestRepository.findById(solicitudId).get();

        // Verificar que el usuario sea el dueño
        Long clienteId = solicitud.getClient().getId();
        Long usuarioId = usuario.getId();
        if (!clienteId.equals(usuarioId)) {
            throw new IllegalArgumentException("No tienes permisos para acceder a esta solicitud");
        }

        return solicitud;
    }

    // ============================================================================
    // CREA UNA NUEVA DIRECCIÓN DESDE FORMULARIO
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
    // OBTIENE TODAS LAS CATEGORÍAS ACTIVAS
    // ============================================================================
    public List<Category> getActiveCategories() {
        // Buscar todas las categorías que están activas
        List<Category> categoriasActivas = categoryRepository.findByActiveTrue();

        return categoriasActivas;
    }

}
