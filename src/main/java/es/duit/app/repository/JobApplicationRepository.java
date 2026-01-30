package es.duit.app.repository;

import es.duit.app.entity.JobApplication;
import es.duit.app.entity.ProfessionalProfile;
import es.duit.app.entity.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    // Buscar todas las postulaciones de una solicitud
    List<JobApplication> findByRequest(ServiceRequest request);

    // Buscar postulaciones por estado
    List<JobApplication> findByRequestAndStatus(ServiceRequest request, JobApplication.Status status);
    
    // Buscar postulación específica de un profesional a una solicitud
    List<JobApplication> findByRequestAndProfessional(ServiceRequest request, ProfessionalProfile professional);
    
    // Buscar todas las postulaciones de un profesional
    List<JobApplication> findByProfessional(ProfessionalProfile professional);
}