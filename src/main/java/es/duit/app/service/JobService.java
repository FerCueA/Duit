package es.duit.app.service;

import es.duit.app.entity.AppUser;
import es.duit.app.entity.JobApplication;
import es.duit.app.entity.ServiceJob;
import es.duit.app.entity.ServiceRequest;
import es.duit.app.repository.JobApplicationRepository;
import es.duit.app.repository.ServiceJobRepository;
import es.duit.app.repository.ServiceRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

// ============================================================================
// SERVICIO DE TRABAJOS - GESTIONA TRABAJOS/SERVICIOS CONTRATADOS
// ============================================================================
@Service
@Transactional
public class JobService {

    private final ServiceJobRepository serviceJobRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final ServiceRequestRepository serviceRequestRepository;

    public JobService(ServiceJobRepository serviceJobRepository,
            JobApplicationRepository jobApplicationRepository,
            ServiceRequestRepository serviceRequestRepository) {
        this.serviceJobRepository = serviceJobRepository;
        this.jobApplicationRepository = jobApplicationRepository;
        this.serviceRequestRepository = serviceRequestRepository;
    }

    // ============================================================================
    // ACEPTA UNA POSTULACIÓN Y CREA UN TRABAJO ASOCIADO
    // ============================================================================
    public ServiceJob acceptApplication(Long applicationId, AppUser usuario) {
        // Obtener postulación por ID usando método helper
        JobApplication postulacion = getApplicationById(applicationId);

        // Obtener la solicitud asociada
        ServiceRequest solicitud = postulacion.getRequest();

        // Validar permisos del usuario usando método helper
        validateUserIsClient(solicitud, usuario);

        // Validar estado de la postulación
        validateApplicationIsPending(postulacion);

        // Aceptar la postulación y marcar fecha
        markApplicationAsAccepted(postulacion);

        // Rechazar otras postulaciones a la misma solicitud
        rejectOtherApplications(solicitud, applicationId);

        // Crear y guardar nuevo trabajo
        ServiceJob nuevoTrabajo = createJobFromApplication(postulacion, solicitud);

        // Cambiar estado de solicitud a EN_PROGRESO
        updateRequestStatus(solicitud, ServiceRequest.Status.IN_PROGRESS);

        return nuevoTrabajo;
    }

    // ============================================================================
    // RECHAZA UNA POSTULACIÓN
    // ============================================================================
    public void rejectApplication(Long applicationId, AppUser usuario) {
        // Obtener postulación usando método helper
        JobApplication postulacion = getApplicationById(applicationId);

        // Validar permisos del usuario
        ServiceRequest solicitud = postulacion.getRequest();
        validateUserIsClient(solicitud, usuario);

        // Validar que está pendiente
        validateApplicationIsPending(postulacion);

        // Marcar como rechazada
        markApplicationAsRejected(postulacion);
    }

    // ============================================================================
    // FINALIZA UN TRABAJO (EL PROFESIONAL LO MARCA COMO COMPLETADO)
    // ============================================================================
    public void completeJob(Long jobId, AppUser usuario) {
        // Obtener el trabajo usando método helper
        ServiceJob trabajo = getJobById(jobId);

        // Validar que el usuario es el profesional asignado
        validateUserIsProfessional(trabajo, usuario);

        // Validar que el trabajo puede ser completado
        validateJobCanBeCompleted(trabajo);

        // Marcar como completado y actualizar fecha fin
        markJobAsCompleted(trabajo);
    }

    // ============================================================================
    // PAUSA UN TRABAJO EN PROGRESO
    // ============================================================================
    public void pauseJob(Long jobId, AppUser usuario) {
        // Obtener trabajo
        ServiceJob trabajo = getJobById(jobId);

        // Validar permisos
        validateUserIsProfessional(trabajo, usuario);

        // Validar que está en progreso
        validateJobIsInProgress(trabajo);

        // Marcar como pausado
        markJobAsPaused(trabajo);
    }

