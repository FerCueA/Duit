package es.duit.app.service;

import es.duit.app.entity.AppUser;
import es.duit.app.entity.JobApplication;
import es.duit.app.entity.ServiceRequest;
import es.duit.app.repository.JobApplicationRepository;
import es.duit.app.repository.ServiceRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final ServiceRequestRepository serviceRequestRepository;

    public JobApplicationService(JobApplicationRepository jobApplicationRepository,
                                 ServiceRequestRepository serviceRequestRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.serviceRequestRepository = serviceRequestRepository;
    }

    // Postularse a una oferta
    public JobApplication postularseAOferta(Long ofertaId, BigDecimal precio, String mensaje, AppUser usuario) {
        
        // Validar perfil profesional
        if (usuario.getProfessionalProfile() == null) {
            throw new IllegalArgumentException("No tienes un perfil profesional configurado");
        }

        // Obtener la oferta
        ServiceRequest oferta = serviceRequestRepository.findById(ofertaId)
            .orElseThrow(() -> new IllegalArgumentException("La oferta no existe"));

        // Validar que la oferta está publicada
        if (oferta.getStatus() != ServiceRequest.Status.PUBLISHED) {
            throw new IllegalArgumentException("La oferta no está disponible");
        }

        // No puedes postularte a tu propia oferta
        if (oferta.getClient().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("No puedes postularte a tu propia oferta");
        }

        // Validar que no se haya postulado ya
        boolean yaSePostulo = jobApplicationRepository.findByRequest(oferta).stream()
            .anyMatch(app -> app.getProfessional().getUser().getId().equals(usuario.getId()));

        if (yaSePostulo) {
            throw new IllegalArgumentException("Ya te has postulado a esta oferta");
        }

        // Validar el precio
        if (precio == null || precio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0");
        }

        // Crear la postulación
        JobApplication postulacion = new JobApplication();
        postulacion.setRequest(oferta);
        postulacion.setProfessional(usuario.getProfessionalProfile());
        postulacion.setMessage(mensaje != null && !mensaje.trim().isEmpty() ? mensaje : null);
        postulacion.setProposedPrice(precio);
        postulacion.setStatus(JobApplication.Status.PENDING);

        return jobApplicationRepository.save(postulacion);
    }
}
