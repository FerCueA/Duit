package es.duit.app.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegistroDTO {

    @NotBlank(message = "El nombre es requerido")
    private String firstName;

    @NotBlank(message = "El apellido es requerido")
    @Size(max = 150, message = "El apellido no puede exceder los 150 caracteres")
    private String lastName;

    @NotBlank(message = "El correo es requerido")
    @Email(message = "El correo debe ser válido")
    private String email;

    @NotBlank(message = "El DNI es requerido")
    @Pattern(regexp = "^[0-9]{8}[A-Z]$", message = "El DNI debe tener 8 dígitos y una letra")
    private String dni;

    @NotBlank(message = "El teléfono es requerido")
    @Pattern(regexp = "^[+]?[0-9]{9,15}$", message = "El teléfono debe tener entre 9 y 15 dígitos, puede empezar con +")
    private String phone;

    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotBlank(message = "Debes seleccionar un tipo de usuario")
    @Pattern(regexp = "^(USER|PROFESSIONAL)$", message = "Tipo de usuario no válido")
    private String userType;
}
