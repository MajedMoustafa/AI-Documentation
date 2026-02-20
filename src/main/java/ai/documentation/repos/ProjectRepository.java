package ai.documentation.repos;

import ai.documentation.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProjectRepository extends JpaRepository<Project, Long> {

    Project findFirstByCreatedById(Long id);

}
