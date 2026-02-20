package ai.documentation.repos;

import ai.documentation.domain.Documentation;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DocumentationRepository extends JpaRepository<Documentation, Long> {

    Documentation findFirstByProjectId(Long id);

    Documentation findFirstByCreatedById(Long id);

}
