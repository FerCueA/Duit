package es.duit.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/usuarios")
    public String usuarios() {
        return "admin/usuarios";
    }

    @GetMapping("/estadisticas")
    public String estadisticas() {
        return "admin/estadisticas";
    }

   
}