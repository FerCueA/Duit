package es.duit.app.controller;

import es.duit.app.entity.AppUser;
import es.duit.app.entity.JobApplication;
import es.duit.app.entity.ServiceRequest;
import es.duit.app.entity.ServiceJob;
import es.duit.app.repository.AppUserRepository;
import es.duit.app.repository.JobApplicationRepository;
import es.duit.app.repository.ServiceJobRepository;
import es.duit.app.service.AuthService;
import es.duit.app.repository.ServiceRequestRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/jobs/postulaciones")

public class PostulacionesController {

    private final ServiceRequestRepository serviceRequestRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final ServiceJobRepository serviceJobRepository;
    private final AuthService authService;

    public PostulacionesController(
            ServiceRequestRepository serviceRequestRepository,
            JobApplicationRepository jobApplicationRepository,
            AppUserRepository appUserRepository,
            ServiceJobRepository serviceJobRepository,
            AuthService authService) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.jobApplicationRepository = jobApplicationRepository;
        this.serviceJobRepository = serviceJobRepository;
        this.authService = authService;
    }

    // Ver las postulaciones de una solicitud
    @GetMapping("/{id}")
    public String verPostulaciones(@PathVariable Long id, Authentication auth, Model model) {
        try {
            AppUser usuarioLogueado = authService.obtenerUsuarioAutenticado(auth);
            List<ServiceRequest> solicitudes = serviceRequestRepository.findByIdAndClient(id, usuarioLogueado);

            if (solicitudes.isEmpty()) {
                model.addAttribute("postulaciones", null);
                model.addAttribute("solicitud", null);
                return "jobs/ver-postulaciones";
            }

            ServiceRequest solicitud = solicitudes.get(0);
            List<JobApplication> postulaciones = jobApplicationRepository.findByRequest(solicitud);

            for (JobApplication postulacion : postulaciones) {
                postulacion.getProfessional().getUser().getFullName();
                postulacion.getProfessional().getUser().getEmail();
                postulacion.getProfessional().getUser().getPhone();
            }

            model.addAttribute("postulaciones", postulaciones);
            model.addAttribute("solicitud", solicitud);
            return "jobs/ver-postulaciones";

        } catch (Exception e) {

            e.printStackTrace();
            return "redirect:/jobs/mis-solicitudes";
        }
    }

    // Acepta una postulación pendiente y rechaza las demás
    @PostMapping("/aceptar/{postulacionId}")
    public String aceptarPostulacion(@PathVariable Long postulacionId, Authentication auth) {
        AppUser usuarioLogueado = authService.obtenerUsuarioAutenticado(auth);
        JobApplication postulacion = jobApplicationRepository.findById(postulacionId).orElse(null);
        if (postulacion == null) {
            return "redirect:/jobs/mis-solicitudes";
        }
        ServiceRequest solicitud = postulacion.getRequest();
        if (!solicitud.getClient().getId().equals(usuarioLogueado.getId())) {
            return "redirect:/jobs/mis-solicitudes";
        }

        // Rechazar todas las demás postulaciones
        List<JobApplication> otras = jobApplicationRepository.findByRequest(solicitud);
        for (JobApplication otra : otras) {
            if (!otra.getId().equals(postulacionId)) {
                otra.setStatus(JobApplication.Status.REJECTED);
                otra.setRespondedAt(LocalDateTime.now());
                jobApplicationRepository.save(otra);
            }
        }

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
        serviceJobRepository.save(nuevoTrabajo);

        // Cambiar el estado de la solicitud a IN_PROGRESS
        solicitud.setStatus(ServiceRequest.Status.IN_PROGRESS);
        serviceRequestRepository.save(solicitud);

        return "redirect:/jobs/postulaciones/" + solicitud.getId();
    }

    // Rechaza una postulación
    @PostMapping("/rechazar/{postulacionId}")
    public String rechazarPostulacion(@PathVariable Long postulacionId, Authentication auth) {
        AppUser usuarioLogueado = authService.obtenerUsuarioAutenticado(auth);
        JobApplication postulacion = jobApplicationRepository.findById(postulacionId).orElse(null);
        if (postulacion == null) {
            return "redirect:/jobs/mis-solicitudes";
        }
        ServiceRequest solicitud = postulacion.getRequest();
        if (!solicitud.getClient().getId().equals(usuarioLogueado.getId())) {
            return "redirect:/jobs/mis-solicitudes";
        }
        postulacion.setStatus(JobApplication.Status.REJECTED);
        postulacion.setRespondedAt(LocalDateTime.now());
        jobApplicationRepository.save(postulacion);
        return "redirect:/jobs/postulaciones/" + solicitud.getId();
    }

}
