
package es.duit.app.controller;

import es.duit.app.dto.RegistroDTO;
import es.duit.app.service.RegistroService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Controlador para páginas públicas
@Controller
public class PublicController {

    private static final Logger logger = LoggerFactory.getLogger(PublicController.class);
    private final RegistroService registroService;

    public PublicController(RegistroService registroService) {
        this.registroService = registroService;
    }

    // Página principal
    @GetMapping("/")
    public String mostrarPrincipal() {
        return "public/index";
    }

    // Página de índice
    @GetMapping("/index")
    public String mostrarIndex() {
        return "public/index";
    }

    // Página de login
    @GetMapping("/login")
    public String mostrarLogin(
            @RequestParam(value = "error", required = false) String error,
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

    // Página de términos
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
    public String mostrarRegistro(Model model) {
        model.addAttribute("registro", new RegistroDTO(null, null, null, null, null, null, null));
        return "public/registro";
    }

    // Registrar usuario
    @PostMapping("/register")
    public String procesarRegistro(
            @Valid RegistroDTO registro,
            BindingResult bindingResult,
            Model model) {

        // Validar errores de Bean Validation
        if (bindingResult.hasErrors()) {
            return "public/registro";
        }

        try {
            registroService.registrarUsuario(registro);
            logger.info("Nuevo usuario registrado: {}", registro.email());
            model.addAttribute("success", "Registro exitoso. Ya puedes iniciar sesión con tu email.");
            return "public/login";
        } catch (IllegalArgumentException e) {
            logger.warn("Error en registro: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "public/registro";
        } catch (Exception e) {
            logger.error("Error inesperado en registro: {}", e.getMessage());
            model.addAttribute("error", "Error en el registro. Por favor, inténtalo de nuevo.");
            return "public/registro";
        }
    }
}
