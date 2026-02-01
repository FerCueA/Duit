package es.duit.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user_role", indexes = {
        @Index(name = "idx_role_name", columnList = "name"),
        @Index(name = "idx_role_active", columnList = "active")
})
public class UserRole extends BaseEntity {

    public enum RoleName {
        ADMIN, USER, PROFESSIONAL, MODERATOR
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_role")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "name", length = 20, nullable = false, unique = true)
    private RoleName name;

    @Size(max = 100, message = "La descripción no puede exceder los 100 caracteres")
    @Column(name = "description", length = 100)
    private String description;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private List<AppUser> users = new ArrayList<>();

    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }

    public boolean isAdmin() {
        return RoleName.ADMIN.equals(name);
    }

    public boolean isProfessional() {
        return RoleName.PROFESSIONAL.equals(name);
    }

    public int getUsersCount() {
        return users != null ? users.size() : 0;
    }
}