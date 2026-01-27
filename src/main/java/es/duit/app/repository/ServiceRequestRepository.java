package es.duit.app.repository;

import es.duit.app.entity.AppUser;
import es.duit.app.entity.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {

    // Buscar todas las solicitudes de un cliente específico
    List<ServiceRequest> findByClientOrderByCreatedAtDesc(AppUser client);

    // Buscar solicitud por ID y cliente (que solo vea sus propias solicitudes)
    @Query("SELECT sr FROM ServiceRequest sr WHERE sr.id = :id AND sr.client = :client")
    Optional<ServiceRequest> findByIdAndClient(@Param("id") Long id, @Param("client") AppUser client);
}