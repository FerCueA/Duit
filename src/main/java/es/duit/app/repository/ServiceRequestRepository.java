package es.duit.app.repository;

import es.duit.app.entity.AppUser;
import es.duit.app.entity.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {

    // Buscar todas las solicitudes de un cliente
    List<ServiceRequest> findByClient(AppUser client);
    
    // Buscar solicitudes por estado
    List<ServiceRequest> findByStatus(ServiceRequest.Status status);
}