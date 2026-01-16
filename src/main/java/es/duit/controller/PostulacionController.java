package es.duit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;

import es.duit.dao.PostulacionDAO;
import es.duit.dao.SolicitudDAO;
import es.duit.models.Postulacion;
import es.duit.models.Solicitud;
import es.duit.models.Usuario;
import es.duit.util.UsuarioModelHelper;

import java.util.ArrayList;

@Controller
public class PostulacionController {
    @Autowired
    private UsuarioModelHelper usuarioModelHelper;
    @Autowired
    private PostulacionDAO postulacionDAO;
    @Autowired
    private SolicitudDAO solicitudDAO;
    @Autowired
    private es.duit.dao.UsuarioDAO usuarioDAO;

    // Ver postulaciones del usuario
    @GetMapping("/postulaciones")
    public String postulaciones(Model model, Authentication auth) throws Exception {
        usuarioModelHelper.ponerUsuario(model, auth);
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

    // Cancelar postulación
    @PostMapping("/rechazarPostulacion")
    public String rechazarPostulacion(@RequestParam("idPostulacion") int idPostulacion) throws Exception {
        postulacionDAO.rechazarPostulacion(idPostulacion);
        return "redirect:/misOfertas";
    }

    // Confirmar postulación
    @PostMapping("/confirmarPostulacion")
    public String confirmarPostulacion(@RequestParam("idPostulacion") int idPostulacion) throws Exception {
        postulacionDAO.aceptarPostulacion(idPostulacion);
        Postulacion p = postulacionDAO.obtenerPorId(idPostulacion);
        Solicitud s = solicitudDAO.obtenerPorId(p.getIdSolicitud());
        s.setEstado(Solicitud.EstadoSolicitud.CERRADA);
        solicitudDAO.actualizarSolicitud(s);

        return "redirect:/misOfertas";
    }
}
