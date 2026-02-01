
package es.duit.app.controller;

import es.duit.app.dto.RegistroDTO;
import es.duit.app.entity.AppUser;
import es.duit.app.service.RegistroService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

// ============================================================================
// CONTROLADOR PÚBLICO - GESTIONA PÁGINAS ACCESIBLES SIN AUTENTICACIÓN
// ============================================================================
@Controller
public class PublicController {

    private final RegistroService registroService;

    public PublicController(RegistroService registroService) {
        this.registroService = registroService;
    }

    // ============================================================================
    // PÁGINA PRINCIPAL DE LA APLICACIÓN
    // ============================================================================
    @GetMapping("/")
    public String mostrarPaginaPrincipal() {
        return "public/index";
    }

    // ============================================================================
    // PÁGINA DE ÍNDICE ALTERNATIVA
    // ============================================================================
    @GetMapping("/index")
    public String mostrarPaginaIndex() {
        return "public/index";
    }

    // ============================================================================
    // PÁGINA DE INICIO DE SESIÓN CON MANEJO DE MENSAJES
    // ============================================================================
    @GetMapping("/login")
    public String mostrarPaginaLogin(@RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Authentication auth,
            Model model) {

        // Verificar si el usuario ya está autenticado
        boolean usuarioYaLogueado = verificarUsuarioYaAutenticado(auth);
        if (usuarioYaLogueado) {
            return "redirect:/home";
        }

        // Procesar mensajes de error y logout
        procesarMensajesLogin(error, logout, model);

        return "public/login";
    }

    // ============================================================================
    // PÁGINA DE TÉRMINOS Y CONDICIONES
    // ============================================================================
    @GetMapping({ "/terms", "/terminos" })
    public String mostrarPaginaTerminos() {
        return "public/trems";
    }

    // ============================================================================
    // PÁGINA DE POLÍTICA DE PRIVACIDAD
    // ============================================================================
    @GetMapping({ "/privacy", "/privacidad" })
    public String mostrarPaginaPrivacidad() {
        return "public/privacy";
    }

    // ============================================================================
    // PÁGINA DE AYUDA Y SOPORTE
    // ============================================================================
    @GetMapping({ "/help", "/ayuda" })
    public String mostrarPaginaAyuda() {
        return "public/help";
    }

    // ============================================================================
    // FORMULARIO DE REGISTRO DE NUEVOS USUARIOS
    // ============================================================================
    @GetMapping({ "/signup", "/registro" })
    public String mostrarFormularioRegistro(Model model) {
        // Crear formulario vacío para el registro
        RegistroDTO formularioVacio = crearFormularioRegistroVacio();
        model.addAttribute("registroDTO", formularioVacio);
        return "public/signup";
    }

    // ============================================================================
    // PROCESA EL REGISTRO DE NUEVOS USUARIOS
    // ============================================================================
    @PostMapping("/register")
    public String procesarRegistroUsuario(@Valid RegistroDTO registro,
            BindingResult bindingResult,
            Model model) {

        try {
            // Validar errores de formulario
            if (bindingResult.hasErrors()) {
                // Devolver al formulario con los datos ingresados y errores
                model.addAttribute("registroDTO", registro);
                return "public/signup";
            }

            // Procesar registro del usuario usando el servicio
            AppUser usuarioCreado = registroService.registerUser(registro);

            // Preparar respuesta exitosa
            String mensajeExito = "Registro exitoso. Ya puedes iniciar sesión con tu email.";
            model.addAttribute("success", mensajeExito);
            return "public/login";

        } catch (IllegalArgumentException error) {
            // Manejar errores de validación de negocio
            model.addAttribute("error", error.getMessage());

            // Preservar datos del formulario (excepto contraseña por seguridad)
            model.addAttribute("firstName", registro.firstName());
            model.addAttribute("lastName", registro.lastName());
            model.addAttribute("dni", registro.dni());
            model.addAttribute("email", registro.email());
            model.addAttribute("phone", registro.phone());
            model.addAttribute("userType", registro.userType());

            return "public/signup";

        } catch (Exception error) {
            // Manejar errores inesperados
            String mensajeErrorGenerico = "Error en el registro. Por favor, inténtalo de nuevo.";
            model.addAttribute("error", mensajeErrorGenerico);
            return "public/signup";
        }
    }

    // ============================================================================
    // VERIFICA SI EL USUARIO YA ESTÁ AUTENTICADO
    // ============================================================================
    private boolean verificarUsuarioYaAutenticado(Authentication auth) {
        // Verificar si hay autenticación activa
        if (auth == null) {
            return false;
        }

        // Verificar si está realmente autenticado
        boolean estaAutenticado = auth.isAuthenticated();
        if (!estaAutenticado) {
            return false;
        }

        // Verificar que no sea usuario anónimo
        String nombreUsuario = auth.getName();
        boolean esUsuarioAnonimo = "anonymousUser".equals(nombreUsuario);

        return !esUsuarioAnonimo;
    }

    // ============================================================================
    // PROCESA LOS MENSAJES DE ERROR Y LOGOUT EN LA PÁGINA DE LOGIN
    // ============================================================================
    private void procesarMensajesLogin(String error, String logout, Model model) {
        // Procesar mensaje de error de autenticación
        if (error != null) {
            String mensajeError = "Usuario o contraseña incorrectos. Por favor, verifica tus credenciales.";
            model.addAttribute("error", mensajeError);
        }

        // Procesar mensaje de logout exitoso
        if (logout != null) {
            String mensajeLogout = "Has cerrado sesión correctamente.";
            model.addAttribute("success", mensajeLogout);
        }
    }

    // ============================================================================
    // CREA UN FORMULARIO DE REGISTRO VACÍO CON VALORES POR DEFECTO
    // ============================================================================
    private RegistroDTO crearFormularioRegistroVacio() {
        // Crear formulario con todos los campos nulos
        return new RegistroDTO(null, null, null, null, null, null, null);
    }
}
