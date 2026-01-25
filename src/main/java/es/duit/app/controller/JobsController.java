package es.duit.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/jobs")
public class JobsController {

    @GetMapping("/buscar")
    public String buscar() {
        return "jobs/buscar";
    }

    @GetMapping("/crear")
    public String crear() {
        return "jobs/crear";
    }

    @GetMapping("/postulaciones")
    public String postulaciones() {
        return "jobs/postulaciones";
    }

    @GetMapping("/solicitudes")
    public String solicitudes() {
        return "jobs/solicitudes";
    }
}