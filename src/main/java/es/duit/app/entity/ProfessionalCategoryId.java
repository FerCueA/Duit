package es.duit.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class ProfessionalCategoryId implements Serializable {

    @Column(name = "id_professional")
    private Long professionalId;

    @Column(name = "id_category")
    private Long categoryId;
}

