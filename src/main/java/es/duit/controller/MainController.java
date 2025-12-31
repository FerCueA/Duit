package es.duit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import es.duit.dao.UsuarioDAO;
import es.duit.models.Usuario;
import java.sql.Timestamp;

@Controller
public class MainController {

    @Autowired
    private UsuarioDAO usuarioDAO;

    @GetMapping({ "/", "/index" })
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String procesarRegistro(
            @RequestParam("nombre") String nombre,
            @RequestParam("apellidos") String apellidos,
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("telefono") String telefono,
            @RequestParam("idRol") int idRol,
            RedirectAttributes redirectAttributes) {
        try {
            // Verificar si el username ya existe
            if (usuarioDAO.obtenerUsuarioPorUsername(username) != null) {
                redirectAttributes.addFlashAttribute("error", "El nombre de usuario ya existe");
                return "redirect:/register";
            }
            Usuario usuario = new Usuario();
            usuario.setNombre(nombre);
            usuario.setApellidos(apellidos);
            usuario.setUsername(username);
            usuario.setEmail(email);
            usuario.setPassword(password); 
            usuario.setTelefono(telefono);
            usuario.setIdRol(idRol);
            usuario.setActivo(true);
            usuario.setFechaRegistro(new Timestamp(System.currentTimeMillis()));

            // Guardar usuario
            usuarioDAO.insertarUsuario(usuario);
            redirectAttributes.addFlashAttribute("success", "Usuario registrado correctamente");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar usuario: " + e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/index";
    }
}
