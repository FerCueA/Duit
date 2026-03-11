package es.duit.app.service.validation;

import org.springframework.stereotype.Component;

import es.duit.app.entity.AppUser;
import es.duit.app.entity.ServiceRequest;
import es.duit.app.repository.ServiceRequestRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RequestAccessValidator {

    private final ServiceRequestRepository serviceRequestRepository;

    public ServiceRequest getOwnedRequestOrThrow(Long requestId, AppUser usuario) {
        if (requestId == null) {
            throw new IllegalArgumentException("ID de solicitud requerido");
        }
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario requerido");
        }

        ServiceRequest solicitud = serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        if (!solicitud.getClient().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("No tienes permisos para acceder a esta solicitud");
        }

        return solicitud;
    }

    public ServiceRequest getEditableOwnedRequestOrThrow(Long requestId, AppUser usuario) {
        ServiceRequest solicitud = getOwnedRequestOrThrow(requestId, usuario);

        ServiceRequest.Status estado = solicitud.getStatus();
        boolean esBorrador = estado == ServiceRequest.Status.DRAFT;
        boolean estaCancelada = estado == ServiceRequest.Status.CANCELLED;

        if (!esBorrador && !estaCancelada) {
            throw new IllegalArgumentException(
                    "Solo se pueden editar solicitudes en borrador o canceladas. Estado actual: " + estado);
        }

        return solicitud;
    }
}
