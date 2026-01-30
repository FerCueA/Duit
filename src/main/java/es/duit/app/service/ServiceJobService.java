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

@Service
@Transactional
public class ServiceJobService {

    private final ServiceJobRepository serviceJobRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final ServiceRequestRepository serviceRequestRepository;

    public ServiceJobService(ServiceJobRepository serviceJobRepository,
                            JobApplicationRepository jobApplicationRepository,
                            ServiceRequestRepository serviceRequestRepository) {
        this.serviceJobRepository = serviceJobRepository;
        this.jobApplicationRepository = jobApplicationRepository;
        this.serviceRequestRepository = serviceRequestRepository;
    }

    // Aceptar postulación y crear trabajo
    public ServiceJob aceptarPostulacion(Long postulacionId, AppUser usuario) {
        
        // Obtener la postulación
        JobApplication postulacion = jobApplicationRepository.findById(postulacionId)
            .orElseThrow(() -> new IllegalArgumentException("Postulación no encontrada"));

        ServiceRequest solicitud = postulacion.getRequest();

        // Validar que eres el cliente
        if (!solicitud.getClient().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("No tienes permiso para aceptar postulaciones en esta solicitud");
        }

        // Validar que la postulación está pendiente
        if (postulacion.getStatus() != JobApplication.Status.PENDING) {
            throw new IllegalArgumentException("La postulación no está en estado pendiente");
        }

        // Rechazar todas las otras postulaciones
        List<JobApplication> otrasPostulaciones = jobApplicationRepository.findByRequest(solicitud);
        otrasPostulaciones.stream()
            .filter(app -> !app.getId().equals(postulacionId))
            .forEach(app -> {
                app.setStatus(JobApplication.Status.REJECTED);
                app.setRespondedAt(LocalDateTime.now());
                jobApplicationRepository.save(app);
            });

        // Aceptar la postulación seleccionada
        postulacion.setStatus(JobApplication.Status.ACCEPTED);
        postulacion.setRespondedAt(LocalDateTime.now());
        jobApplicationRepository.save(postulacion);

        // Crear el trabajo (ServiceJob)
        ServiceJob nuevoTrabajo = new ServiceJob();
        nuevoTrabajo.setRequest(solicitud);
        nuevoTrabajo.setApplication(postulacion);
        nuevoTrabajo.setAgreedPrice(postulacion.getProposedPrice());
        nuevoTrabajo.setStatus(ServiceJob.Status.CREATED);
        nuevoTrabajo.setStartDate(LocalDateTime.now());
        ServiceJob trabajoGuardado = serviceJobRepository.save(nuevoTrabajo);

        // Cambiar estado de la solicitud a IN_PROGRESS
        solicitud.setStatus(ServiceRequest.Status.IN_PROGRESS);
        serviceRequestRepository.save(solicitud);

        return trabajoGuardado;
    }

    // Rechazar postulación
    public void rechazarPostulacion(Long postulacionId, AppUser usuario) {
        
        JobApplication postulacion = jobApplicationRepository.findById(postulacionId)
            .orElseThrow(() -> new IllegalArgumentException("Postulación no encontrada"));

        ServiceRequest solicitud = postulacion.getRequest();

        // Validar que el usuario es el cliente
        if (!solicitud.getClient().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("No tienes permiso para rechazar postulaciones en esta solicitud");
        }

        postulacion.setStatus(JobApplication.Status.REJECTED);
        postulacion.setRespondedAt(LocalDateTime.now());
        jobApplicationRepository.save(postulacion);
    }

    // Finalizar trabajo
    public void finalizarTrabajo(Long jobId, AppUser usuario) {
        
        ServiceJob trabajo = serviceJobRepository.findById(jobId)
            .orElseThrow(() -> new IllegalArgumentException("Trabajo no encontrado"));

        // Validar que es el profesional del trabajo
        if (!trabajo.getProfessional().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("No tienes permiso para finalizar este trabajo");
        }

        // Validar que está en progreso
        if (trabajo.getStatus() != ServiceJob.Status.IN_PROGRESS && trabajo.getStatus() != ServiceJob.Status.PAUSED) {
            throw new IllegalArgumentException("Solo se pueden finalizar trabajos en progreso o pausados");
        }

        trabajo.setStatus(ServiceJob.Status.COMPLETED);
        trabajo.setEndDate(LocalDateTime.now());
        serviceJobRepository.save(trabajo);
    }

    // Pausar trabajo
    public void pausarTrabajo(Long jobId, AppUser usuario) {
        
        ServiceJob trabajo = serviceJobRepository.findById(jobId)
            .orElseThrow(() -> new IllegalArgumentException("Trabajo no encontrado"));

        // Validar que es el profesional del trabajo
        if (!trabajo.getProfessional().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("No tienes permiso para pausar este trabajo");
        }

        // Validar que está en progreso
        if (trabajo.getStatus() != ServiceJob.Status.IN_PROGRESS) {
            throw new IllegalArgumentException("Solo se pueden pausar trabajos en progreso");
        }

        trabajo.setStatus(ServiceJob.Status.PAUSED);
        serviceJobRepository.save(trabajo);
    }

    // Reanudar trabajo pausado
    public void reanudarTrabajo(Long jobId, AppUser usuario) {
        
        ServiceJob trabajo = serviceJobRepository.findById(jobId)
            .orElseThrow(() -> new IllegalArgumentException("Trabajo no encontrado"));

        // Validar que es el profesional del trabajo
        if (!trabajo.getProfessional().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("No tienes permiso para reanudar este trabajo");
        }

        // Validar que está pausado
        if (trabajo.getStatus() != ServiceJob.Status.PAUSED) {
            throw new IllegalArgumentException("Solo se pueden reanudar trabajos pausados");
        }

        trabajo.setStatus(ServiceJob.Status.IN_PROGRESS);
        serviceJobRepository.save(trabajo);
    }

    // Cancelar trabajo
    public void cancelarTrabajo(Long jobId, AppUser usuario) {
        
        ServiceJob trabajo = serviceJobRepository.findById(jobId)
            .orElseThrow(() -> new IllegalArgumentException("Trabajo no encontrado"));

        // Validar que es el profesional del trabajo
        if (!trabajo.getProfessional().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("No tienes permiso para cancelar este trabajo");
        }

        // Validar que no está completado
        if (trabajo.getStatus() == ServiceJob.Status.COMPLETED) {
            throw new IllegalArgumentException("No se pueden cancelar trabajos completados");
        }

        trabajo.setStatus(ServiceJob.Status.CANCELLED);
        serviceJobRepository.save(trabajo);
    }
}
