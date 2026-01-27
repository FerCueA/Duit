package es.duit.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "category", indexes = {
        @Index(name = "idx_category_name", columnList = "name"),
        @Index(name = "idx_category_active", columnList = "active")
})
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_category")
    private Long id;

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(name = "name", length = 100, nullable = false, unique = true)
    private String name;

    @Size(max = 200, message = "La descripción no puede exceder los 200 caracteres")
    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<ServiceRequest> requests = new ArrayList<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
    private Set<ProfessionalProfile> professionals = new HashSet<>();

    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }

}
