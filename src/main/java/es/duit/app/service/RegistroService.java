package es.duit.app.service;

import es.duit.app.dto.RegistroDTO;
import es.duit.app.entity.AppUser;
import es.duit.app.entity.UserRole;
import es.duit.app.repository.AppUserRepository;
import es.duit.app.repository.RoleRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class RegistroService {

    private final AppUserRepository appUserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;
    private final EmailService emailService;

    public RegistroService(AppUserRepository appUserRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            Validator validator,
            EmailService emailService) {
        this.appUserRepository = appUserRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
        this.emailService = emailService;
    }

    public AppUser registrarUsuario(RegistroDTO registro) {
        // Validar que el email no esté registrado
        if (appUserRepository.findByUsername(registro.email()).isPresent()) {
            throw new IllegalArgumentException("Este correo electrónico ya está registrado");
        }

        // Validar que el DNI no esté registrado
        if (appUserRepository.findByDni(registro.dni()).isPresent()) {
            throw new IllegalArgumentException("Este DNI ya está registrado");
        }

        // Obtener el rol según el tipo de usuario
        UserRole.RoleName nombreRol = "USER".equals(registro.userType())
                ? UserRole.RoleName.USER
                : UserRole.RoleName.PROFESSIONAL;

        UserRole rol = roleRepository.findByName(nombreRol)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado en la base de datos"));

        // Crear el nuevo usuario
        AppUser usuarioNuevo = new AppUser();
        usuarioNuevo.setFirstName(registro.firstName().trim());
        usuarioNuevo.setLastName(registro.lastName().trim());
        usuarioNuevo.setDni(registro.dni().trim().toUpperCase());
        usuarioNuevo.setUsername(registro.email().trim());
        usuarioNuevo.setPassword(passwordEncoder.encode(registro.password()));
        usuarioNuevo.setPhone(registro.phone().trim());
        usuarioNuevo.setRole(rol);
        usuarioNuevo.setActive(false); // Usuario inactivo hasta activación por email

        // Generar token de activación
        String tokenActivacion = UUID.randomUUID().toString();
        usuarioNuevo.setActivationToken(tokenActivacion);
        usuarioNuevo.setActivationTokenExpires(LocalDateTime.now().plusHours(24));

        // Validar la entidad antes de guardar (Bean Validation)
        Set<ConstraintViolation<AppUser>> violations = validator.validate(usuarioNuevo);
        if (!violations.isEmpty()) {
            ConstraintViolation<AppUser> firstViolation = violations.iterator().next();
            throw new IllegalArgumentException(firstViolation.getMessage());
        }

        try {
            AppUser usuarioGuardado = appUserRepository.save(usuarioNuevo);
            // Enviar email de activación
            emailService.enviarEmailActivacion(
                    usuarioGuardado.getUsername(),
                    usuarioGuardado.getFirstName(),
                    tokenActivacion);

            return usuarioGuardado;
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Error inesperado al guardar el usuario: " + e.getMessage());
        }
    }

    public boolean activarCuenta(String token) {
        AppUser usuario = appUserRepository.findByActivationToken(token).orElse(null);

        if (usuario == null) {
            return false;
        }

        if (usuario.getActivationTokenExpires() == null ||
                usuario.getActivationTokenExpires().isBefore(LocalDateTime.now())) {
            return false;
        }

        if (usuario.getActive()) {
            return false;
        }

        // Activar usuario
        usuario.setActive(true);
        usuario.setActivationToken(null);
        usuario.setActivationTokenExpires(null);
        appUserRepository.save(usuario);

        return true;
    }
}
