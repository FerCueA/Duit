package es.duit.controller;

import es.duit.dao.UsuarioDAO;
import es.duit.models.Usuario;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

    private final UsuarioDAO usuarioDAO;

    public MainController() {
        this.usuarioDAO = new UsuarioDAO(new es.duit.connections.MySqlConnection());
    }

    @GetMapping("/login")
    public String login() {
        return "login"; 
    }

    @GetMapping("/home")
    public String home() {
        return "home"; 
    }

    @GetMapping({"/", "/index"})
    public String index() {
        return "index"; 
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/login")
    public String validarLogin(@RequestParam("email") String email,
                                @RequestParam("password") String password,
                                Model model) {
        Usuario usuario = usuarioDAO.obtenerPorEmail(email);
        if (usuario != null && usuario.getPassword().equals(password)) {
            model.addAttribute("usuario", usuario);
            return "home";
        } else {
            model.addAttribute("error", "Credenciales inválidas. Por favor, inténtalo de nuevo.");
            return "login";
        }
    }
}
