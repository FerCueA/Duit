package es.duit.app.repository;

import es.duit.app.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

	// Buscar usuario por nombre de usuario
	Optional<AppUser> findByUsername(String username);

	// Buscar usuario por DNI
	Optional<AppUser> findByDni(String dni);

	// Buscar usuario por token de activación
	Optional<AppUser> findByActivationToken(String activationToken);
}
