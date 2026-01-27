package es.duit.app.service;

import es.duit.app.entity.AppUser;
import es.duit.app.entity.ServiceRequest;
import es.duit.app.entity.JobApplication;
import es.duit.app.repository.AppUserRepository;
import es.duit.app.repository.ServiceRequestRepository;
import es.duit.app.repository.JobApplicationRepository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServiceRequestService {

    // Estos son los repositorios que necesito
    private final ServiceRequestRepository serviceRequestRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final AppUserRepository appUserRepository;

    // Constructor donde Spring me inyecta los repositorios
    public ServiceRequestService(ServiceRequestRepository serviceRequestRepository,
            JobApplicationRepository jobApplicationRepository,
            AppUserRepository appUserRepository) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.jobApplicationRepository = jobApplicationRepository;
        this.appUserRepository = appUserRepository;
    }

    // Este método cancela solicitudes
    public String cancelarSolicitud(Long solicitudId, String username) {

        // Verifico que me pasen datos válidos
        if (solicitudId == null) {
            throw new IllegalArgumentException("El ID de la solicitud no puede ser nulo");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío");
        }

        // Busco el usuario en la BD
        List<AppUser> usuarios = appUserRepository.findByUsername(username.trim());
        if (usuarios.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado en el sistema");
        }
        AppUser usuarioLogueado = usuarios.get(0);

        // Ahora busco la solicitud que quiere cancelar
        List<ServiceRequest> solicitudes = serviceRequestRepository.findByIdAndClient(solicitudId, usuarioLogueado);
        if (solicitudes.isEmpty()) {
            throw new IllegalArgumentException("Solicitud no encontrada o sin permisos para cancelarla");
        }
        ServiceRequest solicitudACancelar = solicitudes.get(0);

        // Solo puedo cancelar si está publicada
        ServiceRequest.Status estadoActual = solicitudACancelar.getStatus();
        if (estadoActual != ServiceRequest.Status.PUBLISHED) {
            throw new IllegalStateException("Solo se pueden cancelar solicitudes que están publicadas");
        }

        // No puedo cancelar si ya hay trabajos aceptados
        List<JobApplication> trabajosAceptados = jobApplicationRepository.findByRequestAndStatus(
                solicitudACancelar, JobApplication.Status.ACCEPTED);
        boolean hayTrabajosAceptados = !trabajosAceptados.isEmpty();
        if (hayTrabajosAceptados) {
            throw new IllegalStateException("No se puede cancelar una solicitud que ya tiene un trabajo aceptado");
        }

        // Veo si hay postulaciones pendientes
        List<JobApplication> postulacionesPendientes = jobApplicationRepository.findByRequestAndStatus(
                solicitudACancelar, JobApplication.Status.PENDING);
        boolean hayPostulacionesPendientes = !postulacionesPendientes.isEmpty();

        // Dependiendo si hay postulaciones o no, hago una cosa u otra
        if (hayPostulacionesPendientes) {
            return procesarCancelacionConPostulaciones(solicitudACancelar, postulacionesPendientes);
        } else {
            return procesarDespublicacion(solicitudACancelar);
        }
    }

    // Cuando hay postulaciones pendientes, las cancelo todas
    private String procesarCancelacionConPostulaciones(ServiceRequest solicitud,
            List<JobApplication> postulacionesPendientes) {
        // Marco la solicitud como cancelada
        solicitud.setStatus(ServiceRequest.Status.CANCELLED);
        serviceRequestRepository.save(solicitud);

        // Cancelo cada postulación de una en una
        int numeroDePostulacionesCanceladas = 0;
        for (JobApplication aplicacion : postulacionesPendientes) {
            aplicacion.setStatus(JobApplication.Status.WITHDRAWN);
            aplicacion.setRespondedAt(LocalDateTime.now());
            jobApplicationRepository.save(aplicacion);
            numeroDePostulacionesCanceladas++;
        }

        // Devuelvo un mensaje para que el usuario sepa qué pasó
        String mensaje = "Solicitud cancelada exitosamente. Se notificó a " +
                numeroDePostulacionesCanceladas + " profesionales.";
        return mensaje;
    }

    // Cuando no hay postulaciones, simplemente la despublico
    private String procesarDespublicacion(ServiceRequest solicitud) {
        // La vuelvo a borrador
        solicitud.setStatus(ServiceRequest.Status.DRAFT);
        serviceRequestRepository.save(solicitud);

        // Le digo al usuario qué hice
        String mensaje = "Solicitud despublicada exitosamente. Ahora está en estado borrador.";
        return mensaje;
    }
}
