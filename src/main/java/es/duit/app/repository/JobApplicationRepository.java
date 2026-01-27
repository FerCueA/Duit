package es.duit.app.repository;

import es.duit.app.entity.JobApplication;
import es.duit.app.entity.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    // Buscar todas las postulaciones de una solicitud ordenadas por fecha
    List<JobApplication> findByRequestOrderByAppliedAtDesc(ServiceRequest request);

    // Buscar solo las postulaciones que estan pendientes (PENDING)
    @Query("SELECT ja FROM JobApplication ja WHERE ja.request = :request AND ja.status = 'PENDING'")
    List<JobApplication> findPendingByRequest(@Param("request") ServiceRequest request);

    // Verificar si hay postulaciones que ya fueron aceptadas
    @Query("SELECT COUNT(ja) > 0 FROM JobApplication ja WHERE ja.request = :request AND ja.status = 'ACCEPTED'")
    boolean hasAcceptedApplications(@Param("request") ServiceRequest request);

    // Cancelar todas las postulaciones pendientes de una solicitud
    @Modifying
    @Query("UPDATE JobApplication ja SET ja.status = 'WITHDRAWN', ja.respondedAt = CURRENT_TIMESTAMP WHERE ja.request = :request AND ja.status = 'PENDING'")
    int withdrawPendingApplications(@Param("request") ServiceRequest request);

    // Contar cuantas postulaciones hay en un estado especifico
    @Query("SELECT COUNT(ja) FROM JobApplication ja WHERE ja.request = :request AND ja.status = :status")
    Long countByRequestAndStatus(@Param("request") ServiceRequest request, @Param("status") JobApplication.Status status);
}