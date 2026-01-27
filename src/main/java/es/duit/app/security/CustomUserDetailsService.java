package es.duit.app.security;

import es.duit.app.entity.AppUser;
import es.duit.app.repository.AppUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    public CustomUserDetailsService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Paso 1: Validar que el username no esté vacío
        if (username == null || username.trim().isEmpty()) {
            throw new UsernameNotFoundException("El correo electrónico no puede estar vacío");
        }

        // Paso 2: Buscar usuario en la base de datos
        List<AppUser> usuarios = appUserRepository.findByUsername(username.trim());
        if (usuarios.isEmpty()) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }
        AppUser user = usuarios.get(0);

        // Paso 3: Verificar que el usuario esté activo
        if (user.getActive() == null || !user.getActive()) {
            throw new UsernameNotFoundException("Usuario inactivo: " + username);
        }

        // Paso 4: Verificar que tenga rol
        if (user.getRole() == null) {
            throw new UsernameNotFoundException("Usuario sin rol asignado: " + username);
        }

        // Paso 5: Verificar que el rol esté activo
        if (user.getRole().getActive() == null || !user.getRole().getActive()) {
            throw new UsernameNotFoundException("Rol inactivo: " + username);
        }

        // Paso 6: Obtener el nombre del rol
        String roleName = user.getRole().getName().name();

        // Paso 7: Crear y retornar UserDetails
        UserDetails userDetails = User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(roleName)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();

        return userDetails;
    }
}
