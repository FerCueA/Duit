package es.duit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;

import es.duit.dao.PerfilProfesionalDAO;
import es.duit.models.Usuario;
import es.duit.util.UsuarioModelHelper;
import es.duit.models.PerfilProfesional;

@Controller
public class HomeController {

    @Autowired
    private PerfilProfesionalDAO perfilProfesionalDAO;
    @Autowired
    private UsuarioModelHelper usuarioModelHelper;

    // HOME
    @GetMapping("/home")
    public String home(Model model, Authentication auth) throws Exception {
        usuarioModelHelper.ponerUsuario(model, auth);
        return "home";
    }

    // AYUDA
    @GetMapping("/ayuda")
    public String ayuda(Model model, Authentication auth) throws Exception {
        usuarioModelHelper.ponerUsuario(model, auth);
        return "seccionesHome/ayuda";
    }

    // HISTORIAL
    @GetMapping("/consultarHistorial")
    public String consultarHistorial(Model model, Authentication auth) throws Exception {
        usuarioModelHelper.ponerUsuario(model, auth);
        return "seccionesHome/consultarHistorial";
    }

    // VALORACIONES
    @GetMapping("/verValoraciones")
    public String verValoraciones(Model model, Authentication auth) throws Exception {
        usuarioModelHelper.ponerUsuario(model, auth);
        return "seccionesHome/verValoraciones";
    }

    // EDITAR PERFIL
    @GetMapping("/editarPerfil")
    public String editarPerfil(Model model, Authentication auth) throws Exception {
        usuarioModelHelper.ponerUsuario(model, auth);
        return "seccionesHome/editarPerfil";
    }

    // EDITAR PERFIL PROFESIONAL
    @GetMapping("/editarPerfilProfesional")
    public String editarPerfilProfesional(Model model, Authentication auth) throws Exception {

        usuarioModelHelper.ponerUsuario(model, auth);

        Usuario u = (Usuario) model.getAttribute("usuario");

        if (u == null || u.getRol() == null) {
            model.addAttribute("errorNoProfesional", true);
            return "seccionesHome/editarPerfilProfesional";
        }

        String nombreRol = u.getRol().getNombre();
        if (nombreRol == null || !nombreRol.equalsIgnoreCase("PROFESIONAL")) {
            model.addAttribute("errorNoProfesional", true);
            return "seccionesHome/editarPerfilProfesional";
        }

        PerfilProfesional p = perfilProfesionalDAO.obtenerPorId(u.getIdUsuario());

        model.addAttribute("perfilProfesional", p);
        return "seccionesHome/editarPerfilProfesional";
    }

}
