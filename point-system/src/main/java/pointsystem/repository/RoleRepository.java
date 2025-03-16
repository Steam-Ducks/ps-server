package pointsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pointsystem.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
}
