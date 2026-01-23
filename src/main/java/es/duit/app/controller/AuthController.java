package es.duit.app.controller;

import es.duit.app.entity.AppUser;
import es.duit.app.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    @Autowired
    private AuthService authService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model) {
        AppUser user = authService.validateUser(username, password);
        if (user != null) {
            model.addAttribute("user", user);
            if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                return "home";
            } else {
                return "user-home";
            }
        } else {
            model.addAttribute("error", "Credenciales incorrectas");
            return "index";
        }
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/user-home")
    public String userHome() {
        return "user-home";
    }
}
