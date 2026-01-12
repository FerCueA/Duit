// ====================
// CONTROLADOR PRINCIPAL DE LA APP (HomeController)
// ====================

package es.duit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.duit.dao.PostulacionDAO;
import es.duit.dao.TrabajoDAO;
import es.duit.dao.DireccionDAO;
import es.duit.dao.ProfesionalCategoriaDAO;

import es.duit.dao.RolDAO;
import es.duit.dao.UsuarioDAO;
import es.duit.dao.CategoriaDAO;
import es.duit.dao.PerfilProfesionalDAO;
import es.duit.dao.SolicitudDAO;
import es.duit.dao.ValoracionDAO;
import es.duit.models.Rol;
import es.duit.models.Usuario;
import es.duit.models.Categoria;
import es.duit.models.Solicitud;
import es.duit.models.PerfilProfesional;
import java.util.ArrayList;

@Controller
public class HomeController {
    // ==== INYECCIÓN DE DEPENDENCIAS (DAOs y servicios) ====

    @Autowired
    private UsuarioDAO usuarioDAO;
    @Autowired
    private RolDAO rolDAO;
    @Autowired
    private CategoriaDAO categoriaDAO;
    @Autowired
    private PerfilProfesionalDAO perfilProfesionalDAO;
    @Autowired
    private SolicitudDAO solicitudDAO;
    @Autowired
    private ValoracionDAO valoracionDAO;
    @Autowired
    private PostulacionDAO postulacionDAO;
    @Autowired
    private TrabajoDAO trabajoDAO;
    @Autowired
    private DireccionDAO direccionDAO;
    @Autowired
    private ProfesionalCategoriaDAO profesionalCategoriaDAO;

    // ==== MÉTODOS AUXILIARES ====

    private void obtenerDatosUsuario(Model model, Authentication authentication) throws Exception {
            // Añade el usuario autenticado al modelo para su uso en las vistas
        if (authentication != null) {
            Usuario usuario = usuarioDAO.obtenerUsuarioPorUsername(authentication.getName());
            if (usuario != null) {
                Rol rol = rolDAO.obtenerRolPorId(usuario.getIdRol());
                usuario.setRol(rol);
            }
            model.addAttribute("usuario", usuario);
        }
    }

    // ==== RUTAS DE NAVEGACIÓN PRINCIPAL ====
    @GetMapping("/home")
    public String home(Model model, Authentication authentication) throws Exception {
        obtenerDatosUsuario(model, authentication);
        return "home";
    }

    // ==== BÚSQUEDA Y VISUALIZACIÓN DE OFERTAS ====
    @GetMapping("/buscarOfertas")
    public String buscarOfertas(Model model, Authentication authentication) throws Exception {
        obtenerDatosUsuario(model, authentication);
        return "seccionesHome/buscarOfertas";
    }

    // ==== OFERTAS DEL USUARIO LOGUEADO ====
    @GetMapping("/misOfertas")
    public String misOfertas(Model model, Authentication authentication) throws Exception {
        obtenerDatosUsuario(model, authentication);
        return "seccionesHome/misOfertas";
    }

    @GetMapping("/anadirOferta")
    public String mostrarFormularioOferta(Model model, Authentication authentication) throws Exception {
        obtenerDatosUsuario(model, authentication);
        model.addAttribute("categorias", categoriaDAO.obtenerTodasCategorias());
        return "seccionesHome/anadirOferta";
    }

    // ==== CREACIÓN DE NUEVA OFERTA ====
    @PostMapping("/anadirOferta")
    public String guardarOferta(
            @RequestParam("titulo") String titulo,
            @RequestParam("fecha") String fecha,
            @RequestParam("tipo") int idCategoria,
            @RequestParam("descripcion") String descripcion,
            Authentication authentication,
            Model model) throws Exception {

        Usuario usuario = usuarioDAO.obtenerUsuarioPorUsername(authentication.getName());
        if (usuario == null) {
            return "redirect:/login";
        }
        Solicitud solicitud = new Solicitud();
        solicitud.setTitulo(titulo);
        solicitud.setDescripcion(descripcion);
        solicitud.setIdCategoria(idCategoria);
        solicitud.setIdCliente(usuario.getIdUsuario());
        solicitud.setFechaSolicitud(java.sql.Date.valueOf(fecha));
        solicitud.setEstado(Solicitud.EstadoSolicitud.ABIERTA);
        solicitudDAO.insertarSolicitud(solicitud);
        return "redirect:/misOfertas";
    }

