package es.duit.app.dto;

import es.duit.app.entity.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryDTO {

    private Long id;

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String name;

    @Size(max = 200, message = "La descripción no puede exceder los 200 caracteres")
    private String description;

    private Boolean active = true;

    public CategoryDTO() {
    }

    public CategoryDTO(Category category) {
        if (category != null) {
            this.id = category.getId();
            this.name = category.getName();
            this.description = category.getDescription();
            this.active = category.getActive();
        }
    }

    // Método para convertir a entidad
    public Category toEntity() {
        Category category = new Category();
        category.setId(this.id);
        category.setName(this.name);
        category.setDescription(this.description);
        category.setActive(this.active);
        return category;
    }

    public String getStatusText() {
        return Boolean.TRUE.equals(active) ? "Activa" : "Inactiva";
    }

    public String getStatusClass() {
        return Boolean.TRUE.equals(active) ? "text-success" : "text-danger";
    }
}