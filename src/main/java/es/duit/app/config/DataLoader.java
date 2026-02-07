package es.duit.app.config;

import es.duit.app.entity.AppUser;
import es.duit.app.entity.UserRole;
import es.duit.app.repository.AppUserRepository;
import es.duit.app.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// ============================================================================
//  CLASE DE CARGA DE DATOS INICIALES - SE EJECUTA AL INICIAR LA APLICACIÓN
// ============================================================================
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Solo inicializar roles si la tabla está completamente vacía
        long rolesCount = roleRepository.count();

        // Si no hay roles en la base de datos, inicializarlos
        if (rolesCount == 0) {
            initializeRoles();
        }

        // Verificar y crear usuario admin
        initializeAdminUser();
    }

    // ============================================================================
    // INICIALIZA LOS ROLES BÁSICOS EN LA BASE DE DATOS SI NO EXISTEN
    // ============================================================================
    private void initializeRoles() {
        // Crear rol ADMIN si no existe
        if (roleRepository.findByName(UserRole.RoleName.ADMIN).isEmpty()) {
            UserRole adminRole = new UserRole();
            adminRole.setName(UserRole.RoleName.ADMIN);
            adminRole.setDescription("Administrador del sistema");
            adminRole.setActive(true);
            roleRepository.save(adminRole);
        }

        // Crear rol USER si no existe
        if (roleRepository.findByName(UserRole.RoleName.USER).isEmpty()) {
            UserRole userRole = new UserRole();
            userRole.setName(UserRole.RoleName.USER);
            userRole.setDescription("Usuario estándar - Cliente");
            userRole.setActive(true);
            roleRepository.save(userRole);
        }

        // Crear rol PROFESSIONAL si no existe
        if (roleRepository.findByName(UserRole.RoleName.PROFESSIONAL).isEmpty()) {
            UserRole professionalRole = new UserRole();
            professionalRole.setName(UserRole.RoleName.PROFESSIONAL);
            professionalRole.setDescription("Usuario profesional - Proveedor de servicios");
            professionalRole.setActive(true);
            roleRepository.save(professionalRole);
        }

    }

    // ============================================================================
    // INICIALIZA UN USUARIO ADMINISTRADOR SI NO EXISTE
    // ============================================================================
    private void initializeAdminUser() {
        String adminEmail = "admin@duit.es";

        // Verificar si ya existe el usuario admin
        if (appUserRepository.findByUsername(adminEmail).isEmpty()) {
            try {
                // Buscar el rol ADMIN
                UserRole adminRole = roleRepository.findByName(UserRole.RoleName.ADMIN)
                        .orElseThrow(() -> new RuntimeException("Rol ADMIN no encontrado"));

                // Crear usuario administrador
                AppUser adminUser = new AppUser();
                adminUser.setFirstName("Administrador");
                adminUser.setLastName("Sistema");
                adminUser.setDni("00000000A");
                adminUser.setUsername(adminEmail);
                adminUser.setPassword(passwordEncoder.encode("1234"));
                adminUser.setPhone("+34600000000");
                adminUser.setRole(adminRole);
                adminUser.setActive(true);

                appUserRepository.save(adminUser);

            } catch (Exception e) {
                // Error inesperado al crear el usuario admin, imprimir detalles para
                // diagnóstico
                System.err.println("Error al crear usuario admin: " + e.getMessage());
                e.printStackTrace();
            }
        }

    }
}