package es.duit.app.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;

import es.duit.app.entity.AppUser;
import es.duit.app.entity.JobApplication;
import es.duit.app.entity.ServiceJob;
import es.duit.app.entity.ServiceRequest;
import es.duit.app.repository.JobApplicationRepository;
import es.duit.app.repository.ServiceJobRepository;
import es.duit.app.repository.ServiceRequestRepository;

// ============================================================================
// SERVICIO DE TRABAJOS - GESTIONA TRABAJOS/SERVICIOS CONTRATADOS
// ============================================================================
@Service
@Transactional
@RequiredArgsConstructor
public class JobService {

    // =========================================================================
    // DEPENDENCIAS Y ATRIBUTOS
    // =========================================================================
    private final ServiceJobRepository serviceJobRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final ServiceRequestRepository serviceRequestRepository;

    // ============================================================================
    // ===== CREACION =====
    // ============================================================================
    public ServiceJob acceptApplication(Long applicationId, AppUser usuario) {
        // 1) Buscar postulación y solicitud
        JobApplication postulacion = getApplicationById(applicationId);
        ServiceRequest solicitud = postulacion.getRequest();

        // 2) Validar permisos y estado
        validateUserIsClient(solicitud, usuario);
        validateRequestIsPublished(solicitud);
        validateApplicationIsPending(postulacion);

        // 3) Aceptar la postulación y rechazar el resto
        markApplicationAsAccepted(postulacion);
        rejectOtherApplications(solicitud, applicationId);

        // 4) Crear el trabajo y actualizar la solicitud
        ServiceJob nuevoTrabajo = createJobFromApplication(postulacion, solicitud);
        updateRequestStatus(solicitud, ServiceRequest.Status.COMPLETED);

        return nuevoTrabajo;
    }

    // ============================================================================
    // ===== ACTUALIZACION =====
    // ============================================================================
    public void rejectApplication(Long applicationId, AppUser usuario) {
        // 1) Buscar postulación y solicitud
        JobApplication postulacion = getApplicationById(applicationId);
        ServiceRequest solicitud = postulacion.getRequest();

        // 2) Validar permisos y estado
        validateUserIsClient(solicitud, usuario);
        validateApplicationIsPending(postulacion);

        // 3) Rechazar postulación
        markApplicationAsRejected(postulacion);
    }

    // ============================================================================
    // TRABAJOS - CAMBIOS DE ESTADO
    // ============================================================================
    public void startJob(Long jobId, AppUser usuario) {
        // 1) Buscar trabajo
        ServiceJob trabajo = getJobById(jobId);

        // 2) Validar permisos y estado
        validateUserIsProfessional(trabajo, usuario);
        validateJobIsCreated(trabajo);

        // 3) Cambiar estado
        markJobAsInProgress(trabajo);
    }

    public void pauseJob(Long jobId, AppUser usuario) {
        ServiceJob trabajo = getJobById(jobId);

        validateUserIsProfessional(trabajo, usuario);
        validateJobIsInProgress(trabajo);

        markJobAsPaused(trabajo);
    }

    public void resumeJob(Long jobId, AppUser usuario) {
        ServiceJob trabajo = getJobById(jobId);

        validateUserIsProfessional(trabajo, usuario);
        validateJobIsPaused(trabajo);

        markJobAsInProgress(trabajo);
    }

    public void completeJob(Long jobId, AppUser usuario) {
        ServiceJob trabajo = getJobById(jobId);

        validateUserIsProfessional(trabajo, usuario);
        validateJobCanBeCompleted(trabajo);

        markJobAsCompleted(trabajo);
    }

    public void cancelJob(Long jobId, AppUser usuario) {
        ServiceJob trabajo = getJobById(jobId);

        validateUserCanCancel(trabajo, usuario);
        validateJobIsNotCompleted(trabajo);

        markJobAsCanceled(trabajo);

        ServiceRequest solicitud = trabajo.getRequest();
        updateRequestStatus(solicitud, ServiceRequest.Status.PUBLISHED);
    }

    // ============================================================================
    // ===== VALIDACIONES =====
    // ============================================================================
    private void validateUserIsClient(ServiceRequest solicitud, AppUser usuario) {
        if (!solicitud.getClient().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("No tienes permiso para esta acción en esta solicitud");
        }
    }

    private void validateUserIsProfessional(ServiceJob trabajo, AppUser usuario) {
        if (!trabajo.getProfessional().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("No tienes permiso para esta acción en este trabajo");
        }
    }

