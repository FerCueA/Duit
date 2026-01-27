package es.duit.app.repository;

import es.duit.app.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<UserRole, Long> {

    List<UserRole> findByName(UserRole.RoleName name);
}
