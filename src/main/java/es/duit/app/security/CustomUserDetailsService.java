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
        if (username == null || username.trim().isEmpty()) {
            throw new UsernameNotFoundException("El nombre de usuario no puede estar vacío");
        }

        AppUser user = appUserRepository.findByUsername(username.trim())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // Verificar que el usuario esté activo
        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new UsernameNotFoundException("Usuario inactivo: " + username);
        }

        // Verificar que tenga rol asignado
        if (user.getRole() == null) {
            throw new UsernameNotFoundException("Usuario sin rol asignado: " + username);
        }

        // Verificar que el rol esté activo
        if (!Boolean.TRUE.equals(user.getRole().getActive())) {
            throw new UsernameNotFoundException("Rol inactivo: " + username);
        }

        String roleName = user.getRole().getName();
        
        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(roleName) // Spring Security agregará automáticamente "ROLE_" como prefijo
                .accountExpired(false)
                .accountLocked(!user.getActive())
                .credentialsExpired(false)
                .disabled(!user.getActive())
                .build();
    }
}
