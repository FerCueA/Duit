package es.duit.app.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;

import es.duit.app.dto.RequestDTO;
import es.duit.app.entity.AppUser;
import es.duit.app.entity.JobApplication;
import es.duit.app.entity.ServiceRequest;
import es.duit.app.repository.AppUserRepository;
import es.duit.app.repository.ServiceRequestRepository;

// ============================================================================
// MOSTRAR SOLICITUDES DEL USUARIO - GESTIONA LAS SOLICITUDES CREADAS POR EL USUARIO
// ============================================================================
@Service
@Transactional
@RequiredArgsConstructor
public class MyRequestsService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final AppUserRepository appUserRepository;
    private final JobService jobService;

    // ============================================================================
    // PUBLICAR UNA SOLICITUD
    // ============================================================================
    public ServiceRequest publishRequest(Long requestId) {

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
            throw new RuntimeException("No tienes permisos para publicar esta solicitud");
        }

        // Verificar que esté en borrador
        if (request.getStatus() != ServiceRequest.Status.DRAFT) {
            throw new RuntimeException("Solo se pueden publicar solicitudes en borrador");
        }

        // Cambiar estado a publicada
        request.setStatus(ServiceRequest.Status.PUBLISHED);

        // Guardar cambios
        ServiceRequest savedRequest = serviceRequestRepository.save(request);

        return savedRequest;
    }

    // ============================================================================
    // ACEPTAR UNA APLICACIÓN/PROPUESTA ESPECÍFICA
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

    // ============================================================================
    // CANCELAR UNA SOLICITUD
    // ============================================================================
    public ServiceRequest cancelRequest(Long requestId) {

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
            throw new RuntimeException("No tienes permisos para cancelar esta solicitud");
        }

        // Cambiar estado a cancelada
        request.setStatus(ServiceRequest.Status.CANCELLED);

        // Guardar cambios
        ServiceRequest savedRequest = serviceRequestRepository.save(request);

        return savedRequest;
    }

    // ============================================================================
    // COMPLETAR UNA SOLICITUD
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
    // ACTUALIZAR/EDITAR UNA SOLICITUD
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
    // ELIMINAR UNA SOLICITUD
    // ============================================================================
    public void deleteRequest(Long requestId) {

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
            throw new RuntimeException("No tienes permisos para eliminar esta solicitud");
        }

        // Solo se pueden eliminar solicitudes en borrador o canceladas
        if (request.getStatus() != ServiceRequest.Status.DRAFT &&
                request.getStatus() != ServiceRequest.Status.CANCELLED) {
            throw new RuntimeException("Solo se pueden eliminar solicitudes en borrador o canceladas");
        }

        // Eliminar la solicitud
        serviceRequestRepository.delete(request);
    }

    // ============================================================================
    // MÉTODO PARA OBTENER LAS SOLICITUDES
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
    // OBTENER UNA SOLICITUD
    // ============================================================================
    @Transactional(readOnly = true)
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
    // OBTENER POSTULACIONES RECIBIDAS EN UNA SOLICITUD
    // ============================================================================
    @Transactional(readOnly = true)
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
        applications.size(); // Trigger lazy loading

        // Forzar la carga de los profesionales relacionados
        for (JobApplication application : applications) {
            if (application.getProfessional() != null) {
                application.getProfessional().getUser().getFirstName(); // Trigger lazy loading
            }
        }

        return applications;
    }

    // ============================================================================
    // MÉTODO OBTENER EL USUARIO AUTENTICADO
    // ============================================================================
    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Usuario no autenticado");
        }

        return authentication.getName();
    }
}