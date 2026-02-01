package es.duit.app.config;

import es.duit.app.entity.AppUser;
import es.duit.app.entity.UserRole;
import es.duit.app.repository.AppUserRepository;
import es.duit.app.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

// ============================================================================
// ESTA CLASE CREA DATOS BÁSICOS (ROLES Y ADMIN) AL ARRANCAR LA APLICACIÓN
// ============================================================================
// @Component  // DESACTIVADO - descomentar si necesitas crear datos automáticamente
public class DataInitializer implements ApplicationRunner {

    // PASO 1: Inyectar repositorios y servicios necesarios
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // PASO 2: Este método se ejecuta automáticamente al arrancar la aplicación
    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        // ====== PASO A: ESPERAR A QUE SE CREEN LAS TABLAS ======
        Thread.sleep(2000);

        // ====== PASO B: CREAR LOS ROLES ======
        crearRoles();

        // ====== PASO C: CREAR EL USUARIO ADMINISTRADOR ======
        crearUsuarioAdmin();
    }

    // ============================================================================
    // CREA TODOS LOS ROLES QUE NECESITA LA APLICACIÓN
    // ============================================================================
    private void crearRoles() {
        crearRolSiNoExiste(UserRole.RoleName.ADMIN, "Administrador del sistema", true);
        crearRolSiNoExiste(UserRole.RoleName.USER, "Usuario regular", true);
        crearRolSiNoExiste(UserRole.RoleName.PROFESSIONAL, "Profesional prestador de servicios", true);
        crearRolSiNoExiste(UserRole.RoleName.MODERATOR, "Moderador de contenido", true);
    }

    // ============================================================================
    // CREA UN ROL INDIVIDUAL SI NO EXISTE YA EN LA BD
    // ============================================================================
    private void crearRolSiNoExiste(UserRole.RoleName nombreRol, String descripcion, boolean activo) {
        // ====== PASO A: BUSCAR SI EL ROL YA EXISTE ======
        var rolExistente = roleRepository.findByName(nombreRol);

        // ====== PASO B: SI EXISTE, NO HACER NADA ======
        if (rolExistente.isPresent()) {
            return;
        }

        // ====== PASO C: SI NO EXISTE, CREAR NUEVO ROL ======
        UserRole nuevoRol = new UserRole();
        nuevoRol.setName(nombreRol);
        nuevoRol.setDescription(descripcion);
        nuevoRol.setActive(activo);
        nuevoRol.setCreatedBy("system");
        nuevoRol.setCreatedAt(LocalDateTime.now());
        nuevoRol.setUpdatedBy("system");
        nuevoRol.setUpdatedAt(LocalDateTime.now());

        // ====== PASO D: GUARDAR EL ROL EN LA BD ======
        roleRepository.save(nuevoRol);
    }

    // ============================================================================
    // CREA EL USUARIO ADMINISTRADOR POR DEFECTO
    // ============================================================================
    private void crearUsuarioAdmin() {
        String emailAdmin = "admin@duit.es";

        // ====== PASO A: VERIFICAR SI EL ADMIN YA EXISTE ======
        var usuarioExistente = appUserRepository.findByUsername(emailAdmin);
        if (usuarioExistente.isPresent()) {
            return;  // Ya existe, no hacer nada
        }

        // ====== PASO B: OBTENER EL ROL ADMIN DE LA BD ======
        UserRole rolAdmin = roleRepository.findByName(UserRole.RoleName.ADMIN)
            .orElseThrow(() -> new RuntimeException("No encontré el rol ADMIN en la base de datos"));

        // ====== PASO C: CREAR NUEVO USUARIO ADMIN ======
        AppUser usuarioAdmin = new AppUser();
        usuarioAdmin.setFirstName("Administrador");
        usuarioAdmin.setLastName("Sistema");
        usuarioAdmin.setDni("12345678A");
        usuarioAdmin.setUsername(emailAdmin);
        usuarioAdmin.setPassword(passwordEncoder.encode("1234"));
        usuarioAdmin.setPhone("600000000");
        usuarioAdmin.setRole(rolAdmin);
        usuarioAdmin.setActive(true);

        // ====== PASO D: GUARDAR EL USUARIO EN LA BD ======
        appUserRepository.save(usuarioAdmin);
    }
}