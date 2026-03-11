package es.duit.app.dto;

import java.util.List;
import java.util.Set;

import lombok.Data;

import es.duit.app.entity.Category;
import es.duit.app.entity.ServiceRequest;

// ============================================================================
// DTO PARA DATOS DE LA PÁGINA DE BÚSQUEDA
// ============================================================================
@Data
public class SearchPageDataDTO {

    private List<ServiceRequest> ofertas;
    private List<Category> categorias;
    private Set<String> codigosPostales;
    private int totalOfertas;

    private Boolean missingProfessionalProfile;
    private Boolean missingAddress;

    private SearchRequestDTO filtrosAplicados;
}
