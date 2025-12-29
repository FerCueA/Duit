package es.duit.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home(@RequestParam(required = false) String view, Model model) {
        String[] validViews = {
            "buscarOfertas",
            "misOfertas",
            "anadirOferta",
            "consultarHistorial",
            "verValoraciones",
            "editarPerfil",
            "panelAdministrador",
            "ayuda"
        };

        String plantilla;
        if (view == null) {
            plantilla = "componentesHome/buscarOfertas";
        } else {
            boolean isValid = false;
            for (String v : validViews) {
                if (v.equals(view)) {
                    isValid = true;
                    break;
                }
            }
            if (isValid) {
                plantilla = "componentesHome/" + view;
            } else {
                plantilla = "componentesHome/errorView";
            }
        }

        // Puedes pasar datos al modelo si lo necesitas
        return plantilla;
    }
}
