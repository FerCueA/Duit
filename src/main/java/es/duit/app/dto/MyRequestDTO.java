package es.duit.app.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

// ============================================================================
// ES UN DTO DE SOLO LECTURA PARA LISTAR SOLICITUDES
// ============================================================================

@Data
public class MyRequestDTO {
    // Solo datos básicos de la entidad
    private Long id;
    private String title;
    private String description;
    private String categoryName;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime deadline;
    private String serviceAddress;
    private BigDecimal expectedPrice;
}
