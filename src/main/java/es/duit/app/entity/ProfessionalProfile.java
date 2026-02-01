package es.duit.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "professional_profile", indexes = {
        @Index(name = "idx_professional_nif", columnList = "nif"),
        @Index(name = "idx_professional_hourly_rate", columnList = "hourly_rate")
})
public class ProfessionalProfile extends BaseEntity {

    @Id
    @Column(name = "id_professional")
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "id_professional")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private AppUser user;

    @NotBlank(message = "La descripción profesional es obligatoria")
    @Size(min = 50, max = 2000, message = "La descripción debe tener entre 50 y 2000 caracteres")
    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @NotNull(message = "La tarifa por hora es obligatoria")
    @DecimalMin(value = "5.00", message = "La tarifa por hora mínima es 5€")
    @DecimalMax(value = "500.00", message = "La tarifa por hora máxima es 500€")
    @Column(name = "hourly_rate", precision = 8, scale = 2, nullable = false)
    private BigDecimal hourlyRate;

    @NotBlank(message = "El NIF es obligatorio para profesionales")
    @Pattern(regexp = "^[0-9]{8}[A-Z]$", message = "El NIF debe tener el formato correcto (8 dígitos + letra)")
    @Column(name = "nif", length = 9, unique = true, nullable = false)
    private String nif;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "professional", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobApplication> applications = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "professional_category", joinColumns = @JoinColumn(name = "id_professional"), inverseJoinColumns = @JoinColumn(name = "id_category"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Category> categories = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        if (registeredAt == null) {
            registeredAt = LocalDateTime.now();
        }
    }

}
