package es.duit.app.service;

import es.duit.app.entity.AppUser;
import es.duit.app.entity.JobApplication;
import es.duit.app.entity.ServiceRequest;
import es.duit.app.repository.JobApplicationRepository;
import es.duit.app.repository.ServiceRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

// ============================================================================
// SERVICIO DE POSTULACIONES - GESTIONA POSTULACIONES A SERVICIOS
// ============================================================================
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

    // ============================================================================
    // PERMITE QUE UN PROFESIONAL SE POSTULE A UNA OFERTA DE SERVICIO
    // ============================================================================
    public JobApplication postularseAOferta(Long ofertaId, BigDecimal precio, String mensaje, AppUser usuario) {
        validateUserHasProfessionalProfile(usuario);

        ServiceRequest oferta = getOfferById(ofertaId);

        validateOfferIsPublished(oferta);

        validateIsNotOfferOwner(oferta, usuario);

        validateHasNotAppliedBefore(oferta, usuario);

        validatePriceIsValid(precio);

        JobApplication postulacion = buildJobApplication(oferta, usuario, precio, mensaje);
        
        if (postulacion == null) {
            throw new IllegalStateException("Error al crear la postulación");
        }

        return jobApplicationRepository.save(postulacion);
    }

    // =========================================================================
    // PERMITE EDITAR UNA POSTULACION PROPIA (SOLO SI ESTA PENDING)
    // =========================================================================
    public JobApplication editarPostulacion(Long postulacionId, AppUser usuario, BigDecimal precio,
            String mensaje) {
        validateUserHasProfessionalProfile(usuario);

        JobApplication postulacion = getApplicationById(postulacionId);

        validateApplicationOwnership(postulacion, usuario);

        validateApplicationIsPending(postulacion);

        if (precio != null) {
            validatePriceIsValid(precio);
            postulacion.setProposedPrice(precio);
        }

        String mensajeFinal = null;
        if (mensaje != null && !mensaje.trim().isEmpty()) {
            mensajeFinal = mensaje.trim();
        }
        postulacion.setMessage(mensajeFinal);

        return jobApplicationRepository.save(postulacion);
    }

    // =========================================================================
    // PERMITE RETIRAR UNA POSTULACION PROPIA (SOLO SI ESTA PENDING)
    // =========================================================================
    public JobApplication retirarPostulacion(Long postulacionId, AppUser usuario) {
        validateUserHasProfessionalProfile(usuario);

        JobApplication postulacion = getApplicationById(postulacionId);

        validateApplicationOwnership(postulacion, usuario);

        validateApplicationIsPending(postulacion);

        postulacion.setStatus(JobApplication.Status.WITHDRAWN);
        postulacion.setRespondedAt(java.time.LocalDateTime.now());

        return jobApplicationRepository.save(postulacion);
    }

    // ============================================================================
    // OBTIENE UNA OFERTA DE SERVICIO POR ID
    // ============================================================================
    private ServiceRequest getOfferById(Long ofertaId) {
        // Buscar la oferta en la BD
        if (ofertaId == null) {
            throw new IllegalArgumentException("ID de oferta requerido");
        }
        ServiceRequest oferta = serviceRequestRepository.findById(ofertaId)
                .orElseThrow(() -> new IllegalArgumentException("La oferta no existe"));

        return oferta;
    }

    // =========================================================================
    // OBTIENE UNA POSTULACION POR ID
    // =========================================================================
    private JobApplication getApplicationById(Long postulacionId) {
        if (postulacionId == null) {
            throw new IllegalArgumentException("ID de postulación requerido");
        }

        return jobApplicationRepository.findById(postulacionId)
                .orElseThrow(() -> new IllegalArgumentException("Postulación no encontrada"));
    }

    // ============================================================================
    // VALIDA QUE LA OFERTA ESTÁ EN ESTADO PUBLICADO
    // ============================================================================
    private void validateOfferIsPublished(ServiceRequest oferta) {
        // Obtener el estado de la oferta
        ServiceRequest.Status estado = oferta.getStatus();

        // Verificar que está publicada
        boolean estaPublicada = estado == ServiceRequest.Status.PUBLISHED;

        if (!estaPublicada) {
            throw new IllegalArgumentException("La oferta no está disponible");
        }
    }

    // ============================================================================
    // VALIDA QUE EL USUARIO TIENE UN PERFIL PROFESIONAL CONFIGURADO
    // ============================================================================
    private void validateUserHasProfessionalProfile(AppUser usuario) {
        // Obtener el perfil profesional del usuario
        Object perfilProfesional = usuario.getProfessionalProfile();

        // Verificar que existe
        if (perfilProfesional == null) {
            throw new IllegalArgumentException("No tienes un perfil profesional configurado");
        }
    }

    // =========================================================================
    // VALIDA QUE LA POSTULACION ES DEL PROFESIONAL LOGUEADO
    // =========================================================================
    private void validateApplicationOwnership(JobApplication postulacion, AppUser usuario) {
        Long usuarioId = usuario.getId();
        Long profesionalId = postulacion.getProfessional().getUser().getId();

        if (!profesionalId.equals(usuarioId)) {
            throw new IllegalArgumentException("No tienes permiso para modificar esta postulación");
        }
    }

    // =========================================================================
    // VALIDA QUE LA POSTULACION ESTA PENDING
    // =========================================================================
    private void validateApplicationIsPending(JobApplication postulacion) {
        if (postulacion.getStatus() != JobApplication.Status.PENDING) {
            throw new IllegalArgumentException("Solo puedes modificar postulaciones en estado pendiente");
        }
    }

    // ============================================================================
    // VALIDA QUE EL USUARIO NO ES EL DUEÑO DE LA OFERTA
    // ============================================================================
    private void validateIsNotOfferOwner(ServiceRequest oferta, AppUser usuario) {
        // Obtener el ID del dueño de la oferta
        Long duenioId = oferta.getClient().getId();

        // Obtener el ID del usuario
        Long usuarioId = usuario.getId();

        // Verificar que el usuario no es el dueño
        if (duenioId.equals(usuarioId)) {
            throw new IllegalArgumentException("No puedes postularte a tu propia oferta");
        }
    }

    // ============================================================================
    // VALIDA QUE EL USUARIO NO SE HA POSTULADO YA A ESTA OFERTA
    // ============================================================================
    private void validateHasNotAppliedBefore(ServiceRequest oferta, AppUser usuario) {
        // Obtener el ID del usuario
        Long usuarioId = usuario.getId();

        // Buscar si existe una postulación del usuario para esta oferta
        boolean yaSePostulo = jobApplicationRepository.findByRequest(oferta).stream()
                .anyMatch(app -> app.getProfessional().getUser().getId().equals(usuarioId));

        if (yaSePostulo) {
            throw new IllegalArgumentException("Ya te has postulado a esta oferta");
        }
    }

    // ============================================================================
    // VALIDA QUE EL PRECIO PROPUESTO ES VÁLIDO
    // ============================================================================
    private void validatePriceIsValid(BigDecimal precio) {
        // Verificar que el precio no es nulo
        if (precio == null) {
            throw new IllegalArgumentException("El precio es requerido");
        }

        // Verificar que es mayor a cero
        int comparacion = precio.compareTo(BigDecimal.ZERO);
        if (comparacion <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0");
        }
    }

    // ============================================================================
    // CONSTRUYE UN OBJETO JOBAPPLICATION CON LOS DATOS PROPORCIONADOS
    // ============================================================================
    private JobApplication buildJobApplication(ServiceRequest oferta, AppUser usuario, BigDecimal precio,
            String mensaje) {
        // Crear nueva instancia de JobApplication
        JobApplication postulacion = new JobApplication();

        // Asignar la oferta/solicitud
        postulacion.setRequest(oferta);

        // Asignar el perfil profesional del usuario
        postulacion.setProfessional(usuario.getProfessionalProfile());

        // Procesar el mensaje (si está vacío, asignamos null)
        String mensajeFinal = null;
        if (mensaje != null && !mensaje.trim().isEmpty()) {
            mensajeFinal = mensaje;
        }
        postulacion.setMessage(mensajeFinal);

        // Asignar el precio propuesto
        postulacion.setProposedPrice(precio);

        // Asignar el estado como PENDING
        postulacion.setStatus(JobApplication.Status.PENDING);

        return postulacion;
    }
}
