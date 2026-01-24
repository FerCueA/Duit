package es.duit.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "rating")
public class Rating extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rating")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_job", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_sender", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private AppUser sender;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_receiver", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private AppUser receiver;

    @Column(name = "type")
    private Short type;

    @Column(name = "score")
    private Short score;

    @Column(name = "comment", length = 300)
    private String comment;

    @Column(name = "rated_at")
    private LocalDateTime ratedAt;

    @Column(name = "status")
    private Short status;
}

