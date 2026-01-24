
package es.duit.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PublicController {

    @GetMapping({ "/", "/index" })
    public String index() {
        return "public/index";
    }

    @GetMapping("/login")
    public String login() {
        return "public/login";
    }

    @GetMapping("/terminos")
    public String terminos() {
        return "public/terminos";
    }

    @GetMapping("/privacidad")
    public String privacidad() {
        return "public/privacidad";
    }

    @GetMapping("/ayuda")
    public String ayuda() {
        return "public/ayuda";
    }

    @GetMapping("/registro")
    public String registro() {
        return "public/registro";
    }

}
