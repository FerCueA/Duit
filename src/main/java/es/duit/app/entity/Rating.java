package es.duit.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "rating", indexes = {
        @Index(name = "idx_rating_job", columnList = "id_job"),
        @Index(name = "idx_rating_type", columnList = "type")
})
public class Rating extends BaseEntity {

    public enum Type {
        CLIENT_TO_PROFESSIONAL, PROFESSIONAL_TO_CLIENT
    }

    public enum Status {
        PENDING, PUBLISHED, HIDDEN
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rating")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_job", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ServiceJob job;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 30, nullable = false)
    private Type type;

    @NotNull
    @Min(value = 1, message = "La puntuación mínima es 1")
    @Max(value = 5, message = "La puntuación máxima es 5")
    @Column(name = "score", nullable = false)
    private Integer score;

    @Size(max = 500, message = "El comentario no puede exceder los 500 caracteres")
    @Column(name = "comment", length = 500)
    private String comment;

    @Column(name = "rated_at")
    private LocalDateTime ratedAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.PENDING;

    @PrePersist
    protected void onCreate() {
        if (ratedAt == null) {
            ratedAt = LocalDateTime.now();
        }
    }

    public boolean isPositive() {
        return score >= 4;
    }

    public AppUser getSender() {
        if (job == null)
            return null;
        return type == Type.CLIENT_TO_PROFESSIONAL ? job.getRequest().getClient()
                : job.getApplication().getProfessional().getUser();
    }

    public AppUser getReceiver() {
        if (job == null)
            return null;
        return type == Type.CLIENT_TO_PROFESSIONAL ? job.getApplication().getProfessional().getUser()
                : job.getRequest().getClient();
    }
}
