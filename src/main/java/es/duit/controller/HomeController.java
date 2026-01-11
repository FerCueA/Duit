package es.duit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


import es.duit.dao.RolDAO;
import es.duit.dao.UsuarioDAO;
import es.duit.dao.CategoriaDAO;
import es.duit.models.Rol;
import es.duit.models.Usuario;
import es.duit.models.Categoria;
import java.util.ArrayList;

@Controller
public class HomeController {
    
   
    @Autowired
    private UsuarioDAO usuarioDAO;
    @Autowired
    private RolDAO rolDAO;
    @Autowired
    private CategoriaDAO categoriaDAO;

    private void obtenerDatosUsuario(Model model, Authentication authentication) throws Exception {
        if (authentication != null) {
            Usuario usuario = usuarioDAO.obtenerUsuarioPorUsername(authentication.getName());
            if (usuario != null) {
                Rol rol = rolDAO.obtenerRolPorId(usuario.getIdRol());
                usuario.setRol(rol);
            }
            model.addAttribute("usuario", usuario);
        }
    }

    @GetMapping("/home")
    public String home(Model model, Authentication authentication) throws Exception {
        obtenerDatosUsuario(model, authentication);
        return "home";
    }

    @GetMapping("/buscarOfertas")
    public String buscarOfertas(Model model, Authentication authentication) throws Exception {
        obtenerDatosUsuario(model, authentication);
        return "seccionesHome/buscarOfertas";
    }

    @GetMapping("/misOfertas")
    public String misOfertas(Model model, Authentication authentication) throws Exception {
        obtenerDatosUsuario(model, authentication);
        return "seccionesHome/misOfertas";
    }

    @GetMapping("/anadirOferta")
    public String anadirOferta(Model model, Authentication authentication) throws Exception {
        obtenerDatosUsuario(model, authentication);
        return "seccionesHome/anadirOferta";
    }

    @GetMapping("/consultarHistorial")
    public String consultarHistorial(Model model, Authentication authentication) throws Exception {
        obtenerDatosUsuario(model, authentication);
        return "seccionesHome/consultarHistorial";
    }

    @GetMapping("/verValoraciones")
    public String verValoraciones(Model model, Authentication authentication) throws Exception {
        obtenerDatosUsuario(model, authentication);
        return "seccionesHome/verValoraciones";
    }

    @GetMapping("/editarPerfil")
    public String editarPerfil(Model model, Authentication authentication) throws Exception {
        obtenerDatosUsuario(model, authentication);
        return "seccionesHome/editarPerfil";
    }

    @GetMapping("/ayuda")
    public String ayuda(Model model, Authentication authentication) throws Exception {
        obtenerDatosUsuario(model, authentication);
        return "seccionesHome/ayuda";
    }


    @GetMapping("/verUsuarios")
    public String verUsuarios(Model model, Authentication authentication) throws Exception {
        obtenerDatosUsuario(model, authentication);
        ArrayList<Usuario> usuarios = usuarioDAO.obtenerTodosUsuarios();
        model.addAttribute("usuarios", usuarios);
        return "seccionesHome/verUsuarios";
    }

    @GetMapping("/verEstadisticas")
    public String verEstadisticas(Model model, Authentication authentication) throws Exception {
        obtenerDatosUsuario(model, authentication);
       
        return "seccionesHome/verEstadisticas";
    }


    @GetMapping("/tiposTrabajo")
    public String tiposTrabajo(Model model, Authentication authentication) throws Exception {
        obtenerDatosUsuario(model, authentication);
        ArrayList<Categoria> categorias = categoriaDAO.obtenerTodasCategorias();
        model.addAttribute("categorias", categorias);
        return "seccionesHome/tiposTrabajo";
    }

    @GetMapping("/errorView")
    public String errorView(Model model, Authentication authentication) throws Exception {
        obtenerDatosUsuario(model, authentication);
        return "seccionesHome/errorView";
    }




}
