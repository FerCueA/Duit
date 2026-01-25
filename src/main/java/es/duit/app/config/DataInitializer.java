package es.duit.app.config;

import es.duit.app.entity.AppUser;
import es.duit.app.entity.UserRole;
import es.duit.app.repository.AppUserRepository;
import es.duit.app.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Inicialización de datos por defecto para la aplicación.
 * Se ejecuta automáticamente al arrancar la aplicación.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Iniciando inicialización de datos...");

        createDefaultRoles();

        createDefaultAdminUser();

        logger.info("Inicialización de datos completada.");
    }

    private void createDefaultRoles() {
        logger.info("Verificando roles por defecto...");

        createRoleIfNotExists(UserRole.RoleName.ADMIN, "Administrador del sistema", true);
        createRoleIfNotExists(UserRole.RoleName.USER, "Usuario regular", true);
        createRoleIfNotExists(UserRole.RoleName.PROFESSIONAL, "Profesional prestador de servicios", true);
        createRoleIfNotExists(UserRole.RoleName.MODERATOR, "Moderador de contenido", true);

        logger.info("Roles verificados correctamente.");
    }

    private void createRoleIfNotExists(UserRole.RoleName roleName, String description, boolean active) {
        if (!roleRepository.findByName(roleName).isPresent()) {
            UserRole role = new UserRole();
            role.setName(roleName);
            role.setDescription(description);
            role.setActive(active);
            role.setCreatedBy("system");
            role.setCreatedAt(LocalDateTime.now());
            role.setUpdatedBy("system");
            role.setUpdatedAt(LocalDateTime.now());

            roleRepository.save(role);
            logger.info("Rol creado: {}", roleName);
        } else {
            logger.debug("Rol ya existe: {}", roleName);
        }
    }

    private void createDefaultAdminUser() {
        logger.info("Verificando usuario administrador por defecto...");

        String adminEmail = "admin@duit.es";

        if (!appUserRepository.findByUsername(adminEmail).isPresent()) {

            UserRole adminRole = roleRepository.findByName(UserRole.RoleName.ADMIN)
                    .orElseThrow(() -> new RuntimeException("No se encontró el rol ADMIN"));

            AppUser adminUser = new AppUser();
            adminUser.setFirstName("Administrador");
            adminUser.setLastName("Sistema");
            adminUser.setDni("12345678A");
            adminUser.setUsername(adminEmail); // username = email (nuestra estrategia)
            adminUser.setPassword(passwordEncoder.encode("1234"));
            adminUser.setPhone("600000000");
            adminUser.setRole(adminRole);
            adminUser.setActive(true);
            adminUser.setRegisteredAt(LocalDateTime.now());
            adminUser.setCreatedBy("system");
            adminUser.setCreatedAt(LocalDateTime.now());
            adminUser.setUpdatedBy("system");
            adminUser.setUpdatedAt(LocalDateTime.now());

            appUserRepository.save(adminUser);

            logger.info("Usuario administrador creado:");
            logger.info("  - Username/Email: admin@duit.es");
            logger.info("  - Password: 1234");
            logger.info("  - Rol: ADMIN");

        } else {
            logger.info("Usuario administrador ya existe: {}", adminEmail);
        }
    }
}