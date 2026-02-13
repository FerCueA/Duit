package es.duit.app.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.duit.app.dto.RegistroDTO;
import es.duit.app.entity.AppUser;
import es.duit.app.entity.UserRole;
import es.duit.app.repository.AppUserRepository;
import es.duit.app.repository.RoleRepository;
import lombok.RequiredArgsConstructor;

// ============================================================================
// SERVICIO DE REGISTRO - GESTIONA EL REGISTRO DE NUEVOS USUARIOS
// ============================================================================
@Service
@Transactional
@RequiredArgsConstructor
public class RegistroService {

    private final AppUserRepository appUserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // ============================================================================
    // REGISTRA UN NUEVO USUARIO CON VALIDACIONES
    // ============================================================================
    public AppUser registerUser(RegistroDTO registro) {
        try {
            // Normalizar y validar email antes de cualquier operacion
            String emailNormalizado = (registro.getEmail() != null) ? registro.getEmail().trim().toLowerCase() : "";
            if (emailNormalizado.isEmpty()) {
                throw new IllegalArgumentException("El correo electrónico es obligatorio");
            }

            // Validar que el email no esté registrado en el sistema
            if (appUserRepository.findByUsername(emailNormalizado).isPresent()) {
                throw new IllegalArgumentException("Este correo electrónico ya está registrado");
            }

            String dniNormalizado = (registro.getDni() != null) ? registro.getDni().trim().toUpperCase() : "";
            if (dniNormalizado.isEmpty()) {
                throw new IllegalArgumentException("El DNI es obligatorio");
            }

            // Validar que el DNI no esté registrado en el sistema
            if (appUserRepository.findByDni(dniNormalizado).isPresent()) {
                throw new IllegalArgumentException("Este DNI ya está registrado");
            }

            // Obtener el rol según el tipo de usuario (USER o PROFESSIONAL)
            String tipoUsuario = (registro.getUserType() != null) ? registro.getUserType().trim().toUpperCase() : "";
            UserRole.RoleName nombreRol = "USER".equals(tipoUsuario)
                    ? UserRole.RoleName.USER
                    : UserRole.RoleName.PROFESSIONAL;

            // Buscar el rol en la BD
            UserRole rol = roleRepository.findByName(nombreRol)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Rol " + nombreRol + " no encontrado en la base de datos"));

            // Crear nueva instancia de usuario y asignar datos del formulario
            AppUser usuarioNuevo = new AppUser();
            String nombre = (registro.getFirstName() != null) ? registro.getFirstName().trim() : "";
            String apellidos = (registro.getLastName() != null) ? registro.getLastName().trim() : "";
            String telefonoNormalizado = (registro.getPhone() != null) ? registro.getPhone().trim() : "";

            usuarioNuevo.setFirstName(nombre);
            usuarioNuevo.setLastName(apellidos);
            usuarioNuevo.setDni(dniNormalizado);
            usuarioNuevo.setUsername(emailNormalizado);

            // Encriptar la contraseña antes de guardar
            usuarioNuevo.setPassword(passwordEncoder.encode(registro.getPassword()));
            usuarioNuevo.setPhone(telefonoNormalizado);
            usuarioNuevo.setRole(rol);
            usuarioNuevo.setActive(true);

            // Guardar usuario en la BD y devolver
            return appUserRepository.save(usuarioNuevo);

        } catch (Exception e) {
            System.err.println("Error en RegistroService.registerUser: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-lanza la excepción para que llegue al controlador
        }
    }
}
