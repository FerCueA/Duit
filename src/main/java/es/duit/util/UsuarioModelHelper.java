package es.duit.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;

import es.duit.dao.UsuarioDAO;
import es.duit.dao.RolDAO;
import es.duit.models.Usuario;
import es.duit.models.Rol;

@Component
public class UsuarioModelHelper {
    @Autowired
    private UsuarioDAO usuarioDAO;
    @Autowired
    private RolDAO rolDAO;

    public void ponerUsuario(Model model, Authentication auth) {
        if (auth != null) {
            Usuario u = usuarioDAO.obtenerUsuarioPorUsername(auth.getName());
            if (u != null) {
                try {
                    Rol r = rolDAO.obtenerRolPorId(u.getIdRol());
                    u.setRol(r);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            model.addAttribute("usuario", u);
        }
    }
}
