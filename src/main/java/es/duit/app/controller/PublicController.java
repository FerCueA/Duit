
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

import java.time.LocalDateTime;

@Controller
public class PublicController {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String root() {
        return "public/index";
    }

    @GetMapping("/index")
    public String index() {
        return "public/index";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {

        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos. Por favor, verifica tus credenciales.");
        }

        if (logout != null) {
            model.addAttribute("success", "Has cerrado sesión correctamente.");
        }

        return "public/login";
    }

    @GetMapping("/terminos")
    public String terminos() {
        return "public/terminos";
    }

    @GetMapping("/privacidad")
    public String privacidad() {
        return "public/privacidad";
    }

    @GetMapping("/ayuda")
    public String ayuda() {
        return "public/ayuda";
    }

    @GetMapping("/registro")
    public String registro() {
        return "public/registro";
    }

    @PostMapping("/register")
    public String register(@RequestParam String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String dni,
            @RequestParam String email,
            @RequestParam(required = false) String phone,
            @RequestParam String password,
            @RequestParam String userType,
            Model model) {

        try {

            if (email != null && appUserRepository.findByUsername(email.trim()).isPresent()) {
                model.addAttribute("error", "Este correo electrónico ya está registrado");
                model.addAttribute("firstName", firstName);
                model.addAttribute("lastName", lastName);
                model.addAttribute("dni", dni);
                model.addAttribute("email", email);
                model.addAttribute("phone", phone);
                model.addAttribute("userType", userType);
                return "public/registro";
            }

            // Validar DNI duplicado
            if (dni != null && !dni.trim().isEmpty()
                    && appUserRepository.findByDni(dni.trim().toUpperCase()).isPresent()) {
                model.addAttribute("error", "Este DNI ya está registrado");
                model.addAttribute("firstName", firstName);
                model.addAttribute("lastName", lastName);
                model.addAttribute("dni", dni);
                model.addAttribute("email", email);
                model.addAttribute("phone", phone);
                model.addAttribute("userType", userType);
                return "public/registro";
            }

            // Validar tipo de usuario
            if (!"USER".equals(userType) && !"PROFESSIONAL".equals(userType)) {
                model.addAttribute("error", "Debes seleccionar un tipo de usuario válido");
                model.addAttribute("firstName", firstName);
                model.addAttribute("lastName", lastName);
                model.addAttribute("dni", dni);
                model.addAttribute("email", email);
                model.addAttribute("phone", phone);
                model.addAttribute("userType", userType);
                return "public/registro";
            }

            UserRole selectedRole = roleRepository
                    .findByName("USER".equals(userType) ? UserRole.RoleName.USER : UserRole.RoleName.PROFESSIONAL)
                    .orElseThrow(() -> new RuntimeException("Rol " + userType + " no encontrado"));

            // Crear y guardar usuario
            AppUser newUser = new AppUser();
            newUser.setFirstName(firstName != null ? firstName.trim() : null);
            newUser.setLastName(lastName != null ? lastName.trim() : null);
            newUser.setDni(dni != null ? dni.trim().toUpperCase() : null);
            newUser.setUsername(email != null ? email.trim() : null);
            newUser.setPassword(password != null ? passwordEncoder.encode(password) : null);
            newUser.setPhone(phone != null ? phone.trim() : null);
            newUser.setRole(selectedRole);
            newUser.setActive(true);
            newUser.setRegisteredAt(LocalDateTime.now());
            newUser.setCreatedBy("system");
            newUser.setCreatedAt(LocalDateTime.now());
            newUser.setUpdatedBy("system");
            newUser.setUpdatedAt(LocalDateTime.now());

            // Guardar directamente
            appUserRepository.save(newUser);

            model.addAttribute("success", "Registro exitoso. Ya puedes iniciar sesión con tu email.");
            return "public/login";

        } catch (Exception e) {
            // Log del error para debugging
            e.printStackTrace();

            model.addAttribute("error", "Error en el registro. Por favor, verifica los datos e inténtalo de nuevo");
            model.addAttribute("firstName", firstName);
            model.addAttribute("lastName", lastName);
            model.addAttribute("dni", dni);
            model.addAttribute("email", email);
            model.addAttribute("phone", phone);
            model.addAttribute("userType", userType);
            return "public/registro";
        }
    }

}
