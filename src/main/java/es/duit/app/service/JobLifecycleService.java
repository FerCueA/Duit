package es.duit.app.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.duit.app.entity.AppUser;
import es.duit.app.entity.ServiceJob;
import es.duit.app.entity.ServiceRequest;
import es.duit.app.repository.ServiceJobRepository;
import es.duit.app.repository.ServiceRequestRepository;
import lombok.RequiredArgsConstructor;

// ============================================================================
// SERVICIO DE CICLO DE VIDA DE TRABAJOS
// ==========================================================================
@Service
@Transactional
@RequiredArgsConstructor
public class JobLifecycleService {

    private final ServiceJobRepository serviceJobRepository;
    private final ServiceRequestRepository serviceRequestRepository;

    public List<ServiceJob> getJobsForClient(AppUser usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario requerido");
        }

        return serviceJobRepository.findByCliente(usuario);
    }

    public List<ServiceJob> getJobsForProfessional(AppUser usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario requerido");
        }

        return serviceJobRepository.findByProfesional(usuario);
    }

    public void startJob(Long jobId, AppUser usuario) {
        ServiceJob trabajo = getJobById(jobId);

        validateUserIsProfessional(trabajo, usuario);
        validateJobIsCreated(trabajo);

        markJobAsInProgress(trabajo);
    }

    public void pauseJob(Long jobId, AppUser usuario) {
        ServiceJob trabajo = getJobById(jobId);

        validateUserCanManageJob(trabajo, usuario);
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

        validateUserCanManageJob(trabajo, usuario);
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

    private ServiceJob getJobById(Long jobId) {
        if (jobId == null) {
            throw new IllegalArgumentException("ID de trabajo requerido");
        }
        return serviceJobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Trabajo no encontrado"));
    }

    private void validateUserIsProfessional(ServiceJob trabajo, AppUser usuario) {
        if (!trabajo.getProfessional().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("No tienes permiso para esta acción en este trabajo");
        }
    }

    private void validateUserCanManageJob(ServiceJob trabajo, AppUser usuario) {
        boolean esCliente = trabajo.getRequest().getClient().getId().equals(usuario.getId());
        boolean esProfesional = trabajo.getProfessional().getId().equals(usuario.getId());

        if (!esCliente && !esProfesional) {
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
        if (trabajo.getStartDate() == null) {
            trabajo.setStartDate(LocalDateTime.now());
        }
        serviceJobRepository.save(trabajo);
    }

    private void markJobAsCanceled(ServiceJob trabajo) {
        trabajo.setStatus(ServiceJob.Status.CANCELLED);
        serviceJobRepository.save(trabajo);
    }

    private void updateRequestStatus(ServiceRequest solicitud, ServiceRequest.Status nuevoEstado) {
        solicitud.setStatus(nuevoEstado);
        serviceRequestRepository.save(solicitud);
    }
}
