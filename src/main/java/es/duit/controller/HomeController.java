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

@Controller
public class HomeController {

    @Autowired
    private UsuarioDAO usuarioDAO;

    @Autowired
    private RolDAO rolDAO;

    @GetMapping("/home")
    public String home(
            @RequestParam(required = false) String view,
            Model model,
            Authentication authentication) throws Exception {

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

        String plantilla;

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

        String username = authentication.getName();
        Usuario usuario = usuarioDAO.obtenerUsuarioPorUsername(username);

        if (usuario != null) {
            Rol rol = rolDAO.obtenerRolPorId(usuario.getIdRol());
            usuario.setRol(rol);
        }

        model.addAttribute("usuario", usuario);

        return plantilla;
    }
}
