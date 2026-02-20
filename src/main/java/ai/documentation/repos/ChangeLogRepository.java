package ai.documentation.repos;

import ai.documentation.domain.ChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long> {

    ChangeLog findFirstByDocumentationId(Long id);

    ChangeLog findFirstByCreatedById(Long id);

}