    // ============================================================================
    // REANUDA UN TRABAJO PAUSADO
    // ============================================================================
    public void resumeJob(Long jobId, AppUser usuario) {
        // Obtener trabajo
        ServiceJob trabajo = getJobById(jobId);

        // Validar permisos
        validateUserIsProfessional(trabajo, usuario);

        // Validar que está pausado
        validateJobIsPaused(trabajo);

        // Marcar como en progreso de nuevo
        markJobAsInProgress(trabajo);
    }

    // ============================================================================
    // CANCELA UN TRABAJO (NO COMPLETADO)
    // ============================================================================
    public void cancelJob(Long jobId, AppUser usuario) {
        // Obtener trabajo
        ServiceJob trabajo = getJobById(jobId);

        // Validar permisos (cliente o profesional pueden cancelar)
        validateUserCanCancel(trabajo, usuario);

        // Validar que no está completado
        validateJobIsNotCompleted(trabajo);

        // Marcar como cancelado
        markJobAsCanceled(trabajo);

        // Revertir estado de solicitud a publicado
        ServiceRequest solicitud = trabajo.getRequest();
        updateRequestStatus(solicitud, ServiceRequest.Status.PUBLISHED);
    }

    // ============================================================================
    // OBTIENE UNA POSTULACIÓN POR ID O LANZA EXCEPCIÓN SI NO EXISTE
    // ============================================================================
    private JobApplication getApplicationById(Long applicationId) {
        if (applicationId == null) {
            throw new IllegalArgumentException("ID de aplicación requerido");
        }
        return jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Postulación no encontrada"));
    }

    // ============================================================================
    // OBTIENE UN TRABAJO POR ID O LANZA EXCEPCIÓN SI NO EXISTE
    // ============================================================================
    private ServiceJob getJobById(Long jobId) {
        if (jobId == null) {
            throw new IllegalArgumentException("ID de trabajo requerido");
        }
        return serviceJobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Trabajo no encontrado"));
    }

    // ============================================================================
    // VALIDA QUE EL USUARIO ES EL CLIENTE DE LA SOLICITUD
    // ============================================================================
    private void validateUserIsClient(ServiceRequest solicitud, AppUser usuario) {
        if (!solicitud.getClient().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("No tienes permiso para esta acción en esta solicitud");
        }
    }

    // ============================================================================
    // VALIDA QUE EL USUARIO ES EL PROFESIONAL ASIGNADO AL TRABAJO
    // ============================================================================
    private void validateUserIsProfessional(ServiceJob trabajo, AppUser usuario) {
        if (!trabajo.getProfessional().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("No tienes permiso para esta acción en este trabajo");
        }
    }

    // ============================================================================
    // VALIDA QUE EL USUARIO PUEDE CANCELAR (CLIENTE O PROFESIONAL)
    // ============================================================================
    private void validateUserCanCancel(ServiceJob trabajo, AppUser usuario) {
        ServiceRequest solicitud = trabajo.getRequest();
        boolean esCliente = solicitud.getClient().getId().equals(usuario.getId());
        boolean esProfesional = trabajo.getProfessional().getId().equals(usuario.getId());

        if (!esCliente && !esProfesional) {
            throw new IllegalArgumentException("No tienes permiso para cancelar este trabajo");
        }
    }

    // ============================================================================
    // VALIDA QUE LA POSTULACIÓN ESTÁ EN ESTADO PENDING
    // ============================================================================
    private void validateApplicationIsPending(JobApplication postulacion) {
        if (postulacion.getStatus() != JobApplication.Status.PENDING) {
            throw new IllegalArgumentException("La postulación no está en estado pendiente");
        }
    }

    // ============================================================================
    // VALIDA QUE EL TRABAJO PUEDE SER COMPLETADO
    // ============================================================================
    private void validateJobCanBeCompleted(ServiceJob trabajo) {
        if (trabajo.getStatus() != ServiceJob.Status.IN_PROGRESS
                && trabajo.getStatus() != ServiceJob.Status.PAUSED) {
            throw new IllegalArgumentException("El trabajo debe estar en progreso o pausado para ser completado");
        }
    }

    // ============================================================================
    // VALIDA QUE EL TRABAJO ESTÁ EN PROGRESO
    // ============================================================================
    private void validateJobIsInProgress(ServiceJob trabajo) {
        if (trabajo.getStatus() != ServiceJob.Status.IN_PROGRESS) {
            throw new IllegalArgumentException("El trabajo debe estar en progreso para ser pausado");
        }
    }

