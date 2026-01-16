package es.duit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;

import es.duit.dao.SolicitudDAO;
import es.duit.dao.CategoriaDAO;
import es.duit.models.Solicitud;
import es.duit.models.Categoria;
import es.duit.models.Usuario;
import es.duit.util.UsuarioModelHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class OfertaController {
    @Autowired
    private UsuarioModelHelper usuarioModelHelper;
    @Autowired
    private SolicitudDAO solicitudDAO;
    @Autowired
    private CategoriaDAO categoriaDAO;
    @Autowired
    private es.duit.dao.UsuarioDAO usuarioDAO;
    @Autowired
    private es.duit.dao.PostulacionDAO postulacionDAO;

    // Ver todas las ofertas
    @GetMapping("/buscarOfertas")
    public String buscarOfertas(Model model, Authentication auth) throws Exception {
        usuarioModelHelper.ponerUsuario(model, auth);
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

    // Formulario nueva oferta
    @GetMapping("/anadirOferta")
    public String anadirOferta(Model model, Authentication auth) throws Exception {
        usuarioModelHelper.ponerUsuario(model, auth);
        model.addAttribute("categorias", categoriaDAO.obtenerTodas());
        return "seccionesHome/anadirOferta";
    }

    // Guardar nueva oferta
    @PostMapping("/anadirOferta")
    public String guardarOferta(@RequestParam("titulo") String titulo,
            @RequestParam("fecha") String fecha,
            @RequestParam("tipo") int idCategoria,
            @RequestParam("descripcion") String descripcion,
            Authentication auth,
            Model model) throws Exception {
        usuarioModelHelper.ponerUsuario(model, auth);
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

    // Ver ofertas propias
    @GetMapping("/misOfertas")
    public String misOfertas(Model model, Authentication auth) throws Exception {
        usuarioModelHelper.ponerUsuario(model, auth);
        Usuario u = usuarioDAO.obtenerUsuarioPorUsername(auth.getName());
        ArrayList<Solicitud> misOfertas = new ArrayList<>();
        Map<Integer, List<es.duit.models.Postulacion>> postulacionesPorOferta = new HashMap<>();
        if (u != null) {
            misOfertas = solicitudDAO.obtenerSolicitudesPorCliente(u.getIdUsuario());
            for (Solicitud oferta : misOfertas) {
                List<es.duit.models.Postulacion> postulaciones = postulacionDAO
                        .obtenerPorSolicitud(oferta.getIdSolicitud());
                postulacionesPorOferta.put(oferta.getIdSolicitud(), postulaciones);
            }
        }
        model.addAttribute("ofertasPropias", misOfertas);
        model.addAttribute("postulacionesPorOferta", postulacionesPorOferta);
        return "seccionesHome/misOfertas";
    }
}
