package es.duit.app.service;

import es.duit.app.entity.AppUser;
import es.duit.app.entity.ServiceRequest;
import es.duit.app.entity.JobApplication;
import es.duit.app.repository.AppUserRepository;
import es.duit.app.repository.ServiceRequestRepository;
import es.duit.app.repository.JobApplicationRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServiceRequestService {

    // Solo mantenemos lo necesario para cancelarSolicitud
    private final ServiceRequestRepository serviceRequestRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final AppUserRepository appUserRepository;

    public ServiceRequestService(ServiceRequestRepository serviceRequestRepository,
            JobApplicationRepository jobApplicationRepository,
            AppUserRepository appUserRepository) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.jobApplicationRepository = jobApplicationRepository;
        this.appUserRepository = appUserRepository;
    }

    // Único método con lógica de negocio compleja
    public String cancelarSolicitud(Long solicitudId, String username) {
        
        // Paso 1: Buscar el usuario en la base de datos
        AppUser usuarioLogueado = appUserRepository.findByUsername(username).orElse(null);
        if (usuarioLogueado == null) {
            throw new IllegalArgumentException("Usuario no encontrado en el sistema");
        }
        
        // Paso 2: Buscar la solicitud del usuario
        ServiceRequest solicitudACancelar = serviceRequestRepository.findByIdAndClient(solicitudId, usuarioLogueado).orElse(null);
        if (solicitudACancelar == null) {
            throw new IllegalArgumentException("Solicitud no encontrada o sin permisos para cancelarla");
        }

        // Paso 3: Verificar que esta publicada
        if (solicitudACancelar.getStatus() != ServiceRequest.Status.PUBLISHED) {
            throw new IllegalStateException("Solo se pueden cancelar solicitudes que están publicadas");
        }

        // Paso 4: Verificar si ya hay trabajos aceptados
        boolean hayTrabajosAceptados = jobApplicationRepository.hasAcceptedApplications(solicitudACancelar);
        if (hayTrabajosAceptados == true) {
            throw new IllegalStateException("No se puede cancelar una solicitud que ya tiene un trabajo aceptado");
        }

        // Paso 5: Contar las postulaciones pendientes
        Long numeroDePostulacionesPendientes = jobApplicationRepository.countByRequestAndStatus(
            solicitudACancelar, JobApplication.Status.PENDING);

        // Paso 6: Decidir que hacer segun las postulaciones
        if (numeroDePostulacionesPendientes > 0) {
            // Hay postulaciones, marcar como cancelada
            solicitudACancelar.setStatus(ServiceRequest.Status.CANCELLED);
            int postulacionesCanceladas = jobApplicationRepository.withdrawPendingApplications(solicitudACancelar);
            String mensajeDeRespuesta = "Solicitud cancelada exitosamente. Se notificó a " + postulacionesCanceladas + " profesionales.";
            return mensajeDeRespuesta;
        } else {
            // No hay postulaciones, volver a borrador
            solicitudACancelar.setStatus(ServiceRequest.Status.DRAFT);
            String mensajeDeRespuesta = "Solicitud despublicada exitosamente. Ahora está en estado borrador.";
            return mensajeDeRespuesta;
        }
    }
}