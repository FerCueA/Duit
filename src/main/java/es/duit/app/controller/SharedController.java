package es.duit.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/shared")
public class SharedController {

    @GetMapping("/historial")
    public String historial() {
        return "shared/historial";
    }

    @GetMapping("/valoraciones")
    public String valoraciones() {
        return "shared/valoraciones";
    }

    @GetMapping("/ayuda")
    public String ayuda() {
        return "shared/ayuda";
    }
}