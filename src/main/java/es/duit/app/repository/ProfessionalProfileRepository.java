package es.duit.app.repository;

import es.duit.app.entity.ProfessionalProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessionalProfileRepository extends JpaRepository<ProfessionalProfile, Long> {
   
}
