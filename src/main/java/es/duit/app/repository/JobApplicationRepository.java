package es.duit.app.repository;

import es.duit.app.entity.JobApplication;
import es.duit.app.entity.ProfessionalProfile;
import es.duit.app.entity.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    // Postulaciones de una solicitud
    List<JobApplication> findByRequest(ServiceRequest request);
    
    // Postulaciones de un profesional
    List<JobApplication> findByProfessional(ProfessionalProfile professional);
}