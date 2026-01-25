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
@Table(name = "app_user", indexes = {
        @Index(name = "idx_user_username", columnList = "username"),
        @Index(name = "idx_user_dni", columnList = "dni"),
        @Index(name = "idx_user_active", columnList = "active"),
        @Index(name = "idx_user_role", columnList = "id_role")
})
public class AppUser extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(name = "first_name", length = 100, nullable = false)
    private String firstName;

    @Size(max = 150, message = "El apellido no puede exceder los 150 caracteres")
    @Column(name = "last_name", length = 150)
    private String lastName;

    @Pattern(regexp = "^[0-9]{8}[A-Z]$", message = "El DNI debe tener el formato correcto (8 dígitos + letra)")
    @Column(name = "dni", length = 9, unique = true)
    private String dni;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo electrónico debe tener un formato válido")
    @Size(max = 100, message = "El correo electrónico no puede exceder los 100 caracteres")
    @Column(name = "username", length = 100, nullable = false, unique = true)
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 60, max = 255, message = "La contraseña encriptada debe tener la longitud correcta")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Pattern(regexp = "^[+]?[0-9]{9,15}$", message = "El teléfono debe tener entre 9 y 15 dígitos, puede empezar con +")
    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @NotNull(message = "El rol es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_role", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_address")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Address address;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ProfessionalProfile professionalProfile;

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<ServiceRequest> requests = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<AccessLog> accessLogs = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (registeredAt == null) {
            registeredAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
    }

    public String getFullName() {
        return lastName != null ? firstName + " " + lastName : firstName;
    }

    public boolean isProfessional() {
        return professionalProfile != null;
    }

    public String getEmail() {
        return username;
    }

    public String getDisplayName() {
        return (firstName != null && !firstName.trim().isEmpty()) ? getFullName() : username;
    }

    public boolean isAdmin() {
        return role != null && role.isAdmin();
    }

}