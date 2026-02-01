package es.duit.app.service;

import es.duit.app.dto.RegistroDTO;
import es.duit.app.entity.AppUser;
import es.duit.app.entity.UserRole;
import es.duit.app.repository.AppUserRepository;
import es.duit.app.repository.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// ============================================================================
// SERVICIO DE REGISTRO - GESTIONA EL REGISTRO DE NUEVOS USUARIOS
// ============================================================================
@Service
@Transactional
public class RegistroService {

    private final AppUserRepository appUserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistroService(AppUserRepository appUserRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ============================================================================
    // REGISTRA UN NUEVO USUARIO CON VALIDACIONES
    // ============================================================================
    public AppUser registerUser(RegistroDTO registro) {
        // Validar que el email no esté registrado en el sistema
        if (appUserRepository.findByUsername(registro.email()).isPresent()) {
            throw new IllegalArgumentException("Este correo electrónico ya está registrado");
        }

        // Validar que el DNI no esté registrado en el sistema
        if (appUserRepository.findByDni(registro.dni()).isPresent()) {
            throw new IllegalArgumentException("Este DNI ya está registrado");
        }

        // Obtener el rol según el tipo de usuario (USER o PROFESSIONAL)
        UserRole.RoleName nombreRol = "USER".equals(registro.userType())
                ? UserRole.RoleName.USER
                : UserRole.RoleName.PROFESSIONAL;

        // Buscar el rol en la BD
        UserRole rol = roleRepository.findByName(nombreRol)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado en la base de datos"));

        // Crear nueva instancia de usuario y asignar datos del formulario
        AppUser usuarioNuevo = new AppUser();
        usuarioNuevo.setFirstName(registro.firstName().trim());
        usuarioNuevo.setLastName(registro.lastName().trim());
        usuarioNuevo.setDni(registro.dni().trim().toUpperCase());
        usuarioNuevo.setUsername(registro.email().trim());
        
        // Encriptar la contraseña antes de guardar
        usuarioNuevo.setPassword(passwordEncoder.encode(registro.password()));
        usuarioNuevo.setPhone(registro.phone().trim());
        usuarioNuevo.setRole(rol);
        usuarioNuevo.setActive(true);

        // Guardar usuario en la BD y devolver
        return appUserRepository.save(usuarioNuevo);
    }
}

