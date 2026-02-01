package es.duit.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// ============================================================================
// CONTROLADOR DE ADMINISTRACIÓN - GESTIONA PÁGINAS DEL PANEL DE ADMIN
// ============================================================================
@Controller
@RequestMapping("/admin")
public class AdminController {

    // ============================================================================
    // PÁGINA DE GESTIÓN DE USUARIOS
    // ============================================================================
    @GetMapping({"/users"})
    public String mostrarGestionUsuarios() {
        return "admin/users";
    }

    // ============================================================================
    // PÁGINA DE ESTADÍSTICAS Y MÉTRICAS
    // ============================================================================
    @GetMapping({"/stats"})
    public String mostrarEstadisticas() {
        return "admin/stats";
    }
}