    private void validateUserCanCancel(ServiceJob trabajo, AppUser usuario) {
        ServiceRequest solicitud = trabajo.getRequest();
        boolean esCliente = solicitud.getClient().getId().equals(usuario.getId());
        boolean esProfesional = trabajo.getProfessional().getId().equals(usuario.getId());

        if (!esCliente && !esProfesional) {
            throw new IllegalArgumentException("No tienes permiso para cancelar este trabajo");
        }
    }

    // ============================================================================
    // VALIDACIONES DE ESTADO - POSTULACIONES
    // ============================================================================
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

    // ============================================================================
    // VALIDACIONES DE ESTADO - TRABAJOS
    // ============================================================================
    private void validateJobCanBeCompleted(ServiceJob trabajo) {
        if (trabajo.getStatus() != ServiceJob.Status.IN_PROGRESS
                && trabajo.getStatus() != ServiceJob.Status.PAUSED) {
            throw new IllegalArgumentException("El trabajo debe estar en progreso o pausado para ser completado");
        }
    }

    private void validateJobIsInProgress(ServiceJob trabajo) {
        if (trabajo.getStatus() != ServiceJob.Status.IN_PROGRESS) {
            throw new IllegalArgumentException("El trabajo debe estar en progreso para ser pausado");
        }
    }

    private void validateJobIsPaused(ServiceJob trabajo) {
        if (trabajo.getStatus() != ServiceJob.Status.PAUSED) {
            throw new IllegalArgumentException("El trabajo debe estar pausado para ser reanudado");
        }
    }

    private void validateJobIsCreated(ServiceJob trabajo) {
        if (trabajo.getStatus() != ServiceJob.Status.CREATED) {
            throw new IllegalArgumentException("El trabajo debe estar creado para ser iniciado");
        }
    }

    private void validateJobIsNotCompleted(ServiceJob trabajo) {
        if (trabajo.getStatus() == ServiceJob.Status.COMPLETED) {
            throw new IllegalArgumentException("No se puede cancelar un trabajo completado");
        }
    }

    // ============================================================================
    // ===== METODOS AUXILIARES PRIVADOS =====
    // ============================================================================
    // ============================================================================
    // OBTENCION DE DATOS
    // ============================================================================
    private JobApplication getApplicationById(Long applicationId) {
        if (applicationId == null) {
            throw new IllegalArgumentException("ID de aplicación requerido");
        }
        return jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Postulación no encontrada"));
    }

    private ServiceJob getJobById(Long jobId) {
        if (jobId == null) {
            throw new IllegalArgumentException("ID de trabajo requerido");
        }
        return serviceJobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Trabajo no encontrado"));
    }

    // ============================================================================
    // CAMBIOS DE ESTADO - POSTULACIONES
    // ============================================================================
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

    // ============================================================================
    // CAMBIOS DE ESTADO - TRABAJOS
    // ============================================================================
    private ServiceJob createJobFromApplication(JobApplication postulacion, ServiceRequest solicitud) {
        ServiceJob nuevoTrabajo = new ServiceJob();
        nuevoTrabajo.setRequest(solicitud);
        nuevoTrabajo.setApplication(postulacion);
        nuevoTrabajo.setAgreedPrice(postulacion.getProposedPrice());
        nuevoTrabajo.setStatus(ServiceJob.Status.IN_PROGRESS);
        nuevoTrabajo.setStartDate(LocalDateTime.now());

        return serviceJobRepository.save(nuevoTrabajo);
    }

    private void markJobAsCompleted(ServiceJob trabajo) {
        trabajo.setStatus(ServiceJob.Status.COMPLETED);
        trabajo.setEndDate(LocalDateTime.now());
        serviceJobRepository.save(trabajo);
    }

    private void markJobAsPaused(ServiceJob trabajo) {
        trabajo.setStatus(ServiceJob.Status.PAUSED);
        serviceJobRepository.save(trabajo);
    }

    private void markJobAsInProgress(ServiceJob trabajo) {
        trabajo.setStatus(ServiceJob.Status.IN_PROGRESS);
        serviceJobRepository.save(trabajo);
    }

    private void markJobAsCanceled(ServiceJob trabajo) {
        trabajo.setStatus(ServiceJob.Status.CANCELLED);
        serviceJobRepository.save(trabajo);
    }

    // ============================================================================
    // CAMBIOS DE ESTADO - SOLICITUDES
    // ============================================================================
    private void updateRequestStatus(ServiceRequest solicitud, ServiceRequest.Status nuevoEstado) {
        solicitud.setStatus(nuevoEstado);
        serviceRequestRepository.save(solicitud);
    }
}