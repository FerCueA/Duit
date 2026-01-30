package es.duit.app.service;

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

    // Crear o actualizar solicitud
    public ServiceRequest crearOActualizarSolicitud(
            String title,
            String description,
            Long categoryId,
            String deadline,
            String addressOption,
            String address,
            String city,
            String postalCode,
            String province,
            String country,
            Long editId,
            AppUser usuario) {

        ServiceRequest solicitud;

        // Buscar solicitud existente
        if (editId != null) {
            solicitud = serviceRequestRepository.findById(editId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));
            
            // Verificar que el usuario es el propietario
            if (!solicitud.getClient().getId().equals(usuario.getId())) {
                throw new IllegalArgumentException("No tienes permisos para editar esta solicitud");
            }
        } else {
            solicitud = new ServiceRequest();
            solicitud.setClient(usuario);
            solicitud.setStatus(ServiceRequest.Status.DRAFT);
            solicitud.setRequestedAt(LocalDateTime.now());
        }

        // Actualizar datos
        solicitud.setTitle(title);
        solicitud.setDescription(description);
        solicitud.setDeadline(parseFechaHora(deadline));

        // Asignar categoría
        Category categoria = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
        solicitud.setCategory(categoria);

        // Asignar dirección
        if ("habitual".equals(addressOption)) {
            solicitud.setServiceAddress(usuario.getAddress());
        } else if ("new".equals(addressOption)) {
            solicitud.setServiceAddress(crearDireccion(address, city, postalCode, province, country));
        }

        return serviceRequestRepository.save(solicitud);
    }

    // Publicar solicitud
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

    // Parsear fecha
    private LocalDateTime parseFechaHora(String fecha) {
        if (fecha == null || fecha.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(fecha).atStartOfDay();
        } catch (Exception e) {
            return null;
        }
    }

    // Crear dirección
    private Address crearDireccion(String address, String city, String postalCode, 
                                   String province, String country) {
        if (address == null || city == null || postalCode == null || province == null || country == null) {
            return null;
        }
        Address dir = new Address();
        dir.setAddress(address);
        dir.setCity(city);
        dir.setPostalCode(postalCode);
        dir.setProvince(province);
        dir.setCountry(country);
        return dir;
    }
}
