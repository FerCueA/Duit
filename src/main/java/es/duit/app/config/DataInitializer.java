package es.duit.app.config;

import es.duit.app.entity.AppUser;
import es.duit.app.entity.UserRole;
import es.duit.app.repository.AppUserRepository;
import es.duit.app.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

// Esta clase crea datos básicos cuando arranca la aplicación
// @Component  // DESACTIVADO: Usar script SQL personalizado en su lugar
public class DataInitializer implements ApplicationRunner {

    // Para escribir logs en consola
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    // Repositorios que necesito
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    // Para encriptar contraseñas
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Este método se ejecuta cuando arranca la app
    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        logger.info("Empezando a crear datos iniciales...");

        // Espero un poquito para que se creen las tablas
        Thread.sleep(2000);

        // Creo los roles y el admin
        crearRoles();
        crearUsuarioAdmin();

        logger.info("Datos iniciales creados correctamente.");
    }

    // Creo todos los roles que necesita la app
    private void crearRoles() {
        logger.info("Verificando que existen los roles...");

        crearRolSiNoExiste(UserRole.RoleName.ADMIN, "Administrador del sistema", true);
        crearRolSiNoExiste(UserRole.RoleName.USER, "Usuario regular", true);
        crearRolSiNoExiste(UserRole.RoleName.PROFESSIONAL, "Profesional prestador de servicios", true);
        crearRolSiNoExiste(UserRole.RoleName.MODERATOR, "Moderador de contenido", true);

        logger.info("Todos los roles están listos.");
    }

    // Creo un rol si no existe ya
    private void crearRolSiNoExiste(UserRole.RoleName nombreRol, String descripcion, boolean activo) {
        // Busco si el rol ya existe
        List<UserRole> rolesEncontrados = roleRepository.findByName(nombreRol);

        // Si ya existe, no hago nada
        if (!rolesEncontrados.isEmpty()) {
            logger.debug("El rol ya existe: {}", nombreRol);
            return;
        }

        // Si no existe, lo creo
        logger.info("Creando nuevo rol: {}", nombreRol);

        UserRole nuevoRol = new UserRole();
        nuevoRol.setName(nombreRol);
        nuevoRol.setDescription(descripcion);
        nuevoRol.setActive(activo);
        nuevoRol.setCreatedBy("system");
        nuevoRol.setCreatedAt(LocalDateTime.now());
        nuevoRol.setUpdatedBy("system");
        nuevoRol.setUpdatedAt(LocalDateTime.now());

        roleRepository.save(nuevoRol);
        logger.info("Rol creado correctamente: {}", nombreRol);
    }

    // Creo el usuario administrador por defecto
    private void crearUsuarioAdmin() {
        logger.info("Verificando si existe el usuario admin...");

        String emailAdmin = "admin@duit.es";

        // Busco si el usuario admin ya existe
        List<AppUser> usuariosExistentes = appUserRepository.findByUsername(emailAdmin);
        if (!usuariosExistentes.isEmpty()) {
            logger.info("El usuario admin ya existe, no hago nada.");
            return;
        }

        // Busco el rol ADMIN
        logger.info("Buscando el rol ADMIN...");
        List<UserRole> rolesAdmin = roleRepository.findByName(UserRole.RoleName.ADMIN);
        if (rolesAdmin.isEmpty()) {
            throw new RuntimeException("No encontré el rol ADMIN en la base de datos");
        }
        UserRole rolAdmin = rolesAdmin.get(0);

        // Creo el usuario admin
        logger.info("Creando el usuario admin...");
        AppUser usuarioAdmin = new AppUser();
        usuarioAdmin.setFirstName("Administrador");
        usuarioAdmin.setLastName("Sistema");
        usuarioAdmin.setDni("12345678A");
        usuarioAdmin.setUsername(emailAdmin);
        usuarioAdmin.setPassword(passwordEncoder.encode("1234"));
        usuarioAdmin.setPhone("600000000");
        usuarioAdmin.setRole(rolAdmin);
        usuarioAdmin.setActive(true);

        // Lo guardo en la BD
        appUserRepository.save(usuarioAdmin);

        // Imprimo la información del admin creado
        logger.info("Usuario administrador creado correctamente:");
        logger.info("  Email: {}", emailAdmin);
        logger.info("  Password: 1234");
        logger.info("  Rol: ADMIN");
    }
}