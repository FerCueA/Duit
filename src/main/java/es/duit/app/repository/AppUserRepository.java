package es.duit.app.repository;

import es.duit.app.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

	// Buscar usuario por nombre de usuario
	List<AppUser> findByUsername(String username);

	// Buscar usuario por DNI
	List<AppUser> findByDni(String dni);
}
