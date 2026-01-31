package es.duit.app.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CrearSolicitudDTO {

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 5, max = 150, message = "El título debe tener entre 5 y 150 caracteres")
    private String title;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 20, max = 2000, message = "La descripción debe tener entre 20 y 2000 caracteres")
    private String description;

    @NotNull(message = "Debes seleccionar una categoría")
    private Long categoryId;

    @FutureOrPresent(message = "La fecha límite no puede ser en el pasado")
    private LocalDate deadline;

    @NotBlank(message = "Debes indicar dónde se realizará el servicio")
    private String addressOption;

    // Campos de nueva dirección si se elige "new"
    @Size(max = 255, message = "La dirección no puede superar los 255 caracteres")
    private String address;
    
    @Size(max = 100, message = "La ciudad no puede superar los 100 caracteres")
    private String city;
    
    @Size(max = 10, message = "El código postal no puede superar los 10 caracteres")
    private String postalCode;
    
    @Size(max = 100, message = "La provincia no puede superar los 100 caracteres")
    private String province;
    
    @Size(max = 100, message = "El país no puede superar los 100 caracteres")
    private String country;

    private Long editId;

    // Verifica si la nueva dirección es válida
    public boolean isValidNewAddress() {
        if (!"new".equals(addressOption)) {
            return true;
        }

        if (address == null || address.trim().isEmpty()) {
            return false;
        }
        if (city == null || city.trim().isEmpty()) {
            return false;
        }
        if (postalCode == null || postalCode.trim().isEmpty()) {
            return false;
        }
        if (province == null || province.trim().isEmpty()) {
            return false;
        }
        if (country == null || country.trim().isEmpty()) {
            return false;
        }

        return true;
    }

    // Verifica si se está editando una solicitud existente
    public boolean isEditing() {
        return editId != null;
    }
}