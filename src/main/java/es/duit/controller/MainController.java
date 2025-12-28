package es.duit.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

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

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/index";
    }
}
