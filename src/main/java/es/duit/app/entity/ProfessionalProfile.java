package es.duit.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "professional_profile")
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

    @Size(min = 50, max = 2000, message = "La descripción debe tener entre 50 y 2000 caracteres")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @DecimalMin(value = "5.00", message = "La tarifa por hora mínima es 5€")
    @DecimalMax(value = "500.00", message = "La tarifa por hora máxima es 500€")
    @Column(name = "hourly_rate", precision = 8, scale = 2)
    private BigDecimal hourlyRate;

    @Pattern(regexp = "^[0-9]{8}[A-Z]$", message = "El NIF debe tener el formato correcto (8 dígitos + letra)")
    @Column(name = "nif", length = 9, unique = true)
    private String nif;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "professional", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobApplication> applications = new ArrayList<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "professional", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProfessionalCategory> categories = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (registeredAt == null) {
            registeredAt = LocalDateTime.now();
        }
    }

    public boolean isProfileComplete() {
        return description != null && !description.trim().isEmpty() &&
               hourlyRate != null &&
               nif != null && !nif.trim().isEmpty() &&
               categories != null && !categories.isEmpty();
    }

    public int getCategoriesCount() {
        return categories != null ? categories.size() : 0;
    }

    public double getAverageRating() {
        if (user == null || user.getRatingsReceived() == null) {
            return 0.0;
        }
        return user.getRatingsReceived().stream()
                .filter(rating -> rating.getStatus() == Rating.Status.PUBLISHED)
                .mapToInt(Rating::getScore)
                .average()
                .orElse(0.0);
    }
}
