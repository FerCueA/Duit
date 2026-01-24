package es.duit.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "professional_category")
public class ProfessionalCategory extends BaseEntity {

    @EmbeddedId
    private ProfessionalCategoryId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("professionalId")
    @JoinColumn(name = "id_professional", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ProfessionalProfile professional;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("categoryId")
    @JoinColumn(name = "id_category", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Category category;
}
