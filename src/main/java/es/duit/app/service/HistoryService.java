package es.duit.app.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import es.duit.app.dto.HistoryDTO;
import es.duit.app.entity.Address;
import es.duit.app.entity.AppUser;
import es.duit.app.entity.JobApplication;
import es.duit.app.entity.ProfessionalProfile;
import es.duit.app.entity.ServiceJob;
import es.duit.app.entity.ServiceRequest;
import es.duit.app.repository.ServiceJobRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HistoryService {

    private final ServiceJobRepository serviceJobRepository;

    // =========================================================================
    // HISTORIAL - OPERACIONES PRINCIPALES
    // =========================================================================
    public List<HistoryDTO> getHistoryForUser(AppUser usuario) {
        // Traer trabajos donde el usuario es cliente y profesional
        List<ServiceJob> comoCliente = serviceJobRepository.findHistoryByCliente(usuario);
        List<ServiceJob> comoProfesional = serviceJobRepository.findHistoryByProfesional(usuario);

        // Unificar en un mapa por id para evitar duplicados
        Map<Long, ServiceJob> jobs = new LinkedHashMap<>();
        for (ServiceJob job : comoCliente) {
            jobs.put(job.getId(), job);
        }
        for (ServiceJob job : comoProfesional) {
            jobs.putIfAbsent(job.getId(), job);
        }

        // Mapear a DTO para la vista
        List<HistoryDTO> historial = new ArrayList<>();
        for (ServiceJob job : jobs.values()) {
            historial.add(mapToHistory(job, usuario));
        }

        // Ordenar por fecha de inicio (mas reciente primero)
        historial.sort(Comparator.comparing(HistoryDTO::getStartDate,
                Comparator.nullsLast(Comparator.reverseOrder())));

        return historial;
    }

    // =========================================================================
    // MAPEOS - DTO PRINCIPAL
    // =========================================================================
    private HistoryDTO mapToHistory(ServiceJob job, AppUser usuario) {
        HistoryDTO dto = new HistoryDTO();

        // Datos basicos del trabajo
        dto.setJobId(job.getId());
        dto.setJobStatus(job.getStatus() != null ? job.getStatus().name() : null);
        dto.setAgreedPrice(job.getAgreedPrice());
        dto.setStartDate(job.getStartDate());
        dto.setEndDate(job.getEndDate());

        // Datos de la solicitud/oferta
        ServiceRequest request = job.getRequest();
        dto.setRequest(mapRequest(request));

        // Datos de la postulacion
        JobApplication application = job.getApplication();
        dto.setApplication(mapApplication(application));

        // Datos del cliente
        AppUser client = request != null ? request.getClient() : null;
        dto.setClient(mapClient(client));

        // Datos del profesional (direccion solo si es el mismo usuario)
        ProfessionalProfile professional = application != null ? application.getProfessional() : null;
        dto.setProfessional(mapProfessional(professional, usuario));

        return dto;
    }

    // =========================================================================
    // MAPEOS - DATOS DE LA OFERTA
    // =========================================================================
    private HistoryDTO.RequestDTO mapRequest(ServiceRequest request) {
        if (request == null) {
            return null;
        }
        HistoryDTO.RequestDTO dto = new HistoryDTO.RequestDTO();
        dto.setRequestId(request.getId());
        dto.setTitle(request.getTitle());
        dto.setDescription(request.getDescription());
        dto.setCategoryName(request.getCategory() != null ? request.getCategory().getName() : null);
        dto.setRequestedAt(request.getRequestedAt());
        dto.setDeadline(request.getDeadline());

        Address effectiveAddress = request.getEffectiveServiceAddress();
        dto.setServiceAddress(effectiveAddress != null ? effectiveAddress.getFullAddress() : null);

        return dto;
    }

    // =========================================================================
    // MAPEOS - DATOS DE LA POSTULACION
    // =========================================================================
    private HistoryDTO.ApplicationDTO mapApplication(JobApplication application) {
        if (application == null) {
            return null;
        }
        HistoryDTO.ApplicationDTO dto = new HistoryDTO.ApplicationDTO();
        dto.setApplicationId(application.getId());
        dto.setStatus(application.getStatus() != null ? application.getStatus().name() : null);
        dto.setMessage(application.getMessage());
        dto.setProposedPrice(application.getProposedPrice());
        dto.setAppliedAt(application.getAppliedAt());
        dto.setRespondedAt(application.getRespondedAt());
        return dto;
    }

    // =========================================================================
    // MAPEOS - DATOS DEL CLIENTE
    // =========================================================================
    private HistoryDTO.ClientDTO mapClient(AppUser client) {
        if (client == null) {
            return null;
        }
        HistoryDTO.ClientDTO dto = new HistoryDTO.ClientDTO();
        dto.setClientId(client.getId());
        dto.setFullName(client.getFullName());
        dto.setEmail(client.getEmail());
        dto.setPhone(client.getPhone());
        return dto;
    }

    // =========================================================================
    // MAPEOS - DATOS DEL PROFESIONAL (CON DIRECCION SOLO PARA EL MISMO USUARIO)
    // =========================================================================
    private HistoryDTO.ProfessionalDTO mapProfessional(ProfessionalProfile professional, AppUser usuario) {
        if (professional == null) {
            return null;
        }
        HistoryDTO.ProfessionalDTO dto = new HistoryDTO.ProfessionalDTO();
        AppUser professionalUser = professional.getUser();
        dto.setProfessionalId(professional.getId());
        dto.setFullName(professionalUser != null ? professionalUser.getFullName() : null);
        dto.setPhone(professionalUser != null ? professionalUser.getPhone() : null);
        dto.setProfessionalDetails(professional.getDescription());
        dto.setHourlyRate(professional.getHourlyRate());

        if (professionalUser != null && usuario != null && professionalUser.getId().equals(usuario.getId())) {
            Address address = professionalUser.getAddress();
            dto.setProfessionalAddress(address != null ? address.getFullAddress() : null);
        }

        return dto;
    }
}