    // ==== HISTORIAL DEL USUARIO ====
    @GetMapping("/consultarHistorial")
    public String consultarHistorial(Model model, Authentication authentication) throws Exception {
        obtenerDatosUsuario(model, authentication);
        return "seccionesHome/consultarHistorial";
    }

    // ==== SECCIÓN DE AYUDA ====
    @GetMapping("/ayuda")
    public String ayuda(Model model, Authentication authentication) throws Exception {
        obtenerDatosUsuario(model, authentication);
        return "seccionesHome/ayuda";
    }

    // ==== VALORACIONES DEL USUARIO ====
    @GetMapping("/verValoraciones")
    public String verValoraciones(Model model, Authentication authentication) throws Exception {
        obtenerDatosUsuario(model, authentication);

        return "seccionesHome/verValoraciones";
    }

    // ==== EDICIÓN DE PERFIL DE USUARIO ====
    @GetMapping("/editarPerfil")
    public String editarPerfil(Model model, Authentication authentication) throws Exception {
        obtenerDatosUsuario(model, authentication);

        return "seccionesHome/editarPerfil";
    }

    // ==== EDICIÓN DE PERFIL PROFESIONAL ====
    @GetMapping("/editarPerfilProfesional")
    public String editarPerfilProfesional(Model model, Authentication authentication) throws Exception {
        obtenerDatosUsuario(model, authentication);
        Usuario usuario = (Usuario) model.getAttribute("usuario");
        PerfilProfesional perfilProfesional = null;
        if (usuario != null) {
            perfilProfesional = perfilProfesionalDAO.obtenerPorId(usuario.getIdUsuario());
        }
        model.addAttribute("perfilProfesional", perfilProfesional);
        return "seccionesHome/editarPerfilProfesional";
    }

    // ==== ADMINISTRACIÓN DE USUARIOS (ADMIN) ====
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

    // ==== EDICIÓN DE USUARIO (ADMIN) ====
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

    // ==== ELIMINACIÓN DE USUARIO (ADMIN) ====
    @PostMapping("/verUsuarios/eliminar")
    public String eliminarUsuario(@RequestParam int idUsuario, Model model, Authentication authentication)
            throws Exception {
        usuarioDAO.eliminarUsuario(idUsuario);
        return "redirect:/verUsuarios";
    }

    // ==== GESTIÓN DE TIPOS DE TRABAJO (CATEGORÍAS) ====
    @GetMapping("/tiposTrabajo")
    public String tiposTrabajo(Model model, Authentication authentication) throws Exception {
        obtenerDatosUsuario(model, authentication);
        ArrayList<Categoria> categorias = categoriaDAO.obtenerTodasCategorias();
        model.addAttribute("categorias", categorias);
        return "seccionesHome/tiposTrabajo";
    }

    // ==== CREAR NUEVO TIPO DE TRABAJO ====
    @PostMapping("/tiposTrabajo/crear")
    public String crearTipoTrabajo(@RequestParam String nombre, @RequestParam String descripcion, Model model,
            Authentication authentication) throws Exception {
        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        categoria.setDescripcion(descripcion);
        categoriaDAO.insertarCategoria(categoria);
        return "redirect:/tiposTrabajo";
    }

    // ==== EDITAR TIPO DE TRABAJO ====
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

    // ==== ELIMINAR TIPO DE TRABAJO ====
    @PostMapping("/tiposTrabajo/eliminar")
    public String eliminarTipoTrabajo(@RequestParam int idCategoria, Model model, Authentication authentication)
            throws Exception {
        categoriaDAO.eliminarCategoria(idCategoria);
        return "redirect:/tiposTrabajo";
    }

    // ==== ESTADÍSTICAS ====
    @GetMapping("/verEstadisticas")
    public String verEstadisticas(Model model, Authentication authentication) throws Exception {
        obtenerDatosUsuario(model, authentication);
        return "seccionesHome/verEstadisticas";
    }
}
