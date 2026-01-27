package es.duit.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @GetMapping("/editar")
    public String editar() {
        return "profile/editarPerfil";
    }

    @GetMapping("/profesional")
    public String profesional() {
        return "profile/editarProfesional";
    }
}