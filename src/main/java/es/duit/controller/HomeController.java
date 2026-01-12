package es.duit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    @Autowired
    private es.duit.dao.PerfilProfesionalDAO perfilProfesionalDAO;
    @Autowired
    private es.duit.dao.ValoracionDAO valoracionDAO;

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

    @GetMapping("/ayuda")
    public String ayuda(Model model, Authentication authentication) throws Exception {
        obtenerDatosUsuario(model, authentication);
        return "seccionesHome/ayuda";
    }

    @GetMapping("/verValoraciones")
    public String verValoraciones(Model model, Authentication authentication) throws Exception {
        obtenerDatosUsuario(model, authentication);
        es.duit.models.Usuario usuario = (es.duit.models.Usuario) model.getAttribute("usuario");
        java.util.ArrayList<es.duit.models.Valoracion> valoraciones = new java.util.ArrayList<>();
        double media = 0.0;
        if (usuario != null) {
            valoraciones = valoracionDAO.obtenerTodas();
            // Filtrar solo las valoraciones recibidas por el usuario logueado
            java.util.List<es.duit.models.Valoracion> recibidas = new java.util.ArrayList<>();
            int suma = 0;
            for (es.duit.models.Valoracion v : valoraciones) {
                if (v.getIdReceptor() == usuario.getIdUsuario()) {
                    recibidas.add(v);
                    suma += v.getPuntuacion();
                }
            }
            if (!recibidas.isEmpty()) {
                media = suma * 1.0 / recibidas.size();
            }
            model.addAttribute("valoraciones", recibidas);
            model.addAttribute("mediaValoracion", media);
        } else {
            model.addAttribute("valoraciones", new java.util.ArrayList<>());
            model.addAttribute("mediaValoracion", 0.0);
        }
        return "seccionesHome/verValoraciones";
    }

    @GetMapping("/editarPerfil")
    public String editarPerfil(Model model, Authentication authentication) throws Exception {
        obtenerDatosUsuario(model, authentication);
        // Solo datos de usuario, sin perfil profesional
        return "seccionesHome/editarPerfil";
    }

    @GetMapping("/editarPerfilProfesional")
    public String editarPerfilProfesional(Model model, Authentication authentication) throws Exception {
        obtenerDatosUsuario(model, authentication);
        es.duit.models.Usuario usuario = (es.duit.models.Usuario) model.getAttribute("usuario");
        es.duit.models.PerfilProfesional perfilProfesional = null;
        if (usuario != null) {
            perfilProfesional = perfilProfesionalDAO.obtenerPorId(usuario.getIdUsuario());
        }
        model.addAttribute("perfilProfesional", perfilProfesional);
        return "seccionesHome/editarPerfilProfesional";
    }

    @GetMapping("/verUsuarios")
    public String verUsuarios(@RequestParam(value = "filtro", required = false) String filtro, Model model,
            Authentication authentication) throws Exception {
        obtenerDatosUsuario(model, authentication);
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
            Model model, Authentication authentication) throws Exception {
        Usuario usuario = usuarioDAO.obtenerUsuarioPorId(idUsuario);
        if (usuario != null) {
            usuario.setNombre(nombre);
            usuario.setApellidos(apellidos);
            usuario.setUsername(username);
            usuario.setEmail(email);
            usuario.setTelefono(telefono);
            usuario.setIdRol(idRol);
            usuario.setActivo(activo != null);
            usuarioDAO.actualizarUsuario(usuario);
        }
        return "redirect:/verUsuarios";
    }

    @PostMapping("/verUsuarios/eliminar")
    public String eliminarUsuario(@RequestParam int idUsuario, Model model, Authentication authentication)
            throws Exception {
        usuarioDAO.eliminarUsuario(idUsuario);
        return "redirect:/verUsuarios";
    }

    @GetMapping("/tiposTrabajo")
    public String tiposTrabajo(Model model, Authentication authentication) throws Exception {
        obtenerDatosUsuario(model, authentication);
        ArrayList<Categoria> categorias = categoriaDAO.obtenerTodasCategorias();
        model.addAttribute("categorias", categorias);
        return "seccionesHome/tiposTrabajo";
    }

    @PostMapping("/tiposTrabajo/crear")
    public String crearTipoTrabajo(@RequestParam String nombre, @RequestParam String descripcion, Model model,
            Authentication authentication) throws Exception {
        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        categoria.setDescripcion(descripcion);
        categoriaDAO.insertarCategoria(categoria);
        return "redirect:/tiposTrabajo";
    }

    @PostMapping("/tiposTrabajo/editar")
    public String editarTipoTrabajo(@RequestParam int idCategoria, @RequestParam String nombre,
            @RequestParam String descripcion, Model model, Authentication authentication) throws Exception {
        Categoria categoria = categoriaDAO.obtenerPorId(idCategoria);
        if (categoria != null) {
            categoria.setNombre(nombre);
            categoria.setDescripcion(descripcion);
            categoriaDAO.actualizarCategoria(categoria);
        }
        return "redirect:/tiposTrabajo";
    }

    @PostMapping("/tiposTrabajo/eliminar")
    public String eliminarTipoTrabajo(@RequestParam int idCategoria, Model model, Authentication authentication)
            throws Exception {
        categoriaDAO.eliminarCategoria(idCategoria);
        return "redirect:/tiposTrabajo";
    }

    @GetMapping("/verEstadisticas")
    public String verEstadisticas(Model model, Authentication authentication) throws Exception {
        obtenerDatosUsuario(model, authentication);
        return "seccionesHome/verEstadisticas";
    }
}
