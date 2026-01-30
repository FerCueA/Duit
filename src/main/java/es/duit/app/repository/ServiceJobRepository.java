package es.duit.app.repository;

import es.duit.app.entity.ServiceJob;
import es.duit.app.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ServiceJobRepository extends JpaRepository<ServiceJob, Long> {
   
    // Trabajos del cliente
    @Query("SELECT j FROM ServiceJob j WHERE j.request.client = :cliente")
    List<ServiceJob> findByCliente(@Param("cliente") AppUser cliente);
    
    // Trabajos del profesional
    @Query("SELECT j FROM ServiceJob j WHERE j.application.professional.user = :profesional")
    List<ServiceJob> findByProfesional(@Param("profesional") AppUser profesional);
    
}

