package es.duit.app.security;

import es.duit.app.entity.AppUser;
import es.duit.app.repository.AppUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    public CustomUserDetailsService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Validar username
        if (username == null || username.trim().isEmpty()) {
            throw new UsernameNotFoundException("El correo electrónico no puede estar vacío");
        }

        // Buscar usuario
        AppUser user = appUserRepository.findByUsername(username.trim())
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // Verificar usuario activo
        if (user.getActive() == null || !user.getActive()) {
            throw new UsernameNotFoundException("Usuario inactivo: " + username);
        }

        // Verificar que tenga rol
        if (user.getRole() == null) {
            throw new UsernameNotFoundException("Usuario sin rol asignado: " + username);
        }

        // Verificar rol activo
        if (user.getRole().getActive() == null || !user.getRole().getActive()) {
            throw new UsernameNotFoundException("Rol inactivo: " + username);
        }

        // Obtener nombre del rol
        String roleName = user.getRole().getName().name();

        // Crear UserDetails
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
