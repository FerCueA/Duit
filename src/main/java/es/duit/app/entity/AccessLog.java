package es.duit.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "access_log", indexes = {
        @Index(name = "idx_access_log_user", columnList = "id_user"),
        @Index(name = "idx_access_log_accessed_at", columnList = "accessed_at")
})
public class AccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_log")
    private Long id;

    @Column(name = "accessed_at")
    private LocalDateTime accessedAt;

    @Size(max = 45, message = "La IP no puede exceder los 45 caracteres")
    @Column(name = "source_ip", length = 45)
    private String sourceIp;

    @NotNull
    @Column(name = "success", nullable = false)
    private Boolean success = false;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_user", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private AppUser user;

    @PrePersist
    protected void onCreate() {
        if (accessedAt == null) {
            accessedAt = LocalDateTime.now();
        }
    }
}
