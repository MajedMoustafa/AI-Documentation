package ai.documentation.repos;

import ai.documentation.domain.Query;
import org.springframework.data.jpa.repository.JpaRepository;


public interface QueryRepository extends JpaRepository<Query, Long> {

    Query findFirstByDocumentationId(Long id);

    Query findFirstByCreatedById(Long id);

}