    // ============================================================================
    // VALIDA QUE EL TRABAJO ESTÁ PAUSADO
    // ============================================================================
    private void validateJobIsPaused(ServiceJob trabajo) {
        if (trabajo.getStatus() != ServiceJob.Status.PAUSED) {
            throw new IllegalArgumentException("El trabajo debe estar pausado para ser reanudado");
        }
    }

    // ============================================================================
    // VALIDA QUE EL TRABAJO NO ESTÁ COMPLETADO
    // ============================================================================
    private void validateJobIsNotCompleted(ServiceJob trabajo) {
        if (trabajo.getStatus() == ServiceJob.Status.COMPLETED) {
            throw new IllegalArgumentException("No se puede cancelar un trabajo completado");
        }
    }

    // ============================================================================
    // MARCA UNA POSTULACIÓN COMO ACEPTADA CON FECHA ACTUAL
    // ============================================================================
    private void markApplicationAsAccepted(JobApplication postulacion) {
        postulacion.setStatus(JobApplication.Status.ACCEPTED);
        postulacion.setRespondedAt(LocalDateTime.now());
        jobApplicationRepository.save(postulacion);
    }

    // ============================================================================
    // MARCA UNA POSTULACIÓN COMO RECHAZADA CON FECHA ACTUAL
    // ============================================================================
    private void markApplicationAsRejected(JobApplication postulacion) {
        postulacion.setStatus(JobApplication.Status.REJECTED);
        postulacion.setRespondedAt(LocalDateTime.now());
        jobApplicationRepository.save(postulacion);
    }

    // ============================================================================
    // RECHAZA TODAS LAS DEMÁS POSTULACIONES CUANDO SE ACEPTA UNA
    // ============================================================================
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
    // CREA UN NUEVO TRABAJO BASADO EN LA POSTULACIÓN ACEPTADA
    // ============================================================================
    private ServiceJob createJobFromApplication(JobApplication postulacion, ServiceRequest solicitud) {
        ServiceJob nuevoTrabajo = new ServiceJob();
        nuevoTrabajo.setRequest(solicitud);
        nuevoTrabajo.setApplication(postulacion);
        nuevoTrabajo.setAgreedPrice(postulacion.getProposedPrice());
        nuevoTrabajo.setStatus(ServiceJob.Status.CREATED);
        nuevoTrabajo.setStartDate(LocalDateTime.now());

        return serviceJobRepository.save(nuevoTrabajo);
    }

    // ============================================================================
    // MARCA EL TRABAJO COMO COMPLETADO CON FECHA FIN
    // ============================================================================
    private void markJobAsCompleted(ServiceJob trabajo) {
        trabajo.setStatus(ServiceJob.Status.COMPLETED);
        trabajo.setEndDate(LocalDateTime.now());
        serviceJobRepository.save(trabajo);
    }

    // ============================================================================
    // MARCA EL TRABAJO COMO PAUSADO
    // ============================================================================
    private void markJobAsPaused(ServiceJob trabajo) {
        trabajo.setStatus(ServiceJob.Status.PAUSED);
        serviceJobRepository.save(trabajo);
    }

    // ============================================================================
    // MARCA EL TRABAJO COMO EN PROGRESO
    // ============================================================================
    private void markJobAsInProgress(ServiceJob trabajo) {
        trabajo.setStatus(ServiceJob.Status.IN_PROGRESS);
        serviceJobRepository.save(trabajo);
    }

    // ============================================================================
    // MARCA EL TRABAJO COMO CANCELADO
    // ============================================================================
    private void markJobAsCanceled(ServiceJob trabajo) {
        trabajo.setStatus(ServiceJob.Status.CANCELLED);
        serviceJobRepository.save(trabajo);
    }

    // ============================================================================
    // ACTUALIZA EL ESTADO DE UNA SOLICITUD
    // ============================================================================
    private void updateRequestStatus(ServiceRequest solicitud, ServiceRequest.Status nuevoEstado) {
        solicitud.setStatus(nuevoEstado);
        serviceRequestRepository.save(solicitud);
    }
}