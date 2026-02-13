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

    @Query("SELECT DISTINCT j FROM ServiceJob j "
            + "JOIN FETCH j.request r "
            + "LEFT JOIN FETCH r.category "
            + "LEFT JOIN FETCH r.serviceAddress "
            + "LEFT JOIN FETCH r.client "
            + "JOIN FETCH j.application a "
            + "LEFT JOIN FETCH a.professional p "
            + "LEFT JOIN FETCH p.user "
            + "WHERE r.client = :cliente")
    List<ServiceJob> findHistoryByCliente(@Param("cliente") AppUser cliente);

    @Query("SELECT DISTINCT j FROM ServiceJob j "
            + "JOIN FETCH j.request r "
            + "LEFT JOIN FETCH r.category "
            + "LEFT JOIN FETCH r.serviceAddress "
            + "LEFT JOIN FETCH r.client "
            + "JOIN FETCH j.application a "
            + "LEFT JOIN FETCH a.professional p "
            + "LEFT JOIN FETCH p.user "
            + "WHERE a.professional.user = :profesional")
    List<ServiceJob> findHistoryByProfesional(@Param("profesional") AppUser profesional);
    
}

