package es.duit.app.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.duit.app.entity.AppUser;
import es.duit.app.entity.JobApplication;
import es.duit.app.entity.ServiceJob;
import es.duit.app.entity.ServiceRequest;
import es.duit.app.repository.JobApplicationRepository;
import es.duit.app.repository.ServiceJobRepository;
import es.duit.app.repository.ServiceRequestRepository;
import lombok.RequiredArgsConstructor;

// ============================================================================
// SERVICIO DE DECISIONES DE POSTULACIONES
// ==========================================================================
@Service
@Transactional
@RequiredArgsConstructor
public class ApplicationDecisionService {

    private final ServiceJobRepository serviceJobRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final ServiceRequestRepository serviceRequestRepository;

    public ServiceJob acceptApplication(Long applicationId, AppUser usuario) {
        JobApplication postulacion = getApplicationById(applicationId);
        ServiceRequest solicitud = postulacion.getRequest();

        validateUserIsClient(solicitud, usuario);
        validateRequestIsPublished(solicitud);
        validateApplicationIsPending(postulacion);

        markApplicationAsAccepted(postulacion);
        rejectOtherApplications(solicitud, applicationId);

        ServiceJob nuevoTrabajo = createJobFromApplication(postulacion, solicitud);
        updateRequestStatus(solicitud, ServiceRequest.Status.IN_PROGRESS);

        return nuevoTrabajo;
    }

    public void rejectApplication(Long applicationId, AppUser usuario) {
        JobApplication postulacion = getApplicationById(applicationId);
        ServiceRequest solicitud = postulacion.getRequest();

        validateUserIsClient(solicitud, usuario);
        validateApplicationIsPending(postulacion);

        markApplicationAsRejected(postulacion);
    }

    private void validateUserIsClient(ServiceRequest solicitud, AppUser usuario) {
        if (!solicitud.getClient().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("No tienes permiso para esta acción en esta solicitud");
        }
    }

    private void validateApplicationIsPending(JobApplication postulacion) {
        if (postulacion.getStatus() != JobApplication.Status.PENDING) {
            throw new IllegalArgumentException("La postulación no está en estado pendiente");
        }
    }

    private void validateRequestIsPublished(ServiceRequest solicitud) {
        if (solicitud.getStatus() != ServiceRequest.Status.PUBLISHED) {
            throw new IllegalArgumentException("La solicitud debe estar publicada para aceptar una postulación");
        }
    }

    private JobApplication getApplicationById(Long applicationId) {
        if (applicationId == null) {
            throw new IllegalArgumentException("ID de aplicación requerido");
        }
        return jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Postulación no encontrada"));
    }

    private void markApplicationAsAccepted(JobApplication postulacion) {
        postulacion.setStatus(JobApplication.Status.ACCEPTED);
        postulacion.setRespondedAt(LocalDateTime.now());
        jobApplicationRepository.save(postulacion);
    }

    private void markApplicationAsRejected(JobApplication postulacion) {
        postulacion.setStatus(JobApplication.Status.REJECTED);
        postulacion.setRespondedAt(LocalDateTime.now());
        jobApplicationRepository.save(postulacion);
    }

    private void rejectOtherApplications(ServiceRequest solicitud, Long acceptedApplicationId) {
        List<JobApplication> otrasPostulaciones = jobApplicationRepository.findByRequest(solicitud);
        otrasPostulaciones.stream()
                .filter(app -> !app.getId().equals(acceptedApplicationId))
                .filter(app -> app.getStatus() == JobApplication.Status.PENDING)
                .forEach(app -> {
                    app.setStatus(JobApplication.Status.REJECTED);
                    app.setRespondedAt(LocalDateTime.now());
                    jobApplicationRepository.save(app);
                });
    }

    private ServiceJob createJobFromApplication(JobApplication postulacion, ServiceRequest solicitud) {
        ServiceJob nuevoTrabajo = new ServiceJob();
        nuevoTrabajo.setRequest(solicitud);
        nuevoTrabajo.setApplication(postulacion);
        nuevoTrabajo.setAgreedPrice(postulacion.getProposedPrice());
        nuevoTrabajo.setStatus(ServiceJob.Status.CREATED);

        return serviceJobRepository.save(nuevoTrabajo);
    }

    private void updateRequestStatus(ServiceRequest solicitud, ServiceRequest.Status nuevoEstado) {
        solicitud.setStatus(nuevoEstado);
        serviceRequestRepository.save(solicitud);
    }
}
