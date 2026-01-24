package es.duit.app.repository;

import es.duit.app.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
	@Query("""
		    SELECT u
		    FROM AppUser u
		    JOIN FETCH u.role
		    WHERE u.username = :username
	""")
	Optional<AppUser> findByUsername(@Param("username") String username);
}
