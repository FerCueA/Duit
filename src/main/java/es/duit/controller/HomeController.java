
package es.duit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;

import es.duit.dao.UsuarioDAO;
import es.duit.dao.RolDAO;
import es.duit.dao.CategoriaDAO;
import es.duit.dao.PerfilProfesionalDAO;
import es.duit.dao.SolicitudDAO;
import es.duit.dao.PostulacionDAO;
import es.duit.dao.TrabajoDAO;
import es.duit.models.Usuario;
import es.duit.models.Rol;
import es.duit.models.Categoria;
import es.duit.models.PerfilProfesional;
import es.duit.models.Solicitud;
import es.duit.models.Postulacion;
import es.duit.models.Trabajo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

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
    private PostulacionDAO postulacionDAO;
    @Autowired
    private TrabajoDAO trabajoDAO;

    // METODO PARA OBTENER USUARIO LOGUEADO
    private void ponerUsuario(Model model, Authentication auth) {
        if (auth != null) {
            Usuario u = usuarioDAO.obtenerUsuarioPorUsername(auth.getName());
            if (u != null) {
                try {
                    Rol r = rolDAO.obtenerRolPorId(u.getIdRol());
                    u.setRol(r);
                } catch (Exception e) {
                    // Si hay error, no se pone el rol
                }
            }
            model.addAttribute("usuario", u);
        }
    }

    // HOME
    @GetMapping("/home")
    public String home(Model model, Authentication auth) throws Exception {
        ponerUsuario(model, auth);
        return "home";
    }

    // BUSCAR OFERTAS
    @GetMapping("/buscarOfertas")
    public String buscarOfertas(Model model, Authentication auth) throws Exception {
        ponerUsuario(model, auth);
        ArrayList<Solicitud> ofertas = solicitudDAO.obtenerTodas();
        ArrayList<Categoria> categorias = categoriaDAO.obtenerTodas();
        for (Solicitud o : ofertas) {
            for (Categoria c : categorias) {
                if (o.getIdCategoria() == c.getIdCategoria()) {
                    o.setCategoria(c);
                }
            }
        }
        model.addAttribute("ofertas", ofertas);
        model.addAttribute("categorias", categorias);
        return "seccionesHome/buscarOfertas";
    }

    // FINALIZAR TRABAJO
    @PostMapping("/finalizarTrabajo")
    public String finalizarTrabajo(@RequestParam("idTrabajo") int idTrabajo) throws Exception {
        Trabajo t = trabajoDAO.obtenerPorId(idTrabajo);
        if (t != null && t.getEstado() == Trabajo.EstadoTrabajo.EN_PROCESO) {
            t.setEstado(Trabajo.EstadoTrabajo.FINALIZADO);
            t.setFechaFin(new java.util.Date());
            trabajoDAO.actualizarTrabajo(t);
        }
        return "redirect:/consultarHistorial";
    }

    // CANCELAR TRABAJO
    @PostMapping("/cancelarTrabajo")
    public String cancelarTrabajo(@RequestParam("idTrabajo") int idTrabajo) throws Exception {
        Trabajo t = trabajoDAO.obtenerPorId(idTrabajo);
        if (t != null && t.getEstado() == Trabajo.EstadoTrabajo.EN_PROCESO) {
            t.setEstado(Trabajo.EstadoTrabajo.CANCELADO);
            t.setFechaFin(new java.util.Date());
            trabajoDAO.actualizarTrabajo(t);
        }
        return "redirect:/consultarHistorial";
    }

    // MIS OFERTAS
    @GetMapping("/misOfertas")
    public String misOfertas(Model model, Authentication auth) throws Exception {
        ponerUsuario(model, auth);
        Usuario u = usuarioDAO.obtenerUsuarioPorUsername(auth.getName());
        ArrayList<Solicitud> misOfertas = new ArrayList<>();
        Map<Integer, List<Postulacion>> postulacionesPorOferta = new HashMap<>();
        if (u != null) {
            misOfertas = solicitudDAO.obtenerSolicitudesPorCliente(u.getIdUsuario());
            for (Solicitud oferta : misOfertas) {
                List<Postulacion> postulaciones = postulacionDAO.obtenerPorSolicitud(oferta.getIdSolicitud());
                postulacionesPorOferta.put(oferta.getIdSolicitud(), postulaciones);
            }
        }
        model.addAttribute("ofertasPropias", misOfertas);
        model.addAttribute("postulacionesPorOferta", postulacionesPorOferta);
        return "seccionesHome/misOfertas";
    }

    // FORMULARIO NUEVA OFERTA
    @GetMapping("/anadirOferta")
    public String anadirOferta(Model model, Authentication auth) throws Exception {
        ponerUsuario(model, auth);
        model.addAttribute("categorias", categoriaDAO.obtenerTodas());
        return "seccionesHome/anadirOferta";
    }

    // POSTULACIONES
    @GetMapping("/postulaciones")
    public String postulaciones(Model model, Authentication auth) throws Exception {
        ponerUsuario(model, auth);
        Usuario u = usuarioDAO.obtenerUsuarioPorUsername(auth.getName());
        if (u != null) {
            ArrayList<Postulacion> todas = postulacionDAO.obtenerTodas();
            ArrayList<Postulacion> mias = new ArrayList<>();
            for (Postulacion p : todas) {
                if (p.getIdProfesional() == u.getIdUsuario()) {
                    mias.add(p);
                }
            }
            model.addAttribute("postulaciones", mias);
        }
        return "seccionesHome/postulaciones";
    }

    // CANCELAR POSTULACION
    @PostMapping("/rechazarPostulacion")
    public String rechazarPostulacion(@RequestParam("idPostulacion") int idPostulacion) throws Exception {
        postulacionDAO.rechazarPostulacion(idPostulacion);
        return "redirect:/misOfertas";
    }

    // CONFIRMAR POSTULACION
    @PostMapping("/confirmarPostulacion")
    public String confirmarPostulacion(@RequestParam("idPostulacion") int idPostulacion) throws Exception {
        postulacionDAO.aceptarPostulacion(idPostulacion);
        Postulacion p = postulacionDAO.obtenerPorId(idPostulacion);
        Solicitud s = solicitudDAO.obtenerPorId(p.getIdSolicitud());
        s.setEstado(Solicitud.EstadoSolicitud.CERRADA);
        solicitudDAO.actualizarSolicitud(s);
        Trabajo t = new Trabajo();
        t.setIdPostulacion(idPostulacion);
        t.setPrecioAcordado(p.getPrecioPropuesto());
        t.setFechaInicio(new java.util.Date());
        t.setEstado(Trabajo.EstadoTrabajo.EN_PROCESO);
        trabajoDAO.insertarTrabajo(t);
        return "redirect:/misOfertas";
    }

    // GUARDAR NUEVA OFERTA
    @PostMapping("/anadirOferta")
    public String guardarOferta(@RequestParam("titulo") String titulo,
                                @RequestParam("fecha") String fecha,
                                @RequestParam("tipo") int idCategoria,
                                @RequestParam("descripcion") String descripcion,
                                Authentication auth,
                                Model model) throws Exception {
        Usuario u = usuarioDAO.obtenerUsuarioPorUsername(auth.getName());
        if (u == null) {
            return "redirect:/login";
        }
        Solicitud s = new Solicitud();
        s.setTitulo(titulo);
        s.setDescripcion(descripcion);
        s.setIdCategoria(idCategoria);
        s.setIdCliente(u.getIdUsuario());
        s.setFechaSolicitud(java.sql.Date.valueOf(fecha));
        s.setEstado(Solicitud.EstadoSolicitud.ABIERTA);
        solicitudDAO.insertarSolicitud(s);
        return "redirect:/misOfertas";
    }

    // POSTULARSE
    @PostMapping("/postularse")
    public String postularse(@RequestParam("idSolicitud") int idSolicitud,
                             @RequestParam("mensaje") String mensaje,
                             @RequestParam("precioPropuesto") double precioPropuesto,
                             Authentication auth,
                             Model model) throws Exception {
        Usuario u = usuarioDAO.obtenerUsuarioPorUsername(auth.getName());
        if (u == null) {
            return "redirect:/login";
        }
        Postulacion p = new Postulacion();
        p.setIdSolicitud(idSolicitud);
        p.setIdProfesional(u.getIdUsuario());
        p.setMensaje(mensaje);
        p.setPrecioPropuesto(precioPropuesto);
        p.setEstado(Postulacion.EstadoPostulacion.PENDIENTE);
        postulacionDAO.insertarPostulacion(p);
        return "redirect:/postulaciones";
    }

    // HISTORIAL
    @GetMapping("/consultarHistorial")
    public String consultarHistorial(Model model, Authentication auth) throws Exception {
        ponerUsuario(model, auth);
        Usuario u = usuarioDAO.obtenerUsuarioPorUsername(auth.getName());
        ArrayList<Trabajo> trabajos = new ArrayList<>();
        if (u != null) {
            ArrayList<Postulacion> postulaciones = postulacionDAO.obtenerTodas();
            for (Postulacion p : postulaciones) {
                if (p.getIdProfesional() == u.getIdUsuario()) {
                    for (Trabajo t : trabajoDAO.obtenerTodos()) {
                        if (t.getIdPostulacion() == p.getIdPostulacion()) {
                            trabajos.add(t);
                        }
                    }
                }
            }
            ArrayList<Solicitud> solicitudes = solicitudDAO.obtenerSolicitudesPorCliente(u.getIdUsuario());
            for (Solicitud s : solicitudes) {
                for (Trabajo t : trabajoDAO.obtenerTrabajosPorSolicitud(s.getIdSolicitud())) {
                    if (!trabajos.contains(t)) {
                        trabajos.add(t);
                    }
                }
            }
        }
        ArrayList<Trabajo> enProceso = new ArrayList<>();
        ArrayList<Trabajo> finalizados = new ArrayList<>();
        ArrayList<Trabajo> cancelados = new ArrayList<>();
        for (Trabajo t : trabajos) {
            if (t.getEstado() == Trabajo.EstadoTrabajo.EN_PROCESO) {
                enProceso.add(t);
            } else if (t.getEstado() == Trabajo.EstadoTrabajo.FINALIZADO) {
                finalizados.add(t);
            } else if (t.getEstado() == Trabajo.EstadoTrabajo.CANCELADO) {
                cancelados.add(t);
            }
        }
        model.addAttribute("trabajosEnProceso", enProceso);
        model.addAttribute("trabajosFinalizados", finalizados);
        model.addAttribute("trabajosCancelados", cancelados);
        return "seccionesHome/consultarHistorial";
    }

    // AYUDA
    @GetMapping("/ayuda")
    public String ayuda(Model model, Authentication auth) throws Exception {
        ponerUsuario(model, auth);
        return "seccionesHome/ayuda";
    }

    // VALORACIONES
    @GetMapping("/verValoraciones")
    public String verValoraciones(Model model, Authentication auth) throws Exception {
        ponerUsuario(model, auth);
        return "seccionesHome/verValoraciones";
    }

    // EDITAR PERFIL
    @GetMapping("/editarPerfil")
    public String editarPerfil(Model model, Authentication auth) throws Exception {
        ponerUsuario(model, auth);
        return "seccionesHome/editarPerfil";
    }

    // EDITAR PERFIL PROFESIONAL
    @GetMapping("/editarPerfilProfesional")
    public String editarPerfilProfesional(Model model, Authentication auth) throws Exception {
        ponerUsuario(model, auth);
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

    // VER USUARIOS (ADMIN)
    @GetMapping("/verUsuarios")
    public String verUsuarios(@RequestParam(value = "filtro", required = false) String filtro, Model model, Authentication auth) throws Exception {
        ponerUsuario(model, auth);
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

    // EDITAR USUARIO (ADMIN)
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

    // ELIMINAR USUARIO (ADMIN)
    @PostMapping("/verUsuarios/eliminar")
    public String eliminarUsuario(@RequestParam int idUsuario, Model model, Authentication auth) throws Exception {
        usuarioDAO.eliminarUsuario(idUsuario);
        return "redirect:/verUsuarios";
    }

    // TIPOS DE TRABAJO
    @GetMapping("/tiposTrabajo")
    public String tiposTrabajo(Model model, Authentication auth) throws Exception {
        ponerUsuario(model, auth);
        ArrayList<Categoria> categorias = categoriaDAO.obtenerTodas();
        model.addAttribute("categorias", categorias);
        return "seccionesHome/tiposTrabajo";
    }

    // CREAR TIPO DE TRABAJO
    @PostMapping("/tiposTrabajo/crear")
    public String crearTipoTrabajo(@RequestParam String nombre, @RequestParam String descripcion, Model model, Authentication auth) throws Exception {
        Categoria c = new Categoria();
        c.setNombre(nombre);
        c.setDescripcion(descripcion);
        categoriaDAO.insertarCategoria(c);
        return "redirect:/tiposTrabajo";
    }

    // EDITAR TIPO DE TRABAJO
    @PostMapping("/tiposTrabajo/editar")
    public String editarTipoTrabajo(@RequestParam int idCategoria, @RequestParam String nombre, @RequestParam String descripcion, Model model, Authentication auth) throws Exception {
        Categoria c = categoriaDAO.obtenerPorId(idCategoria);
        if (c != null) {
            c.setNombre(nombre);
            c.setDescripcion(descripcion);
            categoriaDAO.actualizarCategoria(c);
        }
        return "redirect:/tiposTrabajo";
    }

    // ELIMINAR TIPO DE TRABAJO
    @PostMapping("/tiposTrabajo/eliminar")
    public String eliminarTipoTrabajo(@RequestParam int idCategoria, Model model, Authentication auth) throws Exception {
        categoriaDAO.eliminarCategoria(idCategoria);
        return "redirect:/tiposTrabajo";
    }

    // ESTADISTICAS
    @GetMapping("/verEstadisticas")
    public String verEstadisticas(Model model, Authentication auth) throws Exception {
        ponerUsuario(model, auth);
        return "seccionesHome/verEstadisticas";
    }

}
