package es.duit.app.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

// ============================================================================
// ES UN DTO DE SOLO LECTURA PARA MOSTAR EL HISTORIAL DE TRABAJOS DE UN USUARIO (CLIENTE O PROFESIONAL)
// ============================================================================

@Data
public class HistoryDTO {

    private Long jobId;
    private String jobStatus;
    private BigDecimal agreedPrice;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private RequestDTO request;
    private ApplicationDTO application;
    private ClientDTO client;
    private ProfessionalDTO professional;

    @Data
    public static class RequestDTO {
        private Long requestId;
        private String title;
        private String description;
        private String categoryName;
        private LocalDateTime requestedAt;
        private LocalDateTime deadline;
        private String serviceAddress;
    }

    @Data
    public static class ApplicationDTO {
        private Long applicationId;
        private String status;
        private String message;
        private BigDecimal proposedPrice;
        private LocalDateTime appliedAt;
        private LocalDateTime respondedAt;
    }

    @Data
    public static class ClientDTO {
        private Long clientId;
        private String fullName;
        private String email;
        private String phone;
    }

    @Data
    public static class ProfessionalDTO {
        private Long professionalId;
        private String fullName;
        private String phone;
        private String professionalDetails;
        private BigDecimal hourlyRate;
        private String professionalAddress;
    }

}
