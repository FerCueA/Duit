package es.duit.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// ============================================================================
// CONTROLADOR DE PANEL PRINCIPAL - GESTIONA LA PÁGINA DE INICIO
// ============================================================================
@Controller
public class DashboardController {

    // ============================================================================
    // PÁGINA PRINCIPAL DEL DASHBOARD PARA USUARIOS AUTENTICADOS
    // ============================================================================
    @GetMapping("/home")
    public String mostrarPaginaInicio() {
        return "dashboard/home";
    }
}
