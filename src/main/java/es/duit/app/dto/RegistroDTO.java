package es.duit.app.dto;

import jakarta.validation.constraints.*;

public record RegistroDTO(
    @NotBlank(message = "El nombre es requerido")
    String firstName,

    @NotBlank(message = "El apellido es requerido")
    String lastName,

    @NotBlank(message = "El correo es requerido")
    @Email(message = "El correo debe ser válido")
    String email,

    @NotBlank(message = "El DNI es requerido")
    @Pattern(regexp = "^[0-9]{8}[A-Z]$", message = "El DNI debe tener 8 dígitos y una letra")
    String dni,

    @NotBlank(message = "El teléfono es requerido")
    @Pattern(regexp = "^[0-9]{9}$", message = "El teléfono debe tener 9 dígitos")
    String phone,

    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    String password,

    @NotBlank(message = "Debes seleccionar un tipo de usuario")
    @Pattern(regexp = "^(USER|PROFESSIONAL)$", message = "Tipo de usuario no válido")
    String userType
) {
}
