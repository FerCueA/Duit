package es.duit.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "service_request", indexes = {
        @Index(name = "idx_request_category", columnList = "id_category"),
        @Index(name = "idx_request_client", columnList = "id_client"),
        @Index(name = "idx_request_status", columnList = "status"),
        @Index(name = "idx_request_service_address", columnList = "id_service_address")
})
public class ServiceRequest extends BaseEntity {

    public enum Status {
        DRAFT, PUBLISHED, IN_PROGRESS, COMPLETED, CANCELLED, EXPIRED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_request")
    private Long id;

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 5, max = 150, message = "El título debe tener entre 5 y 150 caracteres")
    @Column(name = "title", length = 150, nullable = false)
    private String title;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 20, max = 2000, message = "La descripción debe tener entre 20 y 2000 caracteres")
    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "deadline")
    private LocalDateTime deadline;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private Status status = Status.DRAFT;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_client", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private AppUser client;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_category", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "id_service_address")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Address serviceAddress;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "request", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobApplication> applications = new ArrayList<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "request", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServiceJob> jobs = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        super.onCreate();
        if (requestedAt == null) {
            requestedAt = LocalDateTime.now();
        }
    }

    public boolean isActive() {
        return status == Status.PUBLISHED || status == Status.IN_PROGRESS;
    }

    public int getApplicationCount() {
        return applications != null ? applications.size() : 0;
    }

    public Address getEffectiveServiceAddress() {
        return serviceAddress != null ? serviceAddress : (client != null ? client.getAddress() : null);
    }

    public boolean hasSpecificServiceAddress() {
        return serviceAddress != null;
    }
}