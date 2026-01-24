package es.duit.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "application")
public class Application extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_application")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_request", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Request request;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_professional", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ProfessionalProfile professional;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "proposed_price", precision = 8, scale = 2)
    private BigDecimal proposedPrice;

    @Column(name = "applied_at")
    private LocalDateTime appliedAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Column(name = "status")
    private Short status;
}
