package es.duit.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "service_job", indexes = {
        @Index(name = "idx_job_status", columnList = "status"),
        @Index(name = "idx_job_request", columnList = "id_request"),
        @Index(name = "idx_job_application", columnList = "id_application"),
        @Index(name = "idx_job_dates", columnList = "start_date, end_date"),
        @Index(name = "idx_job_active", columnList = "status, start_date"),
        @Index(name = "idx_job_price", columnList = "agreed_price")
})
public class ServiceJob extends BaseEntity {

    public enum Status {
        CREATED, IN_PROGRESS, COMPLETED, CANCELLED, PAUSED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_job")
    private Long id;

    @NotNull
    @DecimalMin(value = "0.00", message = "El precio acordado debe ser positivo")
    @Column(name = "agreed_price", precision = 8, scale = 2, nullable = false)
    private BigDecimal agreedPrice;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Size(max = 2000, message = "Las notas no pueden exceder los 2000 caracteres")
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.CREATED;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_request", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ServiceRequest request;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_application", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private JobApplication application;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "job", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings;

    @PrePersist
    protected void onCreate() {
        if (startDate == null) {
            startDate = LocalDateTime.now();
        }
    }

    public boolean isActive() {
        return status == Status.CREATED || status == Status.IN_PROGRESS;
    }

    public boolean isCompleted() {
        return status == Status.COMPLETED;
    }

    public AppUser getClient() {
        return request != null ? request.getClient() : null;
    }

    public AppUser getProfessional() {
        return application != null && application.getProfessional() != null ? application.getProfessional().getUser()
                : null;
    }
}