package ai.documentation.repos;

import ai.documentation.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    UserRole findFirstByUserId(Long id);

    UserRole findFirstByRoleId(Long id);

}
