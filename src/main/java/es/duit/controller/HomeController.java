package es.duit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.duit.dao.RolDAO;
import es.duit.dao.UsuarioDAO;
import es.duit.models.Rol;
import es.duit.models.Usuario;


// Controlador principal para gestionar la navegación de la página de inicio y secciones de usuario
@Controller
public class HomeController {

    // DAO para operaciones con usuarios
    @Autowired
    private UsuarioDAO usuarioDAO;

    // DAO para operaciones con roles
    @Autowired
    private RolDAO rolDAO;


        // Mapeo principal para la ruta /home
        @GetMapping("/home")
        public String home(
            @RequestParam(required = false) String view,
            Model model,
            Authentication authentication) throws Exception {

        // Vistas válidas que se pueden mostrar en la sección de usuario
        String[] validViews = {
                "buscarOfertas",
                "misOfertas",
                "anadirOferta",
                "consultarHistorial",
                "verValoraciones",
                "editarPerfil",
                "panelPrincipal",
                "ayuda",
                "verUsuarios",
                "verEstadisticas",
                "tiposTrabajo"
        };

        // Variable para almacenar la plantilla a renderizar
        String plantilla;

        // Lógica para determinar qué plantilla mostrar según el parámetro 'view'
        if (view == null) {
            plantilla = "seccionesHome/panelPrincipal";
        } else {
            boolean isValid = false;
            for (String v : validViews) {
                if (v.equals(view)) {
                    isValid = true;
                    break;
                }
            }

            if (isValid) {
                plantilla = "seccionesHome/" + view;
            } else {
                plantilla = "seccionesHome/errorView";
            }
        }

        
        // Obtener el usuario autenticado y su rol
        String username = authentication.getName();
        Usuario usuario = usuarioDAO.obtenerUsuarioPorUsername(username);

        if (usuario != null) {
            Rol rol = rolDAO.obtenerRolPorId(usuario.getIdRol());
            usuario.setRol(rol);
        }

        // Añadir el usuario al modelo para la vista
        model.addAttribute("usuario", usuario);

        // Devolver la plantilla correspondiente
        return plantilla;
    }
}
