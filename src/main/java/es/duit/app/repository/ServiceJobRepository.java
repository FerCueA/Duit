package es.duit.app.repository;

import es.duit.app.entity.ServiceJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceJobRepository extends JpaRepository<ServiceJob, Long> {
   
}
