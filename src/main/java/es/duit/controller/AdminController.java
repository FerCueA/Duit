package es.duit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;

import es.duit.dao.UsuarioDAO;
import es.duit.dao.CategoriaDAO;
import es.duit.models.Usuario;
import es.duit.util.UsuarioModelHelper;
import es.duit.models.Categoria;
import java.util.ArrayList;

@Controller
public class AdminController {
    @Autowired
    private CategoriaDAO categoriaDAO;
    @Autowired
    private UsuarioDAO usuarioDAO;
   

    // MÉTODO PARA PONER EL USUARIO EN EL MODEL
        @Autowired
        private UsuarioModelHelper usuarioModelHelper;

    // GESTIÓN DE TIPOS DE TRABAJO

    @GetMapping("/tiposTrabajo")
    public String tiposTrabajo(Model model, Authentication auth) throws Exception {
            usuarioModelHelper.ponerUsuario(model, auth);
        ArrayList<Categoria> categorias = categoriaDAO.obtenerTodas();
        model.addAttribute("categorias", categorias);
        return "seccionesHome/tiposTrabajo";
    }

    @PostMapping("/tiposTrabajo/crear")
    public String crearTipoTrabajo(@RequestParam String nombre, @RequestParam String descripcion, Model model,
            Authentication auth) throws Exception {
        Categoria c = new Categoria();
        c.setNombre(nombre);
        c.setDescripcion(descripcion);
        categoriaDAO.insertarCategoria(c);
        return "redirect:/tiposTrabajo";
    }

    @PostMapping("/tiposTrabajo/editar")
    public String editarTipoTrabajo(@RequestParam int idCategoria, @RequestParam String nombre,
            @RequestParam String descripcion, Model model, Authentication auth) throws Exception {
        Categoria c = categoriaDAO.obtenerPorId(idCategoria);
        if (c != null) {
            c.setNombre(nombre);
            c.setDescripcion(descripcion);
            categoriaDAO.actualizarCategoria(c);
        }
        return "redirect:/tiposTrabajo";
    }

    @PostMapping("/tiposTrabajo/eliminar")
    public String eliminarTipoTrabajo(@RequestParam int idCategoria, Model model, Authentication auth)
            throws Exception {
        categoriaDAO.eliminarCategoria(idCategoria);
        return "redirect:/tiposTrabajo";
    }

    // EDICIÓN DE USUARIOS
    @GetMapping("/verUsuarios")
    public String verUsuarios(@RequestParam(value = "filtro", required = false) String filtro, Model model,
            Authentication auth) throws Exception {
            usuarioModelHelper.ponerUsuario(model, auth);
        ArrayList<Usuario> usuarios;
        if (filtro != null && !filtro.trim().isEmpty()) {
            usuarios = usuarioDAO.buscarUsuarios(filtro.trim());
        } else {
            usuarios = usuarioDAO.obtenerTodosUsuarios();
        }
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("filtro", filtro);
        return "seccionesHome/verUsuarios";
    }

    
    @PostMapping("/verUsuarios/editar")
    public String editarUsuario(@RequestParam int idUsuario,
            @RequestParam String nombre,
            @RequestParam String apellidos,
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String telefono,
            @RequestParam int idRol,
            @RequestParam(value = "activo", required = false) String activo,
            Model model, Authentication auth) throws Exception {
        Usuario u = usuarioDAO.obtenerUsuarioPorId(idUsuario);
        if (u != null) {
            u.setNombre(nombre);
            u.setApellidos(apellidos);
            u.setUsername(username);
            u.setEmail(email);
            u.setTelefono(telefono);
            u.setIdRol(idRol);
            u.setActivo(activo != null);
            usuarioDAO.actualizarUsuario(u);
        }
        return "redirect:/verUsuarios";
    }

    
    @PostMapping("/verUsuarios/eliminar")
    public String eliminarUsuario(@RequestParam int idUsuario, Model model, Authentication auth) throws Exception {
        usuarioDAO.eliminarUsuario(idUsuario);
        return "redirect:/verUsuarios";
    }


    

    // ESTADISTICAS
    @GetMapping("/verEstadisticas")
    public String verEstadisticas(Model model, Authentication auth) throws Exception {
            usuarioModelHelper.ponerUsuario(model, auth);
        return "seccionesHome/verEstadisticas";
    }
}
