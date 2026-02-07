package es.duit.app.dto;

import lombok.Data;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;

// ============================================================================
// DTO PARA BÚSQUEDA DE OFERTAS - TRANSPORTA FILTROS DESDE EL FORMULARIO
// ============================================================================
@Data
public class SearchRequestDTO {

    @Size(max = 100, message = "El texto de búsqueda no puede superar 100 caracteres")
    private String textoBusqueda;

    @Min(value = 1, message = "El ID de categoría debe ser mayor que 0")
    private Long categoriaId;

    @Pattern(regexp = "^[0-9]{5}$|^$", message = "El código postal debe tener exactamente 5 dígitos")
    private String codigoPostal;
}