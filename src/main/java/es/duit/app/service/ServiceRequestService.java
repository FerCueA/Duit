package es.duit.app.service;

import es.duit.app.dto.CrearSolicitudDTO;
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

@Service
@Transactional
public class ServiceRequestService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final CategoryRepository categoryRepository;

    public ServiceRequestService(ServiceRequestRepository serviceRequestRepository,
            CategoryRepository categoryRepository) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.categoryRepository = categoryRepository;
    }

    // Crear o actualizar solicitud a partir del formulario
    public ServiceRequest crearOActualizarSolicitud(CrearSolicitudDTO form, AppUser usuario) {

        validarDatosSolicitud(form, usuario);

        ServiceRequest solicitud;

        if (form.isEditing()) {
            solicitud = obtenerSolicitudParaEditar(form.getEditId(), usuario);
        } else {
            solicitud = new ServiceRequest();
            solicitud.setClient(usuario);
            solicitud.setStatus(ServiceRequest.Status.DRAFT);
            solicitud.setRequestedAt(LocalDateTime.now());
        }

        solicitud.setTitle(form.getTitle().trim());
        solicitud.setDescription(form.getDescription().trim());

        Category categoria = buscarCategoria(form.getCategoryId());
        solicitud.setCategory(categoria);

        solicitud.setDeadline(convertirFecha(form.getDeadline()));

        asignarDireccionServicio(solicitud, form, usuario);

        return serviceRequestRepository.save(solicitud);
    }

    // Validar datos del formulario
    private void validarDatosSolicitud(CrearSolicitudDTO form, AppUser usuario) {

        if (form == null) {
            throw new IllegalArgumentException("Faltan datos del formulario");
        }

        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no válido");
        }

        // Validación específica para dirección habitual
        if ("habitual".equals(form.getAddressOption()) && usuario.getAddress() == null) {
            throw new IllegalArgumentException("No tienes dirección habitual. Especifica una dirección nueva");
        }
        
        // Validación específica para nueva dirección
        if ("new".equals(form.getAddressOption()) && !form.isValidNewAddress()) {
            throw new IllegalArgumentException("Todos los campos de dirección son obligatorios");
        }

    }

    // Obtener solicitud para edición
    private ServiceRequest obtenerSolicitudParaEditar(Long solicitudId, AppUser usuario) {
        if (solicitudId == null) {
            throw new IllegalArgumentException("ID de solicitud no válido");
        }

        ServiceRequest solicitud = serviceRequestRepository.findById(solicitudId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        if (!solicitud.getClient().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("No tienes permisos para editar esta solicitud");
        }

        if (solicitud.getStatus() != ServiceRequest.Status.DRAFT &&
                solicitud.getStatus() != ServiceRequest.Status.PUBLISHED) {
            throw new IllegalArgumentException("Esta solicitud no se puede editar en su estado actual");
        }

        if (solicitud.getStatus() == ServiceRequest.Status.PUBLISHED && solicitud.getApplicationCount() > 0) {
            throw new IllegalArgumentException("No se puede editar una solicitud que ya tiene postulaciones");
        }

        return solicitud;
    }

    // Buscar categoría por ID
    private Category buscarCategoria(Long categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("Debes seleccionar una categoría");
        }

        Category categoria = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        if (!categoria.getActive()) {
            throw new IllegalArgumentException("La categoría seleccionada no está disponible");
        }

        return categoria;
    }

    // Convertir LocalDate a LocalDateTime
    private LocalDateTime convertirFecha(LocalDate fecha) {
        if (fecha == null) {
            return null;
        }
        return fecha.atStartOfDay();
    }

    // Asignar dirección del servicio
    private void asignarDireccionServicio(ServiceRequest solicitud, CrearSolicitudDTO form, AppUser usuario) {
        if ("habitual".equals(form.getAddressOption())) {
            solicitud.setServiceAddress(usuario.getAddress());
        } else if ("new".equals(form.getAddressOption())) {
            Address nuevaDireccion = crearNuevaDireccion(form);
            solicitud.setServiceAddress(nuevaDireccion);
        } else {
            throw new IllegalArgumentException("Debes seleccionar una opción de dirección válida");
        }
    }

    // Crear nueva dirección a partir del formulario
    private Address crearNuevaDireccion(CrearSolicitudDTO form) {
        Address direccion = new Address();
        direccion.setAddress(form.getAddress().trim());
        direccion.setCity(form.getCity().trim());
        direccion.setPostalCode(form.getPostalCode().trim());
        direccion.setProvince(form.getProvince().trim());
        direccion.setCountry(form.getCountry().trim());
        return direccion;
    }

    public ServiceRequest publicarSolicitud(Long solicitudId, AppUser usuario) {
        ServiceRequest solicitud = obtenerSolicitudDelUsuario(solicitudId, usuario);

        if (solicitud.getStatus() != ServiceRequest.Status.DRAFT) {
            throw new IllegalArgumentException("Solo se pueden publicar solicitudes en borrador");
        }

        solicitud.setStatus(ServiceRequest.Status.PUBLISHED);
        return serviceRequestRepository.save(solicitud);
    }

    // Despublicar solicitud
    public ServiceRequest despublicarSolicitud(Long solicitudId, AppUser usuario) {
        ServiceRequest solicitud = obtenerSolicitudDelUsuario(solicitudId, usuario);

        if (solicitud.getStatus() != ServiceRequest.Status.PUBLISHED) {
            throw new IllegalArgumentException("Solo se pueden despublicar solicitudes publicadas");
        }

        solicitud.setStatus(ServiceRequest.Status.DRAFT);
        return serviceRequestRepository.save(solicitud);
    }

    // Cancelar solicitud
    public ServiceRequest cancelarSolicitud(Long solicitudId, AppUser usuario) {
        ServiceRequest solicitud = obtenerSolicitudDelUsuario(solicitudId, usuario);

        if (solicitud.getStatus() != ServiceRequest.Status.PUBLISHED) {
            throw new IllegalArgumentException("Solo se pueden cancelar solicitudes publicadas");
        }

        solicitud.setStatus(ServiceRequest.Status.CANCELLED);
        return serviceRequestRepository.save(solicitud);
    }

    // Reactivar solicitud cancelada
    public ServiceRequest reactivarSolicitud(Long solicitudId, AppUser usuario) {
        ServiceRequest solicitud = obtenerSolicitudDelUsuario(solicitudId, usuario);

        if (solicitud.getStatus() != ServiceRequest.Status.CANCELLED) {
            throw new IllegalArgumentException("Solo se pueden reactivar solicitudes canceladas");
        }

        solicitud.setStatus(ServiceRequest.Status.DRAFT);
        return serviceRequestRepository.save(solicitud);
    }

    // Eliminar solicitud
    public void eliminarSolicitud(Long solicitudId, AppUser usuario) {
        ServiceRequest solicitud = obtenerSolicitudDelUsuario(solicitudId, usuario);
        serviceRequestRepository.delete(solicitud);
    }

    // Obtener solicitudes del usuario
    public List<ServiceRequest> obtenerSolicitudesDelUsuario(AppUser usuario) {
        return serviceRequestRepository.findByClient(usuario);
    }

    // Obtener una solicitud del usuario
    public ServiceRequest obtenerSolicitudDelUsuario(Long solicitudId, AppUser usuario) {
        ServiceRequest solicitud = serviceRequestRepository.findById(solicitudId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        if (!solicitud.getClient().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("No tienes permisos para acceder a esta solicitud");
        }

        return solicitud;
    }

    // Obtener categorías activas
    public List<Category> obtenerCategoriasActivas() {
        return categoryRepository.findByActiveTrue();
    }

}
