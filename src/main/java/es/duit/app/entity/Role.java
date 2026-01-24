package es.duit.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "role")
public class Role extends BaseEntity {

    public enum RoleName {
        ADMIN, USER, PROFESSIONAL, MODERATOR
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_role")
    private Long id;

    @NotBlank(message = "El nombre del rol es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Column(name = "name", length = 50, nullable = false, unique = true)
    private String name;

    @Size(max = 100, message = "La descripción no puede exceder los 100 caracteres")
    @Column(name = "description", length = 100)
    private String description;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private List<AppUser> users = new ArrayList<>();

    // Método de utilidad para verificar si el rol está activo
    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }

    // Método de utilidad para verificar si es rol de administrador
    public boolean isAdmin() {
        return RoleName.ADMIN.name().equalsIgnoreCase(name);
    }

    // Método de utilidad para verificar si es rol de profesional
    public boolean isProfessional() {
        return RoleName.PROFESSIONAL.name().equalsIgnoreCase(name);
    }

    // Método para obtener el número de usuarios con este rol
    public int getUsersCount() {
        return users != null ? users.size() : 0;
    }
}
