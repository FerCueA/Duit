
package es.duit.app.controller;

import es.duit.app.entity.AppUser;
import es.duit.app.entity.UserRole;
import es.duit.app.repository.AppUserRepository;
import es.duit.app.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Este controlador maneja las páginas públicas (login, registro, etc)
@Controller
public class PublicController {

    // Para loguear errores
    private static final Logger logger = LoggerFactory.getLogger(PublicController.class);

    // Repositorios que necesito
    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private RoleRepository roleRepository;

    // Para encriptar contraseñas
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Página principal
    @GetMapping("/")
    public String mostrarPrincipal() {
        return "public/index";
    }

    // También puedo acceder a index directamente
    @GetMapping("/index")
    public String mostrarIndex() {
        return "public/index";
    }

    // Página de login
    @GetMapping("/login")
    public String mostrarLogin(@RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {

        // Si hay error de login
        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos. Por favor, verifica tus credenciales.");
        }

        // Si acaba de cerrar sesión
        if (logout != null) {
            model.addAttribute("success", "Has cerrado sesión correctamente.");
        }

        return "public/login";
    }

    // Página de términos y condiciones
    @GetMapping("/terminos")
    public String mostrarTerminos() {
        return "public/terminos";
    }

    // Página de privacidad
    @GetMapping("/privacidad")
    public String mostrarPrivacidad() {
        return "public/privacidad";
    }

    // Página de ayuda
    @GetMapping("/ayuda")
    public String mostrarAyuda() {
        return "public/ayuda";
    }

    // Formulario de registro
    @GetMapping("/registro")
    public String mostrarRegistro() {
        return "public/registro";
    }

    // Procesar el registro de un nuevo usuario
    @PostMapping("/register")
    public String procesarRegistro(@RequestParam String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String dni,
            @RequestParam String email,
            @RequestParam(required = false) String phone,
            @RequestParam String password,
            @RequestParam String userType,
            Model model) {

        try {
            // Preparo los datos (para usar después si hay error)
            anadirDatosAlModelo(model, firstName, lastName, dni, email, phone, userType);

            // Valido que el email no esté registrado
            String emailLimpio = email != null ? email.trim() : "";
            List<AppUser> usuariosConEmail = appUserRepository.findByUsername(emailLimpio);
            if (!usuariosConEmail.isEmpty()) {
                model.addAttribute("error", "Este correo electrónico ya está registrado");
                return "public/registro";
            }

            // Valido que el DNI no esté registrado
            if (dni != null && !dni.trim().isEmpty()) {
                String dniLimpio = dni.trim().toUpperCase();
                List<AppUser> usuariosConDni = appUserRepository.findByDni(dniLimpio);
                if (!usuariosConDni.isEmpty()) {
                    model.addAttribute("error", "Este DNI ya está registrado");
                    return "public/registro";
                }
            }

            // Valido que sea un tipo de usuario válido
            if (!"USER".equals(userType) && !"PROFESSIONAL".equals(userType)) {
                model.addAttribute("error", "Debes seleccionar un tipo de usuario válido");
                return "public/registro";
            }

            // Busco el rol correspondiente al tipo de usuario
            UserRole.RoleName nombreRol = "USER".equals(userType) ? UserRole.RoleName.USER
                    : UserRole.RoleName.PROFESSIONAL;
            List<UserRole> rolesEncontrados = roleRepository.findByName(nombreRol);
            if (rolesEncontrados.isEmpty()) {
                throw new RuntimeException("Rol " + nombreRol + " no encontrado en la base de datos");
            }
            UserRole rolSeleccionado = rolesEncontrados.get(0);

            // Creo el nuevo usuario
            logger.info("Creando nuevo usuario: {}", emailLimpio);
            AppUser usuarioNuevo = new AppUser();
            usuarioNuevo.setFirstName(firstName != null ? firstName.trim() : null);
            usuarioNuevo.setLastName(lastName != null ? lastName.trim() : null);
            usuarioNuevo.setDni(dni != null ? dni.trim().toUpperCase() : null);
            usuarioNuevo.setUsername(emailLimpio);
            usuarioNuevo.setPassword(passwordEncoder.encode(password));
            usuarioNuevo.setPhone(phone != null ? phone.trim() : null);
            usuarioNuevo.setRole(rolSeleccionado);
            usuarioNuevo.setActive(true);

            // Lo guardo en la base de datos
            appUserRepository.save(usuarioNuevo);
            logger.info("Usuario registrado correctamente: {}", emailLimpio);

            // Muestro un mensaje de éxito
            model.addAttribute("success", "Registro exitoso. Ya puedes iniciar sesión con tu email.");
            return "public/login";

        } catch (Exception error) {
            // Logueo el error para que se vea en consola
            logger.error("Error al registrar usuario: {}", error.getMessage());
            error.printStackTrace();

            // Muestro un mensaje de error
            model.addAttribute("error", "Error en el registro. Por favor, verifica los datos e inténtalo de nuevo");
            return "public/registro";
        }
    }

    // Método auxiliar
    private void anadirDatosAlModelo(Model model, String firstName, String lastName,
            String dni, String email, String phone, String userType) {
        model.addAttribute("firstName", firstName);
        model.addAttribute("lastName", lastName);
        model.addAttribute("dni", dni);
        model.addAttribute("email", email);
        model.addAttribute("phone", phone);
        model.addAttribute("userType", userType);
    }

}
