package es.duit.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "professional_profile")
public class ProfessionalProfile extends BaseEntity {

    @Id
    @Column(name = "id_professional")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "id_professional")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private AppUser user;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "hourly_rate", precision = 8, scale = 2)
    private BigDecimal hourlyRate;

    @Column(name = "nif", length = 9)
    private String nif;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "professional", fetch = FetchType.LAZY)
    private List<Application> applications;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "professional", fetch = FetchType.LAZY)
    private List<ProfessionalCategory> categories;
}
