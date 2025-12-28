package es.duit.connections;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;

import es.duit.dao.RolDAO;
import es.duit.dao.UsuarioDAO;
import es.duit.models.Rol;
import es.duit.models.Usuario;


// Utilizamos esta clase auxiliar para conectar la BD con el proyecto
@Service
public class UsuarioBD implements UserDetailsService {

    /**
     * Esta clase es la conectora de la base de datos con la aplicacion para
     * verificar los datos de la base de datos
     * El metodo loadUserByUsername busca el usuario en la base de datos y obtiene
     * sus roles
     * ese metodo lo trae la implementacion de UserDetailsService de Spring Security
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            // Usamos UsuarioDAO para buscar el usuario en la BD por teléfono
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            Usuario usuario = usuarioDAO.obtenerUsuarioPorUsername(username);

            // Si no existe el usuario, lanzamos excepción
            if (usuario == null) {
                throw new UsernameNotFoundException("Usuario no encontrado con el teléfono: " + username);
            }

             if (!usuario.isActivo()) {
            throw new UsernameNotFoundException("Usuario desactivado");
            }


            // Obtener SOLO el rol del usuario
            RolDAO rolDAO = new RolDAO();
            Rol rol = rolDAO.obtenerRolPorId(usuario.getIdRol());

            if (rol == null) {
                throw new UsernameNotFoundException("Rol no encontrado para el usuario: " + username);
            }

            // Spring Security espera roles SIN "ROLE_"
            String rolNombre = rol.getNombre().toUpperCase();


            // Convertimos el objeto Usuario al formato que Spring Security espera
            return User.withUsername(usuario.getUsername())
                    .password(usuario.getPassword())
                    .roles(rolNombre)
                    .build();

        } catch (Exception e) {
            throw new UsernameNotFoundException("Error al buscar usuario: " + e.getMessage());
        }
    }

}